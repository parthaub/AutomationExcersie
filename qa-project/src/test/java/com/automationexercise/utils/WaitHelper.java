package com.automationexercise.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitHelper.java
 *
 * PURPOSE:
 *   Provides "explicit wait" methods so our tests wait for elements
 *   to be ready before interacting with them.
 *
 * WHY WAITS ARE IMPORTANT:
 *   Web pages load asynchronously. If we click a button before it's
 *   visible, the test will fail even though the button will be there in 1 second.
 *   Explicit waits solve this: "wait UP TO 20 seconds for this element to appear".
 *
 * TWO TYPES OF WAITS (IMPORTANT TO UNDERSTAND):
 *   1. IMPLICIT WAIT (set once in DriverFactory):
 *      "For EVERY findElement call, wait up to N seconds if not found immediately."
 *      Set with: driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
 *
 *   2. EXPLICIT WAIT (this class):
 *      "For THIS SPECIFIC element/condition, wait up to N seconds."
 *      More powerful and more precise than implicit wait.
 *
 * RULE: Never use Thread.sleep() in tests — it always waits the full time
 *       even if the element appears in 1 second. Explicit waits are smarter.
 */
public class WaitHelper {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * Constructor: creates a WaitHelper with the given driver and timeout.
     *
     * @param driver  the browser WebDriver
     */
    public WaitHelper(WebDriver driver) {
        this.driver = driver;

        // Read explicit wait timeout from config (e.g., 20 seconds)
        long waitTime = Long.parseLong(ConfigReader.getProperty("explicit.wait"));

        // WebDriverWait is Selenium's explicit wait mechanism
        // It polls every 500ms by default until the condition is met OR timeout expires
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
    }

    /**
     * Waits until the element is VISIBLE on the page (in the DOM and not hidden).
     *
     * EXAMPLE: waitForElementVisible(By.id("login-button"))
     *
     * @param locator  how to find the element (By.id, By.cssSelector, etc.)
     * @return         the visible WebElement (so you can chain calls)
     */
    public WebElement waitForElementVisible(By locator) {
        // ExpectedConditions contains many built-in conditions to wait for
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element is CLICKABLE (visible AND enabled).
     * Use this before clicking buttons, links, checkboxes.
     *
     * @param locator  how to find the element
     * @return         the clickable WebElement
     */
    public WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until an element is PRESENT in the DOM (even if hidden).
     * Less strict than waitForElementVisible.
     *
     * @param locator  how to find the element
     * @return         the present WebElement
     */
    public WebElement waitForElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the page title CONTAINS the given text.
     * Useful for verifying navigation to the right page.
     *
     * EXAMPLE: waitForTitleContains("Products")
     *
     * @param titlePart  part of the expected title
     */
    public void waitForTitleContains(String titlePart) {
        wait.until(ExpectedConditions.titleContains(titlePart));
    }

    /**
     * Waits until the given text appears INSIDE an element.
     *
     * @param locator       how to find the element
     * @param expectedText  the text to wait for
     */
    public void waitForTextInElement(By locator, String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    /**
     * Waits until the element is NO LONGER visible (disappears from screen).
     * Useful for waiting for loading spinners to disappear.
     *
     * @param locator  how to find the element
     */
    public void waitForElementInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
