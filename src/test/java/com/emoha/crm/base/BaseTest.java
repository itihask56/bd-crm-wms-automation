package com.emoha.crm.base;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.lang.reflect.Method;

public class BaseTest {

    private static final Logger logger =
            LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;

    @BeforeMethod
    public void setup(Method method) {

        logger.info("Starting test: {}", method.getName());

        driver = DriverFactory.initializeDriver();
    }

    @AfterMethod
    public void tearDown(Method method) {

        logger.info("Finished test: {}", method.getName());

        DriverFactory.quitDriver();
    }
}
