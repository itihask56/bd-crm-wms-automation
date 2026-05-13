package com.emoha.crm.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {

        driver = DriverFactory.initializeDriver();
    }

    @AfterMethod
    public void tearDown() {

        DriverFactory.quitDriver();
    }
}