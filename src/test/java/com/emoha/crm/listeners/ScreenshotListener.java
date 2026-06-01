package com.emoha.crm.listeners;

import com.emoha.crm.base.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotListener implements ITestListener {

    private static final Logger logger =
            LoggerFactory.getLogger(ScreenshotListener.class);

    private static final Path SCREENSHOT_DIR =
            Path.of("target", "screenshots");

    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void onTestFailure(ITestResult result) {

        WebDriver driver = DriverFactory.getDriver();

        if (driver == null) {
            logger.warn(
                    "Screenshot skipped for failed test '{}': WebDriver is not available",
                    result.getName()
            );
            return;
        }

        if (!(driver instanceof TakesScreenshot)) {
            logger.warn(
                    "Screenshot skipped for failed test '{}': driver does not support screenshots",
                    result.getName()
            );
            return;
        }

        try {
            Files.createDirectories(SCREENSHOT_DIR);

            File screenshot =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            Path destination = SCREENSHOT_DIR.resolve(buildFileName(result));
            Files.copy(screenshot.toPath(), destination);

            logger.info(
                    "Screenshot captured for failed test '{}': {}",
                    result.getName(),
                    destination.toAbsolutePath()
            );
            Reporter.log(
                    "Screenshot captured: " + destination.toAbsolutePath(),
                    true
            );
        } catch (IOException e) {
            logger.error(
                    "Unable to capture screenshot for failed test '{}'",
                    result.getName(),
                    e
            );
        }
    }

    private String buildFileName(ITestResult result) {

        String testClass =
                result.getTestClass().getRealClass().getSimpleName();

        String testMethod =
                result.getMethod().getMethodName();

        String timestamp =
                LocalDateTime.now().format(FILE_TIMESTAMP);

        return sanitize(testClass)
                + "_"
                + sanitize(testMethod)
                + "_"
                + timestamp
                + ".png";
    }

    private String sanitize(String value) {

        return value.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
