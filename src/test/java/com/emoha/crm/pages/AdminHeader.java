package com.emoha.crm.pages;

import com.emoha.crm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminHeader {

    private static final Logger logger =
            LoggerFactory.getLogger(AdminHeader.class);

    private final WebDriver driver;

    private final By logoImage =
            By.cssSelector("img.application-header-logo[alt='Emoha Admin']");

    public AdminHeader(WebDriver driver) {

        this.driver = driver;
    }

    public WebElement waitForLogoImage() {

        logger.info("Waiting for Emoha Admin header logo");
        return WaitUtils.waitForElementVisible(driver, logoImage);
    }

    public boolean isLogoDisplayed() {

        boolean displayed = waitForLogoImage().isDisplayed();
        logger.info("Header logo displayed: {}", displayed);
        return displayed;
    }

    public String getLogoAltText() {

        String altText = waitForLogoImage().getAttribute("alt");
        logger.info("Header logo alt text: {}", altText);
        return altText;
    }

    public String getLogoSource() {

        String source = waitForLogoImage().getAttribute("src");
        logger.info("Header logo source: {}", source);
        return source;
    }
}
