/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql.server;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Properties;

import com.lealone.db.ConnectionInfo;
import com.lealone.db.Constants;
import com.lealone.db.plugin.PluginManager;
import com.lealone.db.scheduler.Scheduler;
import com.lealone.db.session.ServerSession;
import com.lealone.net.NetBuffer;
import com.lealone.net.WritableChannel;
import com.lealone.plugins.postgresql.PgPlugin;
import com.lealone.plugins.postgresql.server.handler.AuthPacketHandler;
import com.lealone.plugins.postgresql.server.handler.CommandPacketHandler;
import com.lealone.plugins.postgresql.server.handler.PacketHandler;
import com.lealone.plugins.postgresql.server.io.NetBufferOutput;
import com.lealone.plugins.postgresql.sql.PgAlias;
import com.lealone.server.AsyncServerConnection;
import com.lealone.server.scheduler.ServerSessionInfo;
import com.lealone.sql.SQLEngine;

public class PgServerConnection extends AsyncServerConnection {

    private final PgServer server;
    private ServerSession session;
    private boolean stop;
    private boolean initDone;
    private int processId;
    private PacketHandler packetHandler;

    private final NetBufferOutput out;

    protected PgServerConnection(PgServer server, WritableChannel writableChannel, Scheduler scheduler) {
        super(writableChannel, scheduler);
        this.server = server;
        out = new NetBufferOutput(getWritableChannel(), scheduler.getOutputBuffer());
        // 需要先认证，然后再切换到CommandPacketHandler
        packetHandler = new AuthPacketHandler(server, this);
    }

    public NetBufferOutput getOut() {
        return out;
    }

    public void setProcessId(int id) {
        this.processId = id;
    }

    public int getProcessId() {
        return processId;
    }

    @Override
    public void closeSession(ServerSessionInfo si) {
    }

    @Override
    public int getSessionCount() {
        return 1;
    }

    public void initDone() {
        initDone = true;
    }

    public void stop() {
        stop = true;
    }

    public void createSession(String databaseName, String userName, String password)
            throws SQLException {
        Properties info = new Properties();
        info.put("MODE", PgPlugin.NAME);
        info.put("USER", userName);
        info.put("PASSWORD", password);
        info.put("DEFAULT_SQL_ENGINE", PgPlugin.NAME);
        String url = Constants.URL_PREFIX + Constants.URL_TCP + server.getHost() + ":" + server.getPort()
                + "/" + databaseName;
        ConnectionInfo ci = new ConnectionInfo(url, info);
        ci.setRemote(false);
        session = (ServerSession) ci.createSession();
        ServerSessionInfo si = new ServerSessionInfo(scheduler, this, session, -1, -1);
        scheduler.addSessionInfo(si);
        session.setSQLEngine(PluginManager.getPlugin(SQLEngine.class, PgPlugin.NAME));
        session.setVersion(PgAlias.getVersion());

        packetHandler.setSession(session, si); // 旧的设置一次
        packetHandler = new CommandPacketHandler(server, this);
        packetHandler.setSession(session, si); // 新的再设置一次

        server.createBuiltInSchemas(session);
    }

    @Override
    public void close() {
        if (session == null)
            return;
        try {
            stop = true;
            session.close();
            server.trace("Close");
            super.close();
        } catch (Exception e) {
            server.traceError(e);
        }
        session = null;
        server.removeConnection(this);
    }

    @Override
    public int getPacketLengthByteCount() {
        if (initDone)
            return 5;
        else
            return 4;
    }

    @Override
    public int getPacketLength(ByteBuffer buffer) {
        int len;
        if (initDone) {
            len = buffer.getInt(buffer.position() + 1);
        } else {
            len = buffer.getInt();
        }
        return len - 4;
    }

    @Override
    public void handle(NetBuffer buffer, boolean autoRecycle) {
        if (stop)
            return;
        try {
            int x;
            if (initDone) {
                x = buffer.getUnsignedByte();
                buffer.position(buffer.position() + 4);
                if (x < 0) {
                    close();
                    return;
                }
            } else {
                x = 0;
            }
            packetHandler.handle(buffer, x);
        } finally {
            if (autoRecycle)
                buffer.recycle();
        }
    }
}
