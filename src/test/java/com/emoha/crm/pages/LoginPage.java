package com.emoha.crm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

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

        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterPassword(String password) {

        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLogin() {

        driver.findElement(loginButton).click();
    }

    // Reusable combined action
    public void login(String email, String password) {

        enterEmail(email);

        enterPassword(password);

        clickLogin();
    }
}