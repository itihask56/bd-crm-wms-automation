package com.emoha.crm.pages;

import com.emoha.crm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class OtpModal {

    WebDriver driver;

    By mobileInput =
            By.xpath("//input[@placeholder='Enter Phone..']");

    By sendOtpButton =
            By.xpath("//button[contains(.,'Send OTP')]");

    By otpInput =
            By.xpath("//input[@placeholder='Enter 4-digit OTP']");

    By submitButton =
            By.xpath("//button[@htmltype='submit']");

    // Constructor
    public OtpModal(WebDriver driver) {

        this.driver = driver;
    }

    public void enterMobile(String mobile) {

        WaitUtils.waitForElementVisible(driver, mobileInput).sendKeys(mobile);
    }

    public void clickSendOtp() {
        WaitUtils.waitForElementClickable(driver,sendOtpButton).click();

    }

    public void enterOtp(String otp) {
        WaitUtils.waitForElementClickable(driver,otpInput).sendKeys(otp);
//        driver.findElement(otpInput).sendKeys(otp);
    }

    public void clickSubmit() {

        WaitUtils.waitForElementClickable(driver,submitButton).click();

    }

    // Combined Flow
    public void completeOtpFlow(
            String mobile,
            String otp
    ) throws InterruptedException {

        enterMobile(mobile);

        clickSendOtp();

        enterOtp(otp);

        clickSubmit();
        Thread.sleep(5000);
    }
}