package com.emoha.crm.tests.ui;

import com.emoha.crm.base.BaseTest;
import com.emoha.crm.pages.AddCarerPage;
import com.emoha.crm.pages.LoginPage;
import com.emoha.crm.pages.OtpModal;
import com.emoha.crm.testdata.CarerTestData;
import com.emoha.crm.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddCarerTest extends BaseTest {

    private static final Logger logger =
            LoggerFactory.getLogger(AddCarerTest.class);

    @Test
    public void regionalCrtCanAddCarer() {

        loginAsRegionalCrt();

        CarerTestData carer = CarerTestData.defaultCarer();
        AddCarerPage addCarerPage = new AddCarerPage(driver);

        addCarerPage.openFromMyCarers();
        addCarerPage.fillForm(carer);
        addCarerPage.submit();

        Assert.assertTrue(
                addCarerPage.isCarerVisibleInList(carer.getCarerName()),
                "Newly added carer should be visible in My Carers list"
        );
    }

    private void loginAsRegionalCrt() {

        logger.info("Opening CRM login page");
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
