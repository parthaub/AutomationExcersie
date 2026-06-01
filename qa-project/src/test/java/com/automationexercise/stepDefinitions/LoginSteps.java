package com.automationexercise.stepDefinitions;

import com.automationexercise.pages.HomePage;
import com.automationexercise.pages.LoginPage;
import com.automationexercise.utils.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * LoginSteps.java
 *
 * PURPOSE:
 *   Step Definitions for login.feature.
 *   Each method here corresponds to one Given/When/Then/And line
 *   in the .feature file.
 *
 * HOW CUCUMBER MATCHES STEPS:
 *   The @Given/@When/@Then/@And annotations contain a REGEX or STRING
 *   that must match the feature file text EXACTLY.
 *
 *   Feature file line:   When user enters email "a@b.com" and password "pass"
 *   Step def annotation: @When("user enters email {string} and password {string}")
 *   {string} = a Cucumber expression that captures quoted text as a String parameter
 *
 * IMPORTANT: @Given, @When, @Then, @And are INTERCHANGEABLE in Cucumber.
 *   Use them based on what makes the feature file read most naturally.
 *   Cucumber doesn't care which keyword you use — it just matches the text.
 */
public class LoginSteps {

    // Page Objects — initialized in each step that needs them
    // We get the driver from DriverFactory (set up in Hooks.java)
    private WebDriver driver;
    private HomePage  homePage;
    private LoginPage loginPage;

    // ─────────────────────────────────────────────────────────────────────────
    //  GIVEN STEPS — Setup / Preconditions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Navigates to the home page and initializes page objects.
     * This step is in the Background, so it runs before EVERY scenario.
     */
    @Given("user is on the home page")
    public void userIsOnTheHomePage() {
        // Get the driver that Hooks.java created
        driver    = DriverFactory.getDriver();
        homePage  = new HomePage(driver);
        loginPage = new LoginPage(driver);

        // Navigate to the website
        homePage.navigateTo();

        // Verify we landed on the home page
        Assert.assertTrue(homePage.isHomePageVisible(),
            "Home page should be visible after navigation");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  WHEN STEPS — Actions
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clicks the "Signup / Login" link in the navigation.
     */
    @When("user clicks on Signup Login link")
    public void userClicksSignupLoginLink() {
        homePage.clickSignupLogin();
    }

    /**
     * Enters email and password in the login form.
     *
     * {string} in the annotation captures the quoted text from the feature file.
     * So "user enters email "a@b.com" and password "pass"" passes:
     *   email    = "a@b.com"
     *   password = "pass"
     *
     * @param email     the email to type
     * @param password  the password to type
     */
    @When("user enters email {string} and password {string}")
    public void userEntersEmailAndPassword(String email, String password) {
        loginPage.enterLoginEmail(email);
        loginPage.enterLoginPassword(password);
    }

    /**
     * Clicks the Login button.
     */
    @And("user clicks Login button")
    public void userClicksLoginButton() {
        loginPage.clickLoginButton();
    }

    /**
     * Clicks Logout in the navigation.
     */
    @When("user clicks Logout")
    public void userClicksLogout() {
        homePage.clickLogout();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  THEN STEPS — Assertions (verifications)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Asserts that the user is now logged in.
     * Checks for the "Logged in as" text in the header.
     */
    @Then("user should be logged in successfully")
    public void userShouldBeLoggedIn() {
        Assert.assertTrue(homePage.isLoggedIn(),
            "User should be logged in — 'Logged in as' text not found in header");
    }

    /**
     * Asserts a specific text is visible in the header.
     *
     * @param text  expected text (e.g., "Logged in as")
     */
    @And("{string} text should be visible in header")
    public void textShouldBeVisibleInHeader(String text) {
        String loggedInText = homePage.getLoggedInUsername();
        Assert.assertFalse(loggedInText.isEmpty(),
            "Expected '" + text + "' to be in the header, but it wasn't found");
    }

    /**
     * Asserts that a specific error message is displayed on the page.
     *
     * @param expectedMessage  the exact error message text to verify
     */
    @Then("error message {string} should be displayed")
    public void errorMessageShouldBeDisplayed(String expectedMessage) {
        // First check the error is visible
        Assert.assertTrue(loginPage.isLoginErrorVisible(),
            "Login error message should be visible but was not found");

        // Then verify the exact text
        String actualMessage = loginPage.getLoginErrorMessage();
        Assert.assertTrue(actualMessage.contains(expectedMessage),
            "Expected error: '" + expectedMessage + "' but got: '" + actualMessage + "'");
    }

    /**
     * Asserts we're still on the login page (for empty field tests).
     */
    @Then("login page should still be displayed")
    public void loginPageShouldStillBeDisplayed() {
        Assert.assertTrue(loginPage.isLoginPageVisible(),
            "Should still be on login page");
    }

    /**
     * Asserts user is back on the login page after logout.
     */
    @Then("user should be redirected to login page")
    public void userShouldBeRedirectedToLoginPage() {
        Assert.assertTrue(loginPage.isLoginPageVisible(),
            "Should be on login page after logout");
    }

    /**
     * Asserts login result based on "success" or "failure" from Scenario Outline.
     *
     * @param expectedResult  "success" or "failure"
     */
    @Then("login result should be {string}")
    public void loginResultShouldBe(String expectedResult) {
        if (expectedResult.equals("success")) {
            Assert.assertTrue(homePage.isLoggedIn(),
                "Expected successful login but user is not logged in");
        } else {
            // For failure, either error message or still on login page
            boolean hasError  = loginPage.isLoginErrorVisible();
            boolean onLogin   = loginPage.isLoginPageVisible();
            Assert.assertTrue(hasError || onLogin,
                "Expected login failure but user appears to have logged in");
        }
    }
}
