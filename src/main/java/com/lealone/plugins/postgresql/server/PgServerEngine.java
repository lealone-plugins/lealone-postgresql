/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql.server;

import com.lealone.plugins.postgresql.PgPlugin;
import com.lealone.server.ProtocolServer;
import com.lealone.server.ProtocolServerEngineBase;

public class PgServerEngine extends ProtocolServerEngineBase {

    public PgServerEngine() {
        super(PgPlugin.NAME);
    }

    @Override
    protected ProtocolServer createProtocolServer() {
        return new PgServer();
    }
}
