package com.emoha.crm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage {

    private static final Logger logger =
            LoggerFactory.getLogger(LoginPage.class);

    WebDriver driver;

    // Locators
    By emailInput = By.id("basic_email");

    By passwordInput = By.id("basic_password");

    By loginButton = By.xpath("//span[text()='Login']");

    // Constructor
    public LoginPage(WebDriver driver) {

        this.driver = driver;
    }

    // Actions
    public void enterEmail(String email) {

        logger.info("Entering login email: {}", maskEmail(email));
        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterPassword(String password) {

        logger.info("Entering login password");
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLogin() {

        logger.info("Clicking login button");
        driver.findElement(loginButton).click();
    }

    // Reusable combined action
    public void login(String email, String password) {

        logger.info("Starting login form submission");
        enterEmail(email);

        enterPassword(password);

        clickLogin();
        logger.info("Login form submitted");
    }

    private String maskEmail(String email) {

        if (email == null || !email.contains("@")) {
            return "****";
        }

        String[] parts = email.split("@", 2);
        String name = parts[0];
        String domain = parts[1];

        if (name.length() <= 2) {
            return "**@" + domain;
        }

        return name.substring(0, 2) + "****@" + domain;
    }
}
