package com.main;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Vishwanath on 27/04/2019.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationMain.class)
@PropertySource("classpath:application.properties")
public class ApplicationMainTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMainTest.class);

    @Test
    public void contextLoads() {
        log.info("Context Loaded Successfully!");
    }
}
