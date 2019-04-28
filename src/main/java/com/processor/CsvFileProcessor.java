package com.processor;

import com.beans.ServerLogEvent;
import com.connector.DBConnector;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@Component
public class CsvFileProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CsvFileProcessor.class);

    public ConcurrentHashMap<String, ServerLogEvent> logEventMap = new ConcurrentHashMap();

    @Value("${csv.file.path}")
    String csvFilePath;

    @Value("${log.event.max.duration}")
    int logEventMaxDuration;

    public ArrayBlockingQueue<ServerLogEvent> blockingQueue;

    BufferedWriter writer = null;
    String csvName = "";

    public void processEvents(String id) throws Exception {
        ServerLogEvent startEvent = logEventMap.get(id + "STARTED");
        ServerLogEvent finishEvent = logEventMap.get(id + "FINISHED");

        if (finishEvent != null && startEvent != null) {
            long logDiff = Math.abs(finishEvent.getTimestamp() - startEvent.getTimestamp());
            if (logEventMaxDuration > 0 && logDiff > logEventMaxDuration) {
                startEvent.setAlert(true);
                finishEvent.setAlert(true);
            }
            writeToFile(startEvent);
            writeToFile(finishEvent);
            logEventMap.remove(startEvent.getId() + startEvent.getState());
            logEventMap.remove(finishEvent.getId() + finishEvent.getState());

        }
    }

    private void writeToFile(ServerLogEvent se) throws Exception {
        int alert = se.isAlert() ? 1 : 0 ;
        writer.write(se.getId() + ";" + se.getState() + ";" + se.getTimestamp() + ";" + se.getType() + ";" + se.getHost() + ";" + alert + "\n");
    }

    @Override
    public void run() {
        try {
            csvName = csvFilePath + System.nanoTime() + ".csv";
            log.info("CSV FileName :"+csvName);
            writer = new BufferedWriter(new FileWriter(csvName));

            List<ServerLogEvent> serverEventList = new ArrayList<>();
            blockingQueue.drainTo(serverEventList, 100000);

            log.info("Writing Data to CSV Started...");
            for (ServerLogEvent logevent : serverEventList) {
                logEventMap.putIfAbsent(logevent.getId() + logevent.getState(), logevent);
                processEvents(logevent.getId());
            }
            writer.flush();
            writer.close();
            log.info("Writing Data to CSV Finished...");

            log.info("Loading CSV into Database table...");
            DBConnector dbConnector = new DBConnector();
            dbConnector.openDatabaseConnection();
            dbConnector.executeStatement("LOAD DATA local INFILE '" + csvName + "' INTO TABLE MySQL.EVENT FIELDS TERMINATED BY ';' ;");
            dbConnector.closeDatabaseConnection();
            log.info("CSV Data Persisted in database. Deleting CSV file");
            FileUtils.deleteQuietly(new File(csvName));
            log.info("Done!");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public ConcurrentHashMap<String, ServerLogEvent> getLogEventMap() {
        return logEventMap;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public int getLogEventMaxDuration() {
        return logEventMaxDuration;
    }

    public void setLogEventMaxDuration(int logEventMaxDuration) {
        this.logEventMaxDuration = logEventMaxDuration;
    }

    public ArrayBlockingQueue<ServerLogEvent> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(ArrayBlockingQueue<ServerLogEvent> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }
}
