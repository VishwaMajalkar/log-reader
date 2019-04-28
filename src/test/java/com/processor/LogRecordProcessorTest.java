package com.processor;

import com.connector.DBConnector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogRecordProcessorTest {

    private static final Logger log = LoggerFactory.getLogger(LogRecordProcessorTest.class);

    @InjectMocks
    LogRecordProcessor logRecordProcessor;

    @Mock
    DBConnector dbConnector;

    @Mock
    CsvFileProcessor csvFileProcessor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init()throws Exception{
        ThreadPoolExecutor executor = new ThreadPoolExecutor(32, 32, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Field field = ReflectionUtils.findField(LogRecordProcessor.class, "executor");
        field.setAccessible(true);
        ReflectionUtils.setField(field, logRecordProcessor, executor);
    }

    @Test
    public void testRunLogRecordProcessor() throws Exception {
        logRecordProcessor.run("bulk_record.json");
        log.info("Data Loaded Successfully!");
        Mockito.verify(dbConnector).openDatabaseConnection();
        Mockito.verify(dbConnector).closeDatabaseConnection();
    }

    @Test
    public void testReadInputJsonFileEvents() throws Exception {
        File inFile = new File("bulk_record.json");
        logRecordProcessor.readInputJsonFileEvents(inFile);
        Mockito.verifyZeroInteractions(dbConnector);
    }
}
