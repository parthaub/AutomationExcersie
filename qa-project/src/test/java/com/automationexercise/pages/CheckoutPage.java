package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutPage.java
 *
 * PURPOSE:
 *   Represents the CHECKOUT PAGE (/checkout).
 *   Shows: delivery address, order summary, comment box, payment form.
 *
 * URL: https://automationexercise.com/checkout
 */
public class CheckoutPage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    // Checkout section headings (to verify we're on the right page)
    private By addressDetailsHeading = By.xpath("//h2[@class='heading' and contains(.,'Address Details')]");
    private By reviewOrderHeading    = By.xpath("//h2[@class='heading' and contains(.,'Review Your Order')]");

    // Delivery address fields (verify address matches registered address)
    private By deliveryFullName      = By.xpath("//ul[@id='address_delivery']//li[@class='address_firstname address_lastname']");
    private By deliveryAddress       = By.xpath("//ul[@id='address_delivery']//li[@class='address_address1 address_address2']");

    // Order comment text area
    private By orderCommentBox       = By.cssSelector("textarea.form-control");

    // "Place Order" button
    private By placeOrderBtn         = By.xpath("//a[@href='/payment' and contains(.,'Place Order')]");

    // ─── Payment Form Locators ────────────────────────────────────────────────
    private By cardNameInput         = By.cssSelector("input[data-qa='name-on-card']");
    private By cardNumberInput       = By.cssSelector("input[data-qa='card-number']");
    private By cardCvcInput          = By.cssSelector("input[data-qa='cvc']");
    private By cardExpiryMonthInput  = By.cssSelector("input[data-qa='expiry-month']");
    private By cardExpiryYearInput   = By.cssSelector("input[data-qa='expiry-year']");
    private By payAndConfirmBtn      = By.cssSelector("button[data-qa='pay-button']");

    // Order success confirmation
    // The site shows a green alert or a specific heading after payment
    private By orderSuccessAlert     = By.cssSelector("div.alert-success");
    private By orderSuccessHeading   = By.xpath("//h2[contains(.,'Order Placed')]");
    private By downloadInvoiceBtn    = By.cssSelector("a.btn-default[href*='invoice']");

    /**
     * Constructor.
     */
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks if the Address Details section is visible.
     * Use to confirm we arrived on the checkout page.
     */
    public boolean isCheckoutPageVisible() {
        return isDisplayed(addressDetailsHeading);
    }

    /**
     * Returns the delivery name shown in the address section.
     * Useful to verify the correct user's address is displayed.
     */
    public String getDeliveryFullName() {
        return getText(deliveryFullName);
    }

    /**
     * Types a comment/message in the order comment box.
     * @param comment  e.g., "Please leave at door"
     */
    public void enterOrderComment(String comment) {
        type(orderCommentBox, comment);
    }

    /**
     * Clicks "Place Order" button — navigates to the payment page.
     */
    public void clickPlaceOrder() {
        click(placeOrderBtn);
    }

    /**
     * Fills in all payment card fields.
     *
     * @param cardName    name on the card
     * @param cardNumber  card number (use "4111111111111111" for test Visa)
     * @param cvc         3-digit security code
     * @param expMonth    expiry month (e.g., "12")
     * @param expYear     expiry year (e.g., "2028")
     */
    public void fillPaymentDetails(String cardName, String cardNumber,
                                   String cvc, String expMonth, String expYear) {
        type(cardNameInput,        cardName);
        type(cardNumberInput,      cardNumber);
        type(cardCvcInput,         cvc);
        type(cardExpiryMonthInput, expMonth);
        type(cardExpiryYearInput,  expYear);
    }

    /**
     * Clicks "Pay and Confirm Order" to submit payment.
     */
    public void clickPayAndConfirm() {
        click(payAndConfirmBtn);
    }

    /**
     * Checks if the order success message is displayed after payment.
     * @return  true if order was placed successfully
     */
    public boolean isOrderPlacedSuccessfully() {
        return isDisplayed(orderSuccessAlert) || isDisplayed(orderSuccessHeading);
    }

    /**
     * Returns the success message text.
     */
    public String getOrderSuccessMessage() {
        if (isDisplayed(orderSuccessAlert)) {
            return getText(orderSuccessAlert);
        }
        return getText(orderSuccessHeading);
    }

    /**
     * CONVENIENCE METHOD: fills payment form and confirms in one call.
     */
    public void completePayment(String cardName, String cardNumber,
                                String cvc, String expMonth, String expYear) {
        fillPaymentDetails(cardName, cardNumber, cvc, expMonth, expYear);
        clickPayAndConfirm();
    }
}
