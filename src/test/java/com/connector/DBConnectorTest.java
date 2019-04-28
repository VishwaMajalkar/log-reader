package com.connector;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBConnectorTest {

    private static final Logger log = LoggerFactory.getLogger(DBConnectorTest.class);

    @InjectMocks
    DBConnector dbConnector;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init()throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MySQL?useSSL=false&allowPublicKeyRetrieval=true", "root", "admin");
        conn.setAutoCommit(false);
        Statement st = conn.createStatement();
        Field connField = ReflectionUtils.findField(DBConnector.class, "conn");
        connField.setAccessible(true);
        ReflectionUtils.setField(connField, dbConnector, conn);
        Field statement = ReflectionUtils.findField(DBConnector.class, "st");
        statement.setAccessible(true);
        ReflectionUtils.setField(statement, dbConnector, st);
    }

    @Test
    public void test1OpenDatabaseConnection() throws Exception {
        dbConnector.openDatabaseConnection();
        log.info("Connection Established Successfully!");
    }

    @Test
    public void test2CreateEventTable() throws Exception {
        dbConnector.createEventTable();
        log.info("Table created Successfully!");
    }

    @Test
    public void test3CheckDatabaseRecordCount() throws Exception {
        dbConnector.checkDatabaseRecordCount();
        log.info("Record Count Verified Successfully!");
    }

    @Test
    public void test4CloseDatabaseConnection() throws Exception {
        dbConnector.closeDatabaseConnection();
        log.info("Connection Closed Successfully!");
    }
}
