package com.emoha.crm.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitUtils {

    private static final Logger logger =
            LoggerFactory.getLogger(WaitUtils.class);

    private static final int TIMEOUT = 10;

    public static WebElement waitForElementVisible(
            WebDriver driver,
            By locator
    ) {

        logger.info("Waiting up to {} seconds for element to be visible: {}", TIMEOUT, locator);
        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(TIMEOUT)
                );

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    public static WebElement waitForElementClickable(
            WebDriver driver,
            By locator
    ) {

        logger.info("Waiting up to {} seconds for element to be clickable: {}", TIMEOUT, locator);
        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(TIMEOUT)
                );

        return wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }
}
