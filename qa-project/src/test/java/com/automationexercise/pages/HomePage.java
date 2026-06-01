package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * HomePage.java
 *
 * PURPOSE:
 *   Represents the AutomationExercise HOME PAGE.
 *   Contains all locators and actions specific to the home page.
 *
 * URL: https://automationexercise.com
 *
 * WHAT'S ON THIS PAGE:
 *   - Top navigation: Home, Products, Cart, Signup/Login, etc.
 *   - "Logged in as [username]" (shown after login)
 *   - Delete Account / Logout links (shown after login)
 *   - Product carousel/slider
 *   - Footer with newsletter subscription
 *   - Category sidebar
 *
 * POM RULE: This class has ONLY locators and page methods.
 *           NO assertions here. Assertions belong in test classes.
 */
public class HomePage extends BasePage {

    // ─────────────────────────────────────────────────────────────────────────
    //  LOCATORS
    //  By.cssSelector = CSS selector (like in CSS stylesheets, fast and readable)
    //  By.xpath       = XPath expression (more powerful but more fragile)
    //  By.id          = element's id attribute (fastest, most reliable)
    // ─────────────────────────────────────────────────────────────────────────

    // Navigation links in the top menu bar
    private By navSignupLogin  = By.cssSelector("a[href='/login']");
    private By navProducts     = By.cssSelector("a[href='/products']");
    private By navCart         = By.cssSelector("a[href='/view_cart']");
    private By navContactUs    = By.cssSelector("a[href='/contact_us']");
    private By navTestCases    = By.cssSelector("a[href='/test_cases']");

    // These elements only appear AFTER a user is logged in
    private By loggedInAsText  = By.xpath("//a[contains(., 'Logged in as')]");
    private By deleteAccount   = By.cssSelector("a[href='/delete_account']");
    private By logoutLink      = By.cssSelector("a[href='/logout']");

    // Footer subscription elements (scroll down to see these)
    private By subscriptionEmailInput = By.id("susbscribe_email");   // Note: typo in the site's HTML
    private By subscriptionSubmitBtn  = By.id("subscribe");
    private By subscriptionSuccess    = By.cssSelector("div.alert-success span");

    // The page logo (used to verify we're on the home page)
    private By siteLogo = By.cssSelector("div#header img");

    /**
     * Constructor: calls BasePage(driver) which sets up driver, wait, actions, js.
     * ALL page classes must have this constructor.
     *
     * @param driver  the active WebDriver from DriverFactory.getDriver()
     */
    public HomePage(WebDriver driver) {
        super(driver);   // This calls BasePage's constructor
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PAGE ACTIONS (METHODS)
    //  One method = one user action on this page
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates to the home page URL.
     * Call this at the start of each test to ensure we start from a clean page.
     */
    public void navigateTo() {
        String baseUrl = com.automationexercise.utils.ConfigReader.getProperty("base.url");
        driver.get(baseUrl);
    }

    /**
     * Clicks "Signup / Login" in the navigation bar.
     * Takes you to /login page.
     */
    public void clickSignupLogin() {
        click(navSignupLogin);
    }

    /**
     * Clicks "Products" in the navigation bar.
     * Takes you to /products page.
     */
    public void clickProducts() {
        click(navProducts);
    }

    /**
     * Clicks the Cart icon/link in the navigation bar.
     * Takes you to /view_cart page.
     */
    public void clickCart() {
        click(navCart);
    }

    /**
     * Clicks "Contact Us" in the navigation bar.
     */
    public void clickContactUs() {
        click(navContactUs);
    }

    /**
     * Clicks "Logout" link (only visible when logged in).
     */
    public void clickLogout() {
        click(logoutLink);
    }

    /**
     * Clicks "Delete Account" link (only visible when logged in).
     */
    public void clickDeleteAccount() {
        click(deleteAccount);
    }

    /**
     * Returns the username shown in the "Logged in as [name]" header text.
     * Example: if header says "Logged in as John", this returns "John"
     *
     * @return  the logged-in username, or empty string if not found
     */
    public String getLoggedInUsername() {
        String fullText = getText(loggedInAsText); // "Logged in as John"
        // Split by "as " and take the second part (index 1)
        // e.g., ["Logged in ", "John"] → "John"
        if (fullText.contains("Logged in as ")) {
            return fullText.replace("Logged in as ", "").trim();
        }
        return "";
    }

    /**
     * Checks whether the "Logged in as [name]" text is visible in the header.
     * Returns true if user is logged in, false otherwise.
     *
     * @return  true if logged in
     */
    public boolean isLoggedIn() {
        return isDisplayed(loggedInAsText);
    }

    /**
     * Checks whether the site logo is visible.
     * Used to verify we successfully landed on the home page.
     *
     * @return  true if logo is visible
     */
    public boolean isHomePageVisible() {
        return isDisplayed(siteLogo);
    }

    /**
     * Subscribes to the newsletter using the footer subscription form.
     * Scrolls to the footer first since it's below the fold.
     *
     * @param email  email address to subscribe with
     */
    public void subscribeToNewsletter(String email) {
        scrollToBottom();                    // Scroll down to the footer
        type(subscriptionEmailInput, email); // Type the email address
        click(subscriptionSubmitBtn);        // Click the Subscribe button
    }

    /**
     * Gets the success message shown after newsletter subscription.
     *
     * @return  the success message text
     */
    public String getSubscriptionSuccessMessage() {
        return getText(subscriptionSuccess);
    }
}
