package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage.java
 *
 * PURPOSE:
 *   Represents the Login AND Signup page (both forms are on the same URL: /login).
 *   The LEFT side has the LOGIN form.
 *   The RIGHT side has the SIGNUP (new user) form.
 *
 * URL: https://automationexercise.com/login
 */
public class LoginPage extends BasePage {

    // ─── LEFT SIDE: Login Form Locators ─────────────────────────────────────
    // data-qa attributes are test-friendly attributes added by developers
    // specifically for automation. They're more stable than CSS classes.
    private By loginEmailInput    = By.cssSelector("input[data-qa='login-email']");
    private By loginPasswordInput = By.cssSelector("input[data-qa='login-password']");
    private By loginButton        = By.cssSelector("button[data-qa='login-button']");

    // Error message shown when login fails (wrong email or password)
    private By loginErrorMsg = By.xpath("//p[contains(text(),'Your email or password is incorrect')]");

    // ─── RIGHT SIDE: Signup (New User) Form Locators ────────────────────────
    private By signupNameInput  = By.cssSelector("input[data-qa='signup-name']");
    private By signupEmailInput = By.cssSelector("input[data-qa='signup-email']");
    private By signupButton     = By.cssSelector("button[data-qa='signup-button']");

    // Error message shown when trying to register with an existing email
    private By signupErrorMsg = By.xpath("//p[contains(text(),'Email Address already exist')]");

    // Heading to verify we're on the right page
    private By loginHeading  = By.xpath("//h2[text()='Login to your account']");
    private By signupHeading = By.xpath("//h2[text()='New User Signup!']");

    /**
     * Constructor: passes driver to BasePage.
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  LOGIN ACTIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Types the email address into the login email field.
     * @param email  e.g., "testuser@test.com"
     */
    public void enterLoginEmail(String email) {
        type(loginEmailInput, email);
    }

    /**
     * Types the password into the login password field.
     * @param password  e.g., "Test@1234"
     */
    public void enterLoginPassword(String password) {
        type(loginPasswordInput, password);
    }

    /**
     * Clicks the LOGIN button.
     */
    public void clickLoginButton() {
        click(loginButton);
    }

    /**
     * CONVENIENCE METHOD: enters email + password and clicks Login.
     * This combines the three steps above into one call.
     * Makes test code cleaner: loginPage.login("a@b.com", "pass")
     *
     * @param email     the login email
     * @param password  the login password
     */
    public void login(String email, String password) {
        enterLoginEmail(email);
        enterLoginPassword(password);
        clickLoginButton();
    }

    /**
     * Returns the error message displayed after a failed login attempt.
     * @return  error message text
     */
    public String getLoginErrorMessage() {
        return getText(loginErrorMsg);
    }

    /**
     * Checks if the login error message is visible.
     * @return  true if error is shown
     */
    public boolean isLoginErrorVisible() {
        return isDisplayed(loginErrorMsg);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SIGNUP ACTIONS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Types a name into the Signup name field.
     * @param name  e.g., "Test User"
     */
    public void enterSignupName(String name) {
        type(signupNameInput, name);
    }

    /**
     * Types an email into the Signup email field.
     * @param email  e.g., "newuser@test.com"
     */
    public void enterSignupEmail(String email) {
        type(signupEmailInput, email);
    }

    /**
     * Clicks the SIGNUP button (takes user to account registration form).
     */
    public void clickSignupButton() {
        click(signupButton);
    }

    /**
     * CONVENIENCE METHOD: enters name + email and clicks Signup.
     *
     * @param name   the user's name
     * @param email  the signup email
     */
    public void signUp(String name, String email) {
        enterSignupName(name);
        enterSignupEmail(email);
        clickSignupButton();
    }

    /**
     * Returns the error message shown when trying to register an already-used email.
     * @return  error message text
     */
    public String getSignupErrorMessage() {
        return getText(signupErrorMsg);
    }

    /**
     * Verifies the login page headings are visible.
     * Call this at the start of login tests to confirm we're on the right page.
     *
     * @return  true if "Login to your account" heading is visible
     */
    public boolean isLoginPageVisible() {
        return isDisplayed(loginHeading);
    }

    /**
     * Checks if "New User Signup!" heading is visible.
     * @return  true if signup section is visible
     */
    public boolean isSignupSectionVisible() {
        return isDisplayed(signupHeading);
    }
}
