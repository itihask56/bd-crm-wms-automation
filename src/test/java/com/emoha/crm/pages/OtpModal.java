package com.emoha.crm.pages;

import com.emoha.crm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OtpModal {

    private static final Logger logger =
            LoggerFactory.getLogger(OtpModal.class);

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

        logger.info("Entering mobile number: {}", maskMobile(mobile));
        WaitUtils.waitForElementVisible(driver, mobileInput).sendKeys(mobile);
    }

    public void clickSendOtp() {
        logger.info("Clicking Send OTP button");
        WaitUtils.waitForElementClickable(driver,sendOtpButton).click();

    }

    public void enterOtp(String otp) {
        logger.info("Entering OTP");
        WaitUtils.waitForElementClickable(driver,otpInput).sendKeys(otp);
//        driver.findElement(otpInput).sendKeys(otp);
    }

    public void clickSubmit() {

        logger.info("Submitting OTP modal");
        WaitUtils.waitForElementClickable(driver,submitButton).click();

    }

    // Combined Flow
    public void completeOtpFlow(
            String mobile,
            String otp
    ) throws InterruptedException {

        logger.info("Starting OTP flow");
        enterMobile(mobile);

        clickSendOtp();

        enterOtp(otp);

        clickSubmit();
        Thread.sleep(5000);
        logger.info("OTP flow completed");
    }

    private String maskMobile(String mobile) {

        if (mobile == null || mobile.length() < 4) {
            return "****";
        }

        return "******" + mobile.substring(mobile.length() - 4);
    }
}
