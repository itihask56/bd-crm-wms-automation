package com.emoha.crm.tests.ui;

import com.emoha.crm.base.BaseTest;
import com.emoha.crm.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GoogleTest extends BaseTest {

    @Test
    public void verifyGoogleTitle() throws InterruptedException {

        driver.get(ConfigReader.getProperty("baseUrl"));
        Thread.sleep(2000);

        String title = driver.getTitle();
        Thread.sleep(2000);

        System.out.println("Page Title: " + title);

        Assert.assertTrue(title.contains("Google"));
    }
}