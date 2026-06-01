package com.automationexercise.pages;

import com.automationexercise.utils.WaitHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * BasePage.java
 *
 * PURPOSE:
 *   The PARENT class that all Page Objects extend.
 *   Contains common browser interaction methods so we don't
 *   repeat the same code in every page class.
 *
 * PAGE OBJECT MODEL (POM) CONCEPT:
 *   Instead of writing driver.findElement(...).click() in every test,
 *   we create a class for each web page that:
 *     1. Defines WHERE the elements are (locators)
 *     2. Provides WHAT you can do on that page (methods)
 *   Tests then just call: loginPage.clickLoginButton()
 *   instead of: driver.findElement(By.cssSelector("button")).click()
 *
 * THIS class provides the low-level browser actions that ALL pages need.
 */
public class BasePage {

    // The WebDriver (browser) — all page interactions happen through this
    protected WebDriver driver;

    // WaitHelper provides explicit waits to avoid timing failures
    protected WaitHelper wait;

    // Actions allows more complex interactions: hover, drag-and-drop, right-click
    protected Actions actions;

    // JavascriptExecutor lets us run JavaScript code in the browser
    // Useful for clicking hidden elements, scrolling, etc.
    protected JavascriptExecutor js;

    /**
     * Constructor: every page class calls super(driver) which runs this.
     * It sets up the driver and creates helper objects.
     *
     * @param driver  the WebDriver instance from DriverFactory.getDriver()
     */
    public BasePage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WaitHelper(driver);
        this.actions = new Actions(driver);
        this.js      = (JavascriptExecutor) driver;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CLICK METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Waits for an element to be clickable, then clicks it.
     * This is the SAFE way to click — always prefer this over driver.findElement().click()
     *
     * @param locator  how to find the element (By.id, By.cssSelector, etc.)
     */
    protected void click(By locator) {
        wait.waitForElementClickable(locator).click();
    }

    /**
     * Clicks an element using JavaScript instead of Selenium's normal click.
     * Use this when the normal click fails due to overlapping elements.
     *
     * @param locator  how to find the element
     */
    protected void jsClick(By locator) {
        WebElement element = wait.waitForElementVisible(locator);
        js.executeScript("arguments[0].click();", element);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TYPE / INPUT METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clears an input field and types the given text.
     * Always clear before typing to avoid appending to existing text.
     *
     * @param locator  how to find the input element
     * @param text     the text to type
     */
    protected void type(By locator, String text) {
        WebElement element = wait.waitForElementVisible(locator);
        element.clear();          // Remove any existing text
        element.sendKeys(text);   // Type the new text
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET TEXT / ATTRIBUTE METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the visible text of an element.
     * Example: getText(By.id("greeting")) → "Welcome, John!"
     *
     * @param locator  how to find the element
     * @return         the element's text content
     */
    protected String getText(By locator) {
        return wait.waitForElementVisible(locator).getText();
    }

    /**
     * Returns the value of an HTML attribute.
     * Example: getAttribute(By.id("email"), "placeholder") → "Enter email"
     *
     * @param locator    how to find the element
     * @param attribute  attribute name (e.g., "value", "href", "class", "placeholder")
     * @return           the attribute's value
     */
    protected String getAttribute(By locator, String attribute) {
        return wait.waitForElementVisible(locator).getAttribute(attribute);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VISIBILITY CHECK METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks if an element is displayed on the page.
     * Returns false instead of throwing an exception if not found.
     *
     * @param locator  how to find the element
     * @return         true if visible, false if not found or hidden
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            // If the element isn't found at all, return false instead of crashing
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DROPDOWN METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Selects an option from an HTML <select> dropdown by the visible text.
     * Example: selectByVisibleText(By.id("country"), "United States")
     *
     * @param locator       how to find the <select> element
     * @param visibleText   the text of the option to select
     */
    protected void selectByVisibleText(By locator, String visibleText) {
        WebElement selectElement = wait.waitForElementVisible(locator);
        // Select is a Selenium helper class specifically for <select> dropdowns
        new Select(selectElement).selectByVisibleText(visibleText);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SCROLL METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Scrolls the page so the given element is in view.
     * Useful for elements that are below the fold (need to scroll to see them).
     *
     * @param locator  how to find the element to scroll to
     */
    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scrolls to the bottom of the page.
     * Useful for reaching footer elements like newsletter subscription.
     */
    protected void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  HOVER METHOD
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Hovers the mouse cursor over an element without clicking.
     * Triggers CSS hover effects (like showing "Add to Cart" on product images).
     *
     * @param locator  how to find the element to hover over
     */
    protected void hoverOver(By locator) {
        WebElement element = wait.waitForElementVisible(locator);
        actions.moveToElement(element).perform();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PAGE METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the current page title.
     * Example: getTitle() → "Automation Exercise - All Products"
     */
    protected String getTitle() {
        return driver.getTitle();
    }

    /**
     * Returns the current page URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
