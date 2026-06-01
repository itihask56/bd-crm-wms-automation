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

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver initializeDriver() {

        String browser = ConfigReader.getProperty("browser");

        boolean headless =
                Boolean.parseBoolean(
                        ConfigReader.getProperty("headless"));

        logger.info("Initializing WebDriver. browser={}, headless={}", browser, headless);

        WebDriver webDriver;

        if (browser.equalsIgnoreCase("chrome")) {

            webDriver = initializeChromeDriver(headless);
        } else {
            logger.error("Unsupported browser requested: {}", browser);
            throw new IllegalArgumentException(
                    "Unsupported browser: "
                            + browser
                            + ". Supported browser: chrome"
            );
        }

        if (!headless) {
            webDriver.manage().window().maximize();
            logger.info("Browser window maximized");
        }

        driver.set(webDriver);

        return webDriver;
    }

    public static void quitDriver() {
        WebDriver webDriver = driver.get();

        if (webDriver != null) {

            logger.info("Closing WebDriver session");
            webDriver.quit();
            driver.remove();
        }
    }

    public static WebDriver getDriver() {

        return driver.get();
    }

    private static WebDriver initializeChromeDriver(boolean headless) {

        logger.info("Setting up ChromeDriver with WebDriverManager");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--use-fake-ui-for-media-stream");
        options.addArguments("--disable-notifications");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        return new ChromeDriver(options);
    }
}
