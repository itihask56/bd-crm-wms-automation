package com.emoha.crm.tests.ui;

import com.emoha.crm.base.BaseTest;
import com.emoha.crm.pages.AdminHeader;
import com.emoha.crm.pages.LoginPage;
import com.emoha.crm.pages.OtpModal;
import com.emoha.crm.utils.ConfigReader;
import org.testng.Assert;
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

        AdminHeader adminHeader = new AdminHeader(driver);

        Assert.assertTrue(
                adminHeader.isLogoDisplayed(),
                "Emoha Admin header logo should be visible after successful login"
        );

        Assert.assertEquals(
                adminHeader.getLogoAltText(),
                "Emoha Admin",
                "Header logo alt text should match"
        );

        Assert.assertTrue(
                adminHeader.getLogoSource().endsWith(".svg"),
                "Header logo source should be an SVG image"
        );
    }
}
