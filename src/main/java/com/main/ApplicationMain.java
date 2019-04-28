package com.main;

import com.processor.LogRecordProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"com"})
public class ApplicationMain {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMain.class);

    public static void main(String[] args) throws Exception{
        if (args.length == 0) {
            log.error("Input file not provided.");
            System.exit(1);
        }
        String jsonFilePath = args[0];
        File inFile = new File(jsonFilePath);
        if (!inFile.exists()) {
            log.error("Input Json file does not exist for provided Path:" + jsonFilePath);
            System.exit(2);
        }
        log.info("File PATH:" + jsonFilePath);
        ApplicationContext applicationContext = SpringApplication.run(ApplicationMain.class, args);
        LogRecordProcessor logRecordProcessor = applicationContext.getBean(LogRecordProcessor.class);
        logRecordProcessor.run(jsonFilePath);
    }
}
