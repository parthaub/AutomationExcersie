package com.automationexercise.stepDefinitions;

import com.automationexercise.pages.*;
import com.automationexercise.utils.DriverFactory;
import com.automationexercise.utils.ExtentManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * CheckoutSteps.java
 *
 * Step definitions for checkout.feature.
 * Handles the full end-to-end order placement flow.
 */
public class CheckoutSteps {

    private WebDriver    driver;
    private HomePage     homePage;
    private LoginPage    loginPage;
    private CartPage     cartPage;
    private CheckoutPage checkoutPage;

    /**
     * Helper: initializes page objects using the current thread's WebDriver.
     * Called lazily (first time a step in this class runs).
     */
    private void initPages() {
        driver       = DriverFactory.getDriver();
        homePage     = new HomePage(driver);
        loginPage    = new LoginPage(driver);
        cartPage     = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
    }

    @And("user clicks Proceed to Checkout")
    public void userClicksProceedToCheckout() {
        initPages();
        cartPage.clickProceedToCheckout();
        ExtentManager.logInfo("Clicked Proceed to Checkout");
    }

    @And("user clicks Register Login from checkout modal")
    public void userClicksRegisterLoginFromModal() {
        cartPage.clickRegisterLoginInModal();
        ExtentManager.logInfo("Clicked Register/Login from checkout modal");
    }

    @And("user logs in with email {string} and password {string}")
    public void userLogsIn(String email, String password) {
        loginPage.login(email, password);
        ExtentManager.logInfo("Logged in with: " + email);
    }

    @And("user clicks on Cart link in navigation")
    public void userClicksCartLink() {
        homePage.clickCart();
    }

    @And("user returns to cart page")
    public void userReturnsToCartPage() {
        homePage.clickCart();
        ExtentManager.logInfo("Navigated back to cart page");
    }

    @And("user clicks Proceed to Checkout again")
    public void userClicksProceedToCheckoutAgain() {
        cartPage.clickProceedToCheckout();
        ExtentManager.logInfo("Clicked Proceed to Checkout (second time, as logged-in user)");
    }

    @Then("checkout page should display address details")
    public void checkoutPageShouldDisplayAddressDetails() {
        Assert.assertTrue(checkoutPage.isCheckoutPageVisible(),
            "Checkout page with address details should be visible");
        ExtentManager.logPass("Checkout page displayed with address details");
    }

    @When("user enters order comment {string}")
    public void userEntersOrderComment(String comment) {
        checkoutPage.enterOrderComment(comment);
        ExtentManager.logInfo("Entered order comment: " + comment);
    }

    @And("user clicks Place Order")
    public void userClicksPlaceOrder() {
        checkoutPage.clickPlaceOrder();
        ExtentManager.logInfo("Clicked Place Order");
    }

    @And("user fills payment with card name {string} number {string} cvc {string} expiry month {string} year {string}")
    public void userFillsPaymentDetails(String name, String number, String cvc, String month, String year) {
        checkoutPage.fillPaymentDetails(name, number, cvc, month, year);
        ExtentManager.logInfo("Payment details filled for card: " + name);
    }

    @And("user confirms payment")
    public void userConfirmsPayment() {
        checkoutPage.clickPayAndConfirm();
        ExtentManager.logInfo("Confirmed payment");
    }

    @Then("order success message should be displayed")
    public void orderSuccessMessageShouldBeDisplayed() {
        Assert.assertTrue(checkoutPage.isOrderPlacedSuccessfully(),
            "Order success message should appear after payment");
        ExtentManager.logPass("Order placed successfully: " + checkoutPage.getOrderSuccessMessage());
    }

    @Then("cart should not be empty")
    public void cartShouldNotBeEmpty() {
        int count = cartPage.getCartItemCount();
        Assert.assertTrue(count > 0,
            "Cart should have at least 1 product but was empty");
        ExtentManager.logPass("Cart has " + count + " product(s) — persisted after login");
    }
}
