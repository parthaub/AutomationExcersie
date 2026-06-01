package com.automationexercise.stepDefinitions;

import com.automationexercise.pages.CartPage;
import com.automationexercise.pages.HomePage;
import com.automationexercise.pages.ProductsPage;
import com.automationexercise.utils.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * ProductSteps.java
 *
 * PURPOSE:
 *   Step Definitions for products.feature.
 *   These steps control the browser for product-related scenarios.
 */
public class ProductSteps {

    private WebDriver    driver;
    private HomePage     homePage;
    private ProductsPage productsPage;
    private CartPage     cartPage;

    // ─────────────────────────────────────────────────────────────────────────
    //  WHEN STEPS
    // ─────────────────────────────────────────────────────────────────────────

    @When("user clicks on Products link in navigation")
    public void userClicksProductsLink() {
        driver       = DriverFactory.getDriver();
        homePage     = new HomePage(driver);
        productsPage = new ProductsPage(driver);
        cartPage     = new CartPage(driver);

        homePage.clickProducts();
    }

    /**
     * Searches for a product using the search box.
     * {string} captures the keyword from the feature file.
     *
     * @param keyword  word to search for (e.g., "top")
     */
    @And("user searches for product {string}")
    public void userSearchesForProduct(String keyword) {
        productsPage.searchForProduct(keyword);
    }

    @And("user hovers over first product and clicks Add to Cart")
    public void userHoversFirstProductAndAddsToCart() {
        productsPage.hoverAndAddToCart(1); // Product number 1 = first product
    }

    @And("user hovers over second product and clicks Add to Cart")
    public void userHoversSecondProductAndAddsToCart() {
        productsPage.hoverAndAddToCart(2); // Product number 2 = second product
    }

    @And("user clicks Continue Shopping in the popup")
    public void userClicksContinueShopping() {
        productsPage.clickContinueShopping();
    }

    @And("user clicks View Cart in the popup")
    public void userClicksViewCartInPopup() {
        productsPage.clickViewCartInModal();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  THEN STEPS (Assertions)
    // ─────────────────────────────────────────────────────────────────────────

    @Then("user should be on All Products page")
    public void userShouldBeOnAllProductsPage() {
        Assert.assertTrue(productsPage.isOnProductsPage(),
            "Should be on All Products page");
    }

    @Then("product list should be visible with multiple products")
    public void productListShouldBeVisible() {
        int count = productsPage.getProductCount();
        Assert.assertTrue(count > 0,
            "Expected products to be listed but found: " + count);
    }

    /**
     * Verifies the "SEARCHED PRODUCTS" section header appeared.
     * The {string} captures what's in quotes in the feature file.
     *
     * @param sectionName  text of the section heading (e.g., "SEARCHED PRODUCTS")
     */
    @Then("{string} section should appear")
    public void sectionShouldAppear(String sectionName) {
        Assert.assertTrue(productsPage.isSearchResultsVisible(),
            "'" + sectionName + "' section should be visible after search");
    }

    @Then("search results should not be empty")
    public void searchResultsShouldNotBeEmpty() {
        int count = productsPage.getProductCount();
        Assert.assertTrue(count > 0,
            "Search results should show at least one product but found: " + count);
    }

    @Then("cart page should have {int} products")
    public void cartPageShouldHaveProducts(int expectedCount) {
        // {int} captures an integer from the feature file
        int actualCount = cartPage.getCartItemCount();
        Assert.assertEquals(actualCount, expectedCount,
            "Expected " + expectedCount + " products in cart but found " + actualCount);
    }
}
