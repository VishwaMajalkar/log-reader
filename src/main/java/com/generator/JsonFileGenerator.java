package com.generator;

import com.beans.ServerLogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by Vishwanath on 27/04/2019.
 */
public class JsonFileGenerator {

    private static final Logger log = LoggerFactory.getLogger(JsonFileGenerator.class);

    private static int logLines = 50000;

    public static void main(String[] args) throws Exception {
        JsonFileGenerator jsonGenerator = new JsonFileGenerator();
        long startTime = System.nanoTime();
        jsonGenerator.generateSampleJsonFile(logLines);
        long elapsedTime = System.nanoTime() - startTime;
        log.info("Total time required to create " + logLines + " json lines in millis: " + elapsedTime / 1000000);
    }

    public void generateSampleJsonFile(int i) throws IOException {
        String host = "host123";
        String type = "APPLICATION_LOG";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Path path = Paths.get("bulk_record.json");
        Random random = new Random();

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            while (i-- > 0) {
                String id = RandomStringUtils.randomAlphabetic(10);
                long timestamp = RandomUtils.nextInt();

                ServerLogEvent serverEvent = new ServerLogEvent(id,"STARTED", timestamp, type, host, false);
                writer.write(mapper.writeValueAsString(serverEvent) + System.lineSeparator());

                serverEvent = new ServerLogEvent(id,"FINISHED", timestamp + random.nextInt(10), type, host, false);
                writer.write(mapper.writeValueAsString(serverEvent) + System.lineSeparator());
            }
        }
    }
}
