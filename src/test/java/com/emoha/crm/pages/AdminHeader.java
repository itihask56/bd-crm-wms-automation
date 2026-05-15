package com.emoha.crm.pages;

import com.emoha.crm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminHeader {

    private final WebDriver driver;

    private final By logoImage =
            By.cssSelector("img.application-header-logo[alt='Emoha Admin']");

    public AdminHeader(WebDriver driver) {

        this.driver = driver;
    }

    public WebElement waitForLogoImage() {

        return WaitUtils.waitForElementVisible(driver, logoImage);
    }

    public boolean isLogoDisplayed() {

        return waitForLogoImage().isDisplayed();
    }

    public String getLogoAltText() {

        return waitForLogoImage().getAttribute("alt");
    }

    public String getLogoSource() {

        return waitForLogoImage().getAttribute("src");
    }
}
