package com.processor;

import com.beans.ServerLogEvent;
import com.connector.DBConnector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@Component
public class LogRecordProcessor {

    private static final Logger log = LoggerFactory.getLogger(LogRecordProcessor.class);

    @Autowired
    DBConnector dbConnector;

    @Autowired
    CsvFileProcessor csvFileProcessor;

    private String inputFile;
    private ArrayBlockingQueue<ServerLogEvent> blockingQueue = new ArrayBlockingQueue(1000000);
    private List<CsvFileProcessor> csvRecordList = new ArrayList<>();

    Thread readerThread = null;
    ThreadPoolExecutor executor = null;

    public void run(String filePath) throws Exception {
        inputFile = filePath;
        File inFile = new File(inputFile);
        log.info("Opening Database Connection");
        dbConnector.openDatabaseConnection();
        log.info("Creating Event table");
        dbConnector.createEventTable();
        executor = new ThreadPoolExecutor(32, 32, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        log.info("Reading Input Json File log events");
        readInputJsonFileEvents(inFile);
        while (!executor.isTerminated()) {
            Thread.sleep(2000);
            log.info(" Completed Task Count:" + executor.getCompletedTaskCount() + " Blocking Queue Size:" + blockingQueue.size()
                    + " Active:" + executor.getActiveCount() + " Reader Queue Size::" + executor.getQueue().size());
            if (!readerThread.isAlive()) {
                log.info("Reader Died!");
                log.info("Closing Database Connection");
                //dbConnector.checkDatabaseRecordCount();
                dbConnector.closeDatabaseConnection();
            }
        }
    }

    public void readInputJsonFileEvents(File inFile) throws Exception {
        Runnable task = () -> {
            try {
                LineIterator it = FileUtils.lineIterator(inFile, "UTF-8");
                while (it.hasNext()) {
                    String line = it.nextLine();
                    JSONObject logObject = new JSONObject(line);
                    ServerLogEvent logEvent = null;

                    if (logObject.isNull("type")) {
                        logEvent = new ServerLogEvent(logObject.getString("id"), logObject.getString("state"), logObject.getLong("timestamp"), null,
                                null, false);
                    } else {
                        logEvent = new ServerLogEvent(logObject.getString("id"), logObject.getString("state"), logObject.getLong("timestamp"),
                                logObject.getString("type"), logObject.getString("host"), false);
                    }

                    blockingQueue.put(logEvent);
                }
                log.info("File read finished");
                csvFileProcessor();
                log.info(" Completed Task Count:" + executor.getCompletedTaskCount() + " Blocking Queue Size:" + blockingQueue.size()
                        + " Active:" + executor.getActiveCount() + " Reader Queue Size::" + executor.getQueue().size());
                executor.shutdown();
            } catch (Exception e) {
               log.error(e.getMessage(), e);
            }
        };
        readerThread = new Thread(task);
        task.run();
    }

    private void csvFileProcessor() throws Exception{
        log.info("Writing records to CSV File");
        csvFileProcessor.setBlockingQueue(blockingQueue);
        csvRecordList.add(csvFileProcessor);
        executor.execute(csvFileProcessor);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        LogRecordProcessor f = new LogRecordProcessor();
        try {
            f.run("sample.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsedTime:" + elapsedTime / 1000 + " secs");
    }
}
