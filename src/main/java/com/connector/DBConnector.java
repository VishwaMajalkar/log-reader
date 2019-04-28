package com.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@Component
public class DBConnector {
    private static final Logger log = LoggerFactory.getLogger(DBConnector.class);

    Connection conn;
    private static String databaseUrl = "//localhost:3306/MySQL?useSSL=false&allowPublicKeyRetrieval=true";
    private static String databaseUser = "root";
    private static String databasePwd = "admin";
    Statement st = null;

    public void openDatabaseConnection() throws Exception {
        log.info("Establishing Database Connection");
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql:" + databaseUrl, databaseUser, databasePwd);
        st = conn.createStatement();
        conn.setAutoCommit(false);
        conn.commit();
        log.info("Database Connection Established");
    }

    public void closeDatabaseConnection() throws Exception{
        log.info("Closing Database Connection");
        conn.close();
        log.info("Database Connection Closed");
    }

    public void checkDatabaseRecordCount() throws Exception {
        log.info("Checking Database Record Count");
        Statement st = conn.createStatement();
        conn.commit();
        ResultSet r = st.executeQuery("SELECT COUNT(*) FROM event");
        r.next();
        log.info("object count in db " + r.getString(1));
    }

    public void createEventTable() throws Exception{
        st.execute("drop table if exists MySQL.event;");
        st.execute("SET GLOBAL binlog_format = 'ROW';");
        st.execute("SET GLOBAL TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;");
        st.execute("SET GLOBAL concurrent_insert = 2;");
        String sql = "CREATE TABLE EVENT " +
                "(id VARCHAR(50), " +
                " state VARCHAR(50), " +
                " timestamp INTEGER, " +
                " type VARCHAR(50), " +
                " host VARCHAR(50), " +
                " alert BOOLEAN)";
        executeStatement(sql);
    }

    public void executeStatement(String expression) throws Exception{
        st.execute(expression);
        conn.commit();
    }
}
