package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * ProductsPage.java
 *
 * PURPOSE:
 *   Represents the PRODUCTS page (/products).
 *   Users can: view all products, search for products,
 *              hover to add to cart, and view product details.
 *
 * URL: https://automationexercise.com/products
 */
public class ProductsPage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    // Page heading to verify we're on the right page
    private By allProductsHeading = By.xpath("//h2[contains(.,'All Products')]");

    // Search form
    private By searchInput     = By.id("search_product");
    private By searchButton    = By.id("submit_search");
    private By searchedHeading = By.xpath("//h2[contains(.,'Searched Products')]");

    // Product cards (each product in the grid is wrapped in this container)
    // This locator selects ALL product cards on the page
    private By allProductCards = By.cssSelector(".features_items .product-image-wrapper");

    // "Add to cart" button that appears on HOVER over a product card
    // :nth-child(N) selects the Nth product — we'll build this dynamically
    private By addToCartOverlay = By.cssSelector(".product-overlay .add-to-cart");

    // Modal that appears AFTER clicking "Add to Cart"
    private By modalContinueShopping = By.cssSelector("button[data-dismiss='modal']");
    private By modalViewCart         = By.cssSelector("p.text-center a[href='/view_cart']");
    private By modalTitle            = By.cssSelector("#cartModal .modal-title");

    // "View Product" links (one per product)
    private By viewProductLinks = By.cssSelector("a[href*='product_details']");

    /**
     * Constructor.
     */
    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies we are on the All Products page.
     * @return  true if "All Products" heading is visible
     */
    public boolean isOnProductsPage() {
        return isDisplayed(allProductsHeading);
    }

    /**
     * Returns the total number of product cards visible on the page.
     * Useful for verifying search results count.
     *
     * @return  number of visible product cards
     */
    public int getProductCount() {
        // driver.findElements() returns a List of ALL matching elements
        // (unlike findElement which returns only the first one)
        List<WebElement> products = driver.findElements(allProductCards);
        return products.size();
    }

    /**
     * Types a keyword in the search box and clicks Search.
     *
     * @param keyword  search term (e.g., "top", "tshirt")
     */
    public void searchForProduct(String keyword) {
        type(searchInput, keyword);
        click(searchButton);
    }

    /**
     * Checks if "SEARCHED PRODUCTS" heading is visible after searching.
     * @return  true if the section heading appeared
     */
    public boolean isSearchResultsVisible() {
        return isDisplayed(searchedHeading);
    }

    /**
     * Hovers over a product (by its position in the grid) and clicks Add to Cart.
     *
     * Products are 1-indexed here: product 1 = first product, product 2 = second, etc.
     *
     * HOW THIS WORKS:
     *   1. Get all product cards as a list
     *   2. Hover over the Nth card (index = productNumber - 1)
     *   3. The hidden "Add to Cart" button appears on hover
     *   4. Click the "Add to Cart" button
     *
     * @param productNumber  1-based product index (1 = first product)
     */
    public void hoverAndAddToCart(int productNumber) {
        // Get all product card elements
        List<WebElement> products = driver.findElements(allProductCards);

        // Convert 1-based index to 0-based (list index starts at 0)
        int index = productNumber - 1;

        // Hover over the product card to make the overlay buttons appear
        actions.moveToElement(products.get(index)).perform();

        // Wait briefly for the hover animation to complete (CSS transition)
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Find all "Add to cart" buttons that are now visible (one per product card that's hovered)
        List<WebElement> addButtons = driver.findElements(addToCartOverlay);
        // Click the one at the same index as our product
        addButtons.get(index).click();
    }

    /**
     * Clicks "Continue Shopping" in the "Added to Cart" modal.
     * Closes the modal and stays on the products page.
     */
    public void clickContinueShopping() {
        wait.waitForElementClickable(modalContinueShopping);
        click(modalContinueShopping);
    }

    /**
     * Clicks "View Cart" in the "Added to Cart" modal.
     * Navigates to the cart page.
     */
    public void clickViewCartInModal() {
        wait.waitForElementClickable(modalViewCart);
        click(modalViewCart);
    }

    /**
     * Clicks "View Product" link for a specific product.
     * Opens the product detail page.
     *
     * @param productNumber  1-based product index
     */
    public void clickViewProduct(int productNumber) {
        List<WebElement> links = driver.findElements(viewProductLinks);
        links.get(productNumber - 1).click();
    }
}
