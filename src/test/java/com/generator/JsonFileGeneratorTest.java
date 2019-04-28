package com.generator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonFileGeneratorTest {

    private static final Logger log = LoggerFactory.getLogger(JsonFileGeneratorTest.class);

    @InjectMocks
    JsonFileGenerator jsonFileGenerator;

    @Test
    public void testGenerateSampleJsonFile() throws Exception {
        jsonFileGenerator.generateSampleJsonFile(10);
        log.info("Bulk Jason Generated Successfully!");
    }
}
