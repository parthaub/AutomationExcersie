package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * CartPage.java
 *
 * PURPOSE:
 *   Represents the SHOPPING CART page (/view_cart).
 *   Users can: view cart items, verify prices/quantities,
 *              remove items, and proceed to checkout.
 *
 * URL: https://automationexercise.com/view_cart
 */
public class CartPage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    // Each row in the cart table represents one product
    // tbody tr = all rows inside the table body
    private By cartRows = By.cssSelector("#cart_info_table tbody tr");

    // Inside each row, these cells hold specific data
    // We use these as "relative" locators within a row
    private By productNameInRow     = By.cssSelector("td.cart_description h4 a");
    private By productPriceInRow    = By.cssSelector("td.cart_price p");
    private By productQuantityInRow = By.cssSelector("td.cart_quantity button");
    private By productTotalInRow    = By.cssSelector("td.cart_total p");
    private By deleteButtonInRow    = By.cssSelector("td.cart_delete a");

    // "Proceed To Checkout" button
    private By proceedToCheckoutBtn = By.cssSelector("a.btn.btn-default.check_out");

    // Modal that appears when non-logged-in user clicks checkout
    private By checkoutModalLogin    = By.cssSelector("a[href='/login']");
    private By checkoutModalRegister = By.cssSelector("a[href='/login']"); // same link

    /**
     * Constructor.
     */
    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the number of items in the cart.
     * @return  count of product rows in the cart table
     */
    public int getCartItemCount() {
        return driver.findElements(cartRows).size();
    }

    /**
     * Returns the product name at a given row position.
     *
     * @param rowNumber  1-based row number (1 = first item)
     * @return           product name text
     */
    public String getProductNameAtRow(int rowNumber) {
        List<WebElement> rows = driver.findElements(cartRows);
        // Get the product name cell within the specific row
        return rows.get(rowNumber - 1).findElement(productNameInRow).getText();
    }

    /**
     * Returns the price of the product at a given row.
     *
     * @param rowNumber  1-based row number
     * @return           price text (e.g., "Rs. 500")
     */
    public String getProductPriceAtRow(int rowNumber) {
        List<WebElement> rows = driver.findElements(cartRows);
        return rows.get(rowNumber - 1).findElement(productPriceInRow).getText();
    }

    /**
     * Returns the quantity of a product in the cart at a given row.
     *
     * @param rowNumber  1-based row number
     * @return           quantity as String (e.g., "1")
     */
    public String getProductQuantityAtRow(int rowNumber) {
        List<WebElement> rows = driver.findElements(cartRows);
        return rows.get(rowNumber - 1).findElement(productQuantityInRow).getText();
    }

    /**
     * Removes (deletes) the product at the given row by clicking its × button.
     *
     * @param rowNumber  1-based row number to remove
     */
    public void removeProductAtRow(int rowNumber) {
        List<WebElement> rows = driver.findElements(cartRows);
        rows.get(rowNumber - 1).findElement(deleteButtonInRow).click();

        // Wait for the row to disappear after deletion
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Checks if a product with the given name exists in the cart.
     *
     * @param productName  name to look for (partial match)
     * @return             true if found, false otherwise
     */
    public boolean isProductInCart(String productName) {
        List<WebElement> rows = driver.findElements(cartRows);
        for (WebElement row : rows) {
            // findElements on a specific row — checks within that row only
            List<WebElement> nameCells = row.findElements(productNameInRow);
            if (!nameCells.isEmpty() && nameCells.get(0).getText().contains(productName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clicks "Proceed To Checkout".
     * If not logged in, a modal appears asking to register/login.
     */
    public void clickProceedToCheckout() {
        click(proceedToCheckoutBtn);
    }

    /**
     * After clicking Proceed to Checkout as a guest,
     * clicks "Register / Login" in the popup modal.
     */
    public void clickRegisterLoginInModal() {
        click(checkoutModalLogin);
    }
}
