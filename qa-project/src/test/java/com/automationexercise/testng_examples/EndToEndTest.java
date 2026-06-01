package com.automationexercise.testng_examples;

import com.automationexercise.listeners.TestListener;
import com.automationexercise.models.TestDataModel;
import com.automationexercise.pages.*;
import com.automationexercise.utils.ConfigReader;
import com.automationexercise.utils.DriverFactory;
import com.automationexercise.utils.ExtentManager;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.*;

/**
 * EndToEndTest.java
 *
 * PURPOSE:
 *   Demonstrates a COMPLETE end-to-end test flow using TestNG annotations.
 *   This test covers the full user journey:
 *     Register → Browse Products → Add to Cart → Checkout → Verify Order → Delete Account
 *
 *   This is how real QA engineers test "critical paths" — the most important
 *   workflows that MUST work for the business to function.
 *
 * TESTNG ANNOTATIONS SHOWN HERE:
 *   @BeforeSuite, @AfterSuite
 *   @BeforeTest, @AfterTest
 *   @BeforeClass, @AfterClass
 *   @BeforeMethod, @AfterMethod
 *   @Test (with description, groups, priority, dependsOnMethods, timeOut)
 *   @DataProvider
 *   @Parameters (reads values from testng.xml <parameter> tags)
 */
@Listeners(TestListener.class)
public class EndToEndTest {

    // ─── Page Objects ─────────────────────────────────────────────────────────
    private HomePage     homePage;
    private LoginPage    loginPage;
    private RegisterPage registerPage;
    private ProductsPage productsPage;
    private CartPage     cartPage;
    private CheckoutPage checkoutPage;

    // ─── Shared test state ────────────────────────────────────────────────────
    // We store the registered email so later tests (cart, checkout) can log in
    private String registeredEmail;
    private String registeredPassword = "Test@1234";

    // ─────────────────────────────────────────────────────────────────────────
    //  SUITE LEVEL
    // ─────────────────────────────────────────────────────────────────────────

    @BeforeSuite
    public void initSuite() {
        System.out.println("[EndToEndTest] Suite starting — initializing Extent report");
        ExtentManager.getInstance();
    }

    @AfterSuite
    public void tearDownSuite() {
        System.out.println("[EndToEndTest] Suite finished — flushing report");
        ExtentManager.flushReport();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TEST LEVEL
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeTest with ITestContext parameter.
     * ITestContext lets you read <parameter> values from testng.xml:
     *   <parameter name="env" value="staging"/>
     * context.getCurrentXmlTest().getParameter("env")  →  "staging"
     */
    @BeforeTest
    public void setTestEnvironment(ITestContext context) {
        // Read "env" parameter from testng.xml (if it exists)
        // This lets you switch between dev/staging/prod via XML config
        String env = context.getCurrentXmlTest().getParameter("env");
        if (env != null) {
            System.out.println("[EndToEndTest] Running against environment: " + env);
        }
    }

    @AfterTest
    public void afterTestLog() {
        System.out.println("[EndToEndTest] Test block complete");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CLASS LEVEL
    // ─────────────────────────────────────────────────────────────────────────

    @BeforeClass
    public void classInit() {
        System.out.println("[EndToEndTest] BeforeClass — generating unique test email");
        // Generate a unique email for this entire class's test run
        // Using timestamp ensures no conflicts with previous runs
        registeredEmail = "e2e_test_" + System.currentTimeMillis() + "@test.com";
        System.out.println("[EndToEndTest] Test email: " + registeredEmail);
    }

    @AfterClass
    public void classCleanup() {
        System.out.println("[EndToEndTest] AfterClass — all E2E tests completed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHOD LEVEL
    // ─────────────────────────────────────────────────────────────────────────

    /** Opens a fresh browser before EACH @Test */
    @BeforeMethod
    public void openBrowser() {
        DriverFactory.initDriver();
        initPageObjects();
        DriverFactory.getDriver().get(ConfigReader.getProperty("base.url"));
    }

    /** Closes the browser after EACH @Test */
    @AfterMethod
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    /** Helper: re-creates all page objects after browser opens */
    private void initPageObjects() {
        homePage     = new HomePage(DriverFactory.getDriver());
        loginPage    = new LoginPage(DriverFactory.getDriver());
        registerPage = new RegisterPage(DriverFactory.getDriver());
        productsPage = new ProductsPage(DriverFactory.getDriver());
        cartPage     = new CartPage(DriverFactory.getDriver());
        checkoutPage = new CheckoutPage(DriverFactory.getDriver());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TEST METHODS — Full E2E flow in priority order
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * E2E Step 1: Register a new user account.
     * MUST pass before any other E2E test runs (they all depend on this).
     */
    @Test(
        description = "E2E-001: Register a new user account",
        groups      = {"e2e", "regression"},
        priority    = 1
    )
    public void testRegisterNewUser() {
        ExtentManager.logInfo("Step 1: Registering new user with email: " + registeredEmail);

        // Go to login page
        homePage.clickSignupLogin();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "Should be on login page");

        // Sign up with name + email
        loginPage.signUp("E2E Test User", registeredEmail);
        ExtentManager.logInfo("Signup form submitted");

        // Verify account info form appeared
        Assert.assertTrue(registerPage.isAccountFormVisible(),
            "Account information form should appear after signup");

        // Fill the full registration form using TestDataModel convenience method
        TestDataModel user = TestDataModel.defaultTestUser();
        registerPage.fillCompleteRegistrationForm(
            user.getTitle(),   user.getPassword(),
            user.getDobDay(),  user.getDobMonth(), user.getDobYear(),
            user.getFirstName(), user.getLastName(), user.getAddress(),
            user.getCountry(), user.getState(), user.getCity(),
            user.getZipcode(), user.getMobileNumber()
        );

        // Verify "Account Created!" appeared
        Assert.assertTrue(registerPage.isAccountCreated(),
            "Account Created! message should appear");
        ExtentManager.logInfo("Account Created! confirmation visible");

        // Click Continue to go to home page (logged in)
        registerPage.clickContinue();

        // Verify logged in
        Assert.assertTrue(homePage.isLoggedIn(),
            "User should be logged in after registration");
        ExtentManager.logPass("User registered and logged in: " + registeredEmail);
    }

    /**
     * E2E Step 2: Browse products and add to cart.
     * dependsOnMethods = this test SKIPS if testRegisterNewUser failed.
     */
    @Test(
        description      = "E2E-002: Browse products and add two items to cart",
        groups           = {"e2e", "regression"},
        priority         = 2,
        dependsOnMethods = {"testRegisterNewUser"}
    )
    public void testBrowseAndAddToCart() {
        // Login first (each test gets fresh browser from @BeforeMethod)
        homePage.clickSignupLogin();
        loginPage.login(registeredEmail, registeredPassword);
        Assert.assertTrue(homePage.isLoggedIn(), "Must be logged in for this test");

        ExtentManager.logInfo("Step 2: Adding products to cart");

        // Go to products page
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isOnProductsPage(), "Should be on products page");

        // Add first product to cart
        productsPage.hoverAndAddToCart(1);
        productsPage.clickContinueShopping();
        ExtentManager.logInfo("Product 1 added to cart");

        // Add second product to cart
        productsPage.hoverAndAddToCart(2);
        productsPage.clickViewCartInModal();
        ExtentManager.logInfo("Product 2 added, navigated to cart");

        // Verify cart has 2 items
        int cartCount = cartPage.getCartItemCount();
        Assert.assertEquals(cartCount, 2,
            "Cart should have 2 products but has: " + cartCount);

        // Log what's in the cart
        for (int i = 1; i <= cartCount; i++) {
            String name  = cartPage.getProductNameAtRow(i);
            String price = cartPage.getProductPriceAtRow(i);
            ExtentManager.logInfo("  Cart row " + i + ": " + name + " — " + price);
        }

        ExtentManager.logPass("2 products added to cart successfully");
    }

    /**
     * E2E Step 3: Complete checkout and place order.
     */
    @Test(
        description      = "E2E-003: Complete checkout and place an order",
        groups           = {"e2e", "regression"},
        priority         = 3,
        dependsOnMethods = {"testBrowseAndAddToCart"},
        timeOut          = 60000   // Fail if this test takes more than 60 seconds
    )
    public void testCheckoutAndPlaceOrder() {
        // Login
        homePage.clickSignupLogin();
        loginPage.login(registeredEmail, registeredPassword);

        // Go to cart
        homePage.clickCart();
        ExtentManager.logInfo("Step 3: Starting checkout process");

        // Proceed to checkout
        cartPage.clickProceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageVisible(),
            "Checkout page should show address details");
        ExtentManager.logInfo("Checkout page visible");

        // Verify delivery name is visible
        String deliveryName = checkoutPage.getDeliveryFullName();
        Assert.assertFalse(deliveryName.isEmpty(), "Delivery name should be displayed");
        ExtentManager.logInfo("Delivery name: " + deliveryName);

        // Add order comment
        checkoutPage.enterOrderComment("Automated E2E test order — please ignore");

        // Place the order (goes to payment page)
        checkoutPage.clickPlaceOrder();
        ExtentManager.logInfo("Clicked Place Order — on payment page");

        // Fill payment details (using a standard Visa test number)
        checkoutPage.fillPaymentDetails(
            "E2E Test User",       // Name on card
            "4111111111111111",    // Visa test card number
            "123",                 // CVC
            "12",                  // Expiry month
            "2028"                 // Expiry year
        );
        ExtentManager.logInfo("Payment details filled");

        // Confirm payment
        checkoutPage.clickPayAndConfirm();

        // Verify order success
        Assert.assertTrue(checkoutPage.isOrderPlacedSuccessfully(),
            "Order success message should appear after payment");

        ExtentManager.logPass("Order placed successfully! Message: " + checkoutPage.getOrderSuccessMessage());
    }

    /**
     * E2E Step 4: Delete the test account (cleanup).
     * We always clean up test data so the next run can use a fresh state.
     * alwaysRun = true means this runs EVEN IF previous tests failed.
     */
    @Test(
        description      = "E2E-004: Delete test account (cleanup)",
        groups           = {"e2e", "regression"},
        priority         = 4,
        dependsOnMethods = {"testRegisterNewUser"},
        alwaysRun        = true    // Run cleanup even if other tests failed
    )
    public void testDeleteAccount() {
        // Login to the account we need to delete
        homePage.clickSignupLogin();
        loginPage.login(registeredEmail, registeredPassword);

        if (!homePage.isLoggedIn()) {
            // If we can't log in, the account might already be gone — skip cleanup
            throw new SkipException(
                "Cannot log in to delete account — may already be deleted. Email: " + registeredEmail
            );
        }

        ExtentManager.logInfo("Step 4: Deleting test account: " + registeredEmail);

        // Click Delete Account
        homePage.clickDeleteAccount();

        // Verify deletion confirmation
        Assert.assertTrue(registerPage.isAccountDeleted(),
            "Account Deleted! message should appear");

        ExtentManager.logPass("Test account deleted successfully. Cleanup complete.");
    }
}
