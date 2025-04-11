/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package com.lealone.plugins.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.lealone.common.exceptions.DbException;
import com.lealone.db.LealoneDatabase;
import com.lealone.test.TestBase;
import com.lealone.test.sql.SqlTestBase;

public class PgTestBase extends SqlTestBase {

    public final static int TEST_PORT = 9510;
    public final static String NAME = PgPlugin.NAME;

    @BeforeClass
    public static void createPgPlugin() throws Exception {
        TestBase test = new TestBase();
        Connection conn = test.getConnection(LealoneDatabase.NAME);
        Statement stmt = conn.createStatement();
        // File classPath = new File("target/classes");
        String sql = "create plugin if not exists " + NAME //
                + " implement by '" + PgPlugin.class.getName() + "'" //
                // + " class path '" + classPath.getCanonicalPath() + "'" //
                + " parameters(port=" + TEST_PORT + ", host='127.0.0.1')";
        stmt.executeUpdate(sql);
        stmt.executeUpdate("start plugin " + NAME);
        stmt.close();
        conn.close();
    }

    @AfterClass
    public static void dropPgPlugin() throws Exception {
        TestBase test = new TestBase();
        Connection conn = test.getConnection(LealoneDatabase.NAME);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("stop plugin " + NAME);
        stmt.executeUpdate("drop plugin " + NAME);
        stmt.close();
        conn.close();
    }

    @Before
    @Override
    public void setUpBefore() {
        try {
            conn = getPgConnection();
            stmt = conn.createStatement();
        } catch (Exception e) {
            throw DbException.convert(e);
        }
    }

    public static Connection getPgConnection() throws Exception {
        String url = "jdbc:postgresql://localhost:" + TEST_PORT + "/postgres";
        Properties info = new Properties();
        info.put("user", "postgres");
        info.put("password", "postgres");
        return DriverManager.getConnection(url, info);
    }

    public static void sqlException(SQLException e) {
        while (e != null) {
            System.err.println("SQLException:" + e);
            System.err.println("-----------------------------------");
            System.err.println("Message  : " + e.getMessage());
            System.err.println("SQLState : " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            System.err.println();
            System.err.println();
            e = e.getNextException();
        }
    }
}
