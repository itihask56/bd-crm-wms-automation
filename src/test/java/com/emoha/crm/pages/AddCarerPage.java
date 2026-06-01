package com.emoha.crm.pages;

import com.emoha.crm.testdata.CarerTestData;
import com.emoha.crm.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class AddCarerPage {

    private static final int DROPDOWN_SCROLL_ATTEMPTS = 30;
    private static final int DROPDOWN_SCROLL_PIXELS = 220;
    private static final int DROPDOWN_RENDER_DELAY_MILLIS = 150;
    private static final Duration SHORT_WAIT = Duration.ofSeconds(2);
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(10);

    private static final Logger logger =
            LoggerFactory.getLogger(AddCarerPage.class);

    private final WebDriver driver;

    private final By myCarersMenuItem =
            By.xpath("//*[self::a or self::span or self::div][normalize-space()='My Carers']");

    private final By addCarerButton =
            By.xpath("//button[normalize-space()='Add carer' or normalize-space()='Add Carer']");

    private final By submitButton =
            By.xpath("//button[normalize-space()='Submit']");

    private final By pseudoNumberCheckbox =
            By.xpath("//span[normalize-space()=\"I don't have carer's phone number\"]/preceding::input[@type='checkbox'][1]");

    private final By pseudoNumberLabel =
            By.xpath("//span[normalize-space()=\"I don't have carer's phone number\"]");

    private final By openDropdown =
            By.cssSelector(".ant-select-open");

    private final By visibleDropdown =
            By.cssSelector(".ant-select-dropdown:not(.ant-select-dropdown-hidden)");

    private final By visibleDropdownScrollContainer =
            By.cssSelector(".ant-select-dropdown:not(.ant-select-dropdown-hidden) .rc-virtual-list-holder");

    public AddCarerPage(WebDriver driver) {

        this.driver = driver;
    }

    public void openFromMyCarers() {

        logger.info("Opening Add Carer form from My Carers");
        WaitUtils.waitForElementClickable(driver, myCarersMenuItem).click();
        WaitUtils.waitForElementClickable(driver, addCarerButton).click();
        WaitUtils.waitForElementVisible(driver, inputByLabel("Carer Name"));
    }

    public void fillForm(CarerTestData carer) {

        logger.info("Filling Add Carer form for {}", carer.getCarerName());
        uploadIfPresent("PHOTO", carer.getPhotoPath());
        typeByLabel("Carer Name", carer.getCarerName());
        typeByLabel("Carer Number", carer.getCarerNumber());
        setPseudoNumber(carer.isPseudoNumber());
        selectByLabel("Carer Type", carer.getCarerType());
        selectByLabel("Category", carer.getCategory());
        selectByLabel("Gender", carer.getGender());
        typeByLabel("Age", carer.getAge());
        selectByLabel("Region", carer.getRegion());
        selectByLabel("City", carer.getCity());
        typeOptionalByLabel("Service Experience", carer.getServiceExperience());
        selectOptionalByLabel("Special skills", carer.getSpecialSkills());
        typeByLabel("Aadhaar number", carer.getAadhaarNumber());
        selectByLabel("Source", carer.getSource());
        selectOptionalByLabel("Language Preference", carer.getLanguagePreference());
        selectOptionalByLabel("City Preference", carer.getCityPreference());
        uploadRequired("Aadhaar Card", carer.getAadhaarCardPath());
        uploadIfPresent("Experience Document", carer.getExperienceDocumentPath());
        uploadIfPresent("PAN Card", carer.getPanCardPath());
    }

    public void submit() {

        logger.info("Submitting Add Carer form");
        WaitUtils.waitForElementClickable(driver, submitButton).click();
    }

    public boolean isCarerVisibleInList(String carerName) {

        By carerNameCell =
                By.xpath("//*[self::td or self::div][normalize-space()=" + xpathLiteral(carerName) + "]");

        try {
            return WaitUtils.waitForElementVisible(driver, carerNameCell).isDisplayed();
        } catch (RuntimeException e) {
            logger.warn("Carer was not visible in list: {}", carerName);
            return false;
        }
    }

    private void typeByLabel(String label, String value) {

        logger.info("Typing {} field", label);
        WebElement input = WaitUtils.waitForElementClickable(driver, inputByLabel(label));
        scrollIntoView(input);
        input.clear();
        input.sendKeys(value);
    }

    private void typeOptionalByLabel(String label, String value) {

        if (hasText(value)) {
            typeByLabel(label, value);
        }
    }

    private void selectByLabel(String label, String option) {

        logger.info("Selecting {} dropdown option: {}", label, option);
        WebElement select = WaitUtils.waitForElementClickable(driver, dropdownByLabel(label));
        openDropdown(label, select);

        WebElement optionElement = findDropdownOptionWithScroll(label, option);

        clickWithJavascript(optionElement);
        waitForDropdownToClose();
    }

    private void selectOptionalByLabel(String label, String option) {

        if (hasText(option)) {
            selectByLabel(label, option);
        }
    }

    private void openDropdown(String label, WebElement select) {

        scrollIntoView(select);
        select.click();

        if (isDropdownVisible()) {
            return;
        }

        logger.info("Retrying {} dropdown open with JavaScript click", label);
        clickWithJavascript(select);

        WebDriverWait wait =
                new WebDriverWait(driver, DEFAULT_WAIT);

        wait.until(ExpectedConditions.visibilityOfElementLocated(visibleDropdown));
    }

    private boolean isDropdownVisible() {

        try {
            WebDriverWait wait =
                    new WebDriverWait(driver, SHORT_WAIT);

            wait.until(ExpectedConditions.visibilityOfElementLocated(visibleDropdown));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void setPseudoNumber(boolean enabled) {

        WebElement checkbox = driver.findElement(pseudoNumberCheckbox);

        if (checkbox.isSelected() != enabled) {
            WaitUtils.waitForElementClickable(driver, pseudoNumberLabel).click();
        }
    }

    private void uploadIfPresent(String label, String filePath) {

        if (!hasText(filePath)) {
            return;
        }

        uploadFile(label, filePath);
    }

    private void uploadRequired(String label, String filePath) {

        if (!hasText(filePath)) {
            throw new IllegalArgumentException(label + " document path is required");
        }

        uploadFile(label, filePath);
    }

    private void uploadFile(String label, String filePath) {

        Path path = Path.of(filePath).toAbsolutePath();

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    label + " document file does not exist: " + path
            );
        }

        logger.info("Uploading {} document from {}", label, path);
        WebElement fileInput = driver.findElement(fileInputByLabel(label));
        scrollIntoView(fileInput);
        fileInput.sendKeys(path.toString());
    }

    private By inputByLabel(String label) {

        return By.xpath(
                exactLabel(label)
                        + "/following::input[not(@type='hidden')][1]"
        );
    }

    private By dropdownByLabel(String label) {

        return By.xpath(
                exactLabel(label)
                        + "/following::*[contains(@class,'ant-select-selector')][1]"
        );
    }

    private By dropdownOption(String option) {

        return By.xpath(
                "//*[contains(@class,'ant-select-dropdown') and not(contains(@class,'ant-select-dropdown-hidden'))]"
                        + "//*[contains(@class,'ant-select-item-option') and not(contains(@class,'ant-select-item-option-disabled'))"
                        + " and (contains(normalize-space(),"
                        + xpathLiteral(option)
                        + ") or contains(@title,"
                        + xpathLiteral(option)
                        + "))]"
        );
    }

    private WebElement findDropdownOptionWithScroll(String label, String option) {

        WebDriverWait wait =
                new WebDriverWait(driver, DEFAULT_WAIT);

        wait.until(ExpectedConditions.visibilityOfElementLocated(visibleDropdown));
        resetDropdownScroll();

        for (int attempt = 0; attempt < DROPDOWN_SCROLL_ATTEMPTS; attempt++) {
            WebElement optionElement = findVisibleDropdownOption(option);

            if (optionElement != null) {
                return optionElement;
            }

            scrollOpenDropdownDown();
            waitForDropdownRender();
        }

        throw new IllegalStateException(
                "Could not find option '"
                        + option
                        + "' in "
                        + label
                        + " dropdown"
        );
    }

    private WebElement findVisibleDropdownOption(String option) {

        List<WebElement> options =
                driver.findElements(dropdownOption(option));

        for (WebElement optionElement : options) {
            if (optionElement.isDisplayed() && optionElement.isEnabled()) {
                return optionElement;
            }
        }

        return null;
    }

    private void waitForDropdownToClose() {

        WebDriverWait wait =
                new WebDriverWait(driver, DEFAULT_WAIT);

        wait.until(ExpectedConditions.numberOfElementsToBe(openDropdown, 0));
    }

    private void resetDropdownScroll() {

        List<WebElement> containers =
                driver.findElements(visibleDropdownScrollContainer);

        for (WebElement container : containers) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = 0;", container);
        }
    }

    private void scrollOpenDropdownDown() {

        List<WebElement> containers =
                driver.findElements(visibleDropdownScrollContainer);

        if (containers.isEmpty()) {
            WebElement dropdown = driver.findElement(visibleDropdown);
            scrollElementDown(dropdown);
            return;
        }

        for (WebElement container : containers) {
            scrollElementDown(container);
        }
    }

    private void waitForDropdownRender() {

        try {
            Thread.sleep(DROPDOWN_RENDER_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private By fileInputByLabel(String label) {

        return By.xpath(
                "//*[contains(normalize-space(),'"
                        + label
                        + "')]/following::input[@type='file'][1]"
        );
    }

    private void scrollIntoView(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                element
        );
    }

    private void clickWithJavascript(WebElement element) {

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void scrollElementDown(WebElement element) {

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollTop += arguments[1];",
                element,
                DROPDOWN_SCROLL_PIXELS
        );
    }

    private boolean hasText(String value) {

        return value != null && !value.trim().isEmpty();
    }

    private String xpathLiteral(String text) {

        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        return "concat('" + text.replace("'", "',\"'\",'") + "')";
    }

    private String exactLabel(String label) {

        return "//*[self::label or self::span or self::div]"
                + "[normalize-space(translate(., '*', ''))="
                + xpathLiteral(label)
                + "]";
    }
}
