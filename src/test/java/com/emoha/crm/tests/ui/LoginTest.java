package com.emoha.crm.tests.ui;

import com.emoha.crm.base.BaseTest;
import com.emoha.crm.pages.LoginPage;
import com.emoha.crm.pages.OtpModal;
import com.emoha.crm.utils.ConfigReader;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void verifyLoginFlow() throws InterruptedException {

        driver.get(ConfigReader.getProperty("baseUrl"));

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
                ConfigReader.getProperty("email"),
                ConfigReader.getProperty("password")
        );

        OtpModal otpModal = new OtpModal(driver);

        otpModal.completeOtpFlow(
                ConfigReader.getProperty("mobileNumber"),
                ConfigReader.getProperty("testOtp")
        );
    }
}