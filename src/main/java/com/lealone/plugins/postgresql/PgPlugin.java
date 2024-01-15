/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql;

import java.util.Map;

import com.lealone.common.logging.Logger;
import com.lealone.common.logging.LoggerFactory;
import com.lealone.db.PluginBase;
import com.lealone.db.PluginManager;
import com.lealone.plugins.postgresql.server.PgServerEngine;
import com.lealone.plugins.postgresql.sql.PgSQLEngine;
import com.lealone.server.ProtocolServer;
import com.lealone.server.ProtocolServerEngine;
import com.lealone.sql.SQLEngine;

public class PgPlugin extends PluginBase {

    private static final Logger logger = LoggerFactory.getLogger(PgPlugin.class);

    public static final String NAME = "PostgreSQL";

    public PgPlugin() {
        super(NAME);
    }

    @Override
    public void init(Map<String, String> config) {
        if (isInited())
            return;
        super.init(config);
        SQLEngine s = new PgSQLEngine();
        s.init(config); // 内部会自己注册
        // PluginManager.register(SQLEngine.class, s, getName());

        ProtocolServerEngine p = new PgServerEngine();
        p.init(config);
        // PluginManager.register(ProtocolServerEngine.class, p, getName());
    }

    @Override
    public void close() {
        ProtocolServerEngine p = getProtocolServerEngine();
        if (p != null)
            PluginManager.deregister(ProtocolServerEngine.class, p);

        SQLEngine s = PluginManager.getPlugin(SQLEngine.class, getName());
        if (s != null)
            PluginManager.deregister(SQLEngine.class, s);
        super.close();
    }

    @Override
    public void start() {
        if (isStarted())
            return;
        ProtocolServerEngine p = getProtocolServerEngine();
        if (p != null) {
            p.start();
            ProtocolServer ps = p.getProtocolServer();
            logger.info(ps.getName() + " started, host: {}, port: {}", ps.getHost(), ps.getPort());
        }
        super.start();
    }

    @Override
    public void stop() {
        if (isStopped())
            return;
        ProtocolServerEngine p = getProtocolServerEngine();
        if (p != null) {
            p.stop();
            ProtocolServer ps = p.getProtocolServer();
            logger.info(ps.getName() + " stopped");
        }
        super.stop();
    }

    private ProtocolServerEngine getProtocolServerEngine() {
        return PluginManager.getPlugin(ProtocolServerEngine.class, getName());
    }
}
