package com.emoha.crm.base;

import com.emoha.crm.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(DriverFactory.class);

    public static WebDriver driver;

    public static WebDriver initializeDriver() {

        String browser =
                System.getProperty("browser") != null
                        ? System.getProperty("browser")
                        : ConfigReader.getProperty("browser");

        boolean headless =
                Boolean.parseBoolean(
                        ConfigReader.getProperty("headless"));

        logger.info("Initializing WebDriver. browser={}, headless={}", browser, headless);

        if (browser.equalsIgnoreCase("chrome")) {

            logger.info("Setting up ChromeDriver with WebDriverManager");
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            // Auto allow camera & mic permissions
            options.addArguments("--use-fake-ui-for-media-stream");
            // Disable browser notifications
            options.addArguments("--disable-notifications");

            if (headless) {
                options.addArguments("--headless=new");
            }
            driver = new ChromeDriver(options);
        } else {
            logger.error("Unsupported browser requested: {}", browser);
            throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
            );
        }

        driver.manage().window().maximize();
        logger.info("Browser window maximized");

        return driver;
    }

    public static void quitDriver() {

        if (driver != null) {

            logger.info("Closing WebDriver session");
            driver.quit();
        }
    }

    public static WebDriver getDriver() {

        return driver;
    }
}
