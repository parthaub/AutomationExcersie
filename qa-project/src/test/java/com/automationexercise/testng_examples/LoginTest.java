package com.automationexercise.testng_examples;

import com.automationexercise.listeners.RetryAnalyzer;
import com.automationexercise.listeners.TestListener;
import com.automationexercise.pages.HomePage;
import com.automationexercise.pages.LoginPage;
import com.automationexercise.utils.ConfigReader;
import com.automationexercise.utils.DriverFactory;
import com.automationexercise.utils.ExtentManager;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

/**
 * LoginTest.java
 *
 * PURPOSE:
 *   Demonstrates ALL major TestNG annotations in a real test context.
 *   This is NOT a Cucumber test — it's a PURE TestNG test class.
 *
 * TESTNG ANNOTATION EXECUTION ORDER (for one test class):
 *
 *   @BeforeSuite    → runs once before ALL test classes in the suite
 *   @BeforeTest     → runs once before all @Test methods in this <test> block
 *   @BeforeClass    → runs once before the first @Test method in THIS class
 *   @BeforeMethod   → runs before EACH @Test method
 *   @Test           → the actual test method
 *   @AfterMethod    → runs after EACH @Test method
 *   @AfterClass     → runs once after the last @Test method in THIS class
 *   @AfterTest      → runs once after all @Test methods in this <test> block
 *   @AfterSuite     → runs once after ALL test classes in the suite
 *
 * @Listeners:
 *   Registers our TestListener to automatically take screenshots on failure
 *   and log pass/fail to the Extent report.
 */
@Listeners(TestListener.class)   // Attach our custom TestListener to this class
public class LoginTest {

    // Page objects — declared at class level so all @Test methods can use them
    private HomePage  homePage;
    private LoginPage loginPage;

    // Test data (in real projects, read this from Excel using ExcelUtils)
    private final String VALID_EMAIL    = "testautomation_qa@test.com"; // must be pre-registered
    private final String VALID_PASSWORD = "Test@1234";
    private final String INVALID_EMAIL  = "doesnotexist@test.com";
    private final String INVALID_PASS   = "WrongPassword999";
    private final String BASE_URL       = ConfigReader.getProperty("base.url");

    // ─────────────────────────────────────────────────────────────────────────
    //  SUITE-LEVEL SETUP (runs once for the entire suite)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeSuite: Runs ONCE before ALL test classes in the suite.
     * Use for global, one-time setup: initializing databases, reading config files.
     *
     * NOTE: In this project we use it to initialize the Extent report singleton.
     */
    @BeforeSuite
    public void beforeSuiteSetup() {
        System.out.println("[LoginTest] @BeforeSuite — Initializing report and global config");
        ExtentManager.getInstance();  // Create the Extent report HTML file
    }

    /**
     * @AfterSuite: Runs ONCE after ALL test classes in the suite.
     * Use for global cleanup: closing DB connections, flushing reports.
     */
    @AfterSuite
    public void afterSuiteTearDown() {
        System.out.println("[LoginTest] @AfterSuite — Flushing Extent report");
        ExtentManager.flushReport();   // IMPORTANT: saves the HTML file to disk
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TEST-LEVEL SETUP (runs once per <test> block in testng.xml)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeTest: Runs ONCE before all @Test methods in the same <test> block (in testng.xml).
     * The ITestContext parameter gives info about the <test> block (name, parameters, etc.)
     *
     * USE CASE: Set browser type, set test environment (dev/staging/prod).
     */
    @BeforeTest
    public void beforeTestSetup(ITestContext context) {
        // ITestContext gives us info about the TestNG <test> block this class belongs to
        System.out.println("[LoginTest] @BeforeTest — Test block: " + context.getName());
        System.out.println("[LoginTest] Browser: " + ConfigReader.getProperty("browser"));
    }

    /**
     * @AfterTest: Runs ONCE after all @Test methods in the <test> block.
     */
    @AfterTest
    public void afterTestCleanup() {
        System.out.println("[LoginTest] @AfterTest — Test block complete");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  CLASS-LEVEL SETUP (runs once for this test class)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeClass: Runs ONCE before the FIRST @Test in this class.
     * Use for class-level setup: DB connections, class-wide variables.
     *
     * NOTE: "static" is NOT required for @BeforeClass in TestNG (unlike JUnit).
     */
    @BeforeClass
    public void classSetup() {
        System.out.println("[LoginTest] @BeforeClass — Class-level setup");
        // Example: could set up test data in DB here that all tests in this class use
    }

    /**
     * @AfterClass: Runs ONCE after the LAST @Test in this class.
     * Use for class-level cleanup.
     */
    @AfterClass
    public void classTearDown() {
        System.out.println("[LoginTest] @AfterClass — Class-level teardown");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHOD-LEVEL SETUP (runs before/after EACH @Test)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeMethod: Runs before EVERY @Test method in this class.
     * This is where we:
     *   1. Launch the browser (DriverFactory.initDriver())
     *   2. Navigate to the page
     *   3. Initialize Page Objects
     *
     * This ensures every test starts from a CLEAN STATE.
     * Each test gets its own fresh browser — no state from previous tests.
     */
    @BeforeMethod
    public void methodSetup() {
        System.out.println("[LoginTest] @BeforeMethod — Launching browser");

        // Open the browser (creates a new WebDriver instance)
        DriverFactory.initDriver();

        // Initialize page objects using the fresh WebDriver
        homePage  = new HomePage(DriverFactory.getDriver());
        loginPage = new LoginPage(DriverFactory.getDriver());

        // Navigate to the application
        DriverFactory.getDriver().get(BASE_URL);
    }

    /**
     * @AfterMethod: Runs after EVERY @Test method.
     * Always close the browser here, whether the test passed or failed.
     * This prevents browser instances from piling up.
     */
    @AfterMethod
    public void methodTearDown() {
        System.out.println("[LoginTest] @AfterMethod — Closing browser");
        DriverFactory.quitDriver();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  TEST METHODS (@Test)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @Test: Marks a method as a TestNG test.
     *
     * @Test attributes:
     *   description   = human-readable description (shown in reports)
     *   groups        = tag-like grouping (run specific groups)
     *   priority      = execution order (lower number = runs first; default = 0)
     *   enabled       = false to SKIP this test without deleting it
     *   retryAnalyzer = class to use for automatic retry on failure
     *   dependsOnMethods = run this test ONLY IF the listed tests passed
     *   timeOut       = fail if test takes longer than N milliseconds
     *   dataProvider  = name of @DataProvider method to use for data-driven testing
     */
    @Test(
        description    = "TC-001: Verify user can login with valid credentials",
        groups         = {"smoke", "regression", "auth"},
        priority       = 1,                                // Run this test FIRST
        retryAnalyzer  = RetryAnalyzer.class               // Retry up to 2 times if fails
    )
    public void testValidLogin() {
        // STEP 1: Go to login page
        homePage.clickSignupLogin();
        ExtentManager.logInfo("Navigated to login page");

        // STEP 2: Verify login page is displayed
        Assert.assertTrue(loginPage.isLoginPageVisible(),
            "Login page heading should be visible");
        ExtentManager.logInfo("Login page is displayed");

        // STEP 3: Enter credentials and login
        loginPage.login(VALID_EMAIL, VALID_PASSWORD);
        ExtentManager.logInfo("Entered credentials and clicked Login");

        // STEP 4: Verify successful login
        // Assert.assertTrue = passes if the condition is true, fails otherwise
        Assert.assertTrue(homePage.isLoggedIn(),
            "User should be logged in after valid credentials");

        // Also verify the username appears in header
        String username = homePage.getLoggedInUsername();
        Assert.assertFalse(username.isEmpty(),
            "Username should appear in header after login");

        ExtentManager.logPass("User successfully logged in as: " + username);
    }

    /**
     * Negative test: wrong password should show error message.
     * priority = 2 means this runs AFTER testValidLogin (priority 1).
     */
    @Test(
        description   = "TC-002: Verify error message for incorrect password",
        groups        = {"regression", "auth", "negative"},
        priority      = 2,
        retryAnalyzer = RetryAnalyzer.class
    )
    public void testInvalidPasswordShowsError() {
        // Navigate to login page
        homePage.clickSignupLogin();

        // Enter valid email but wrong password
        loginPage.login(VALID_EMAIL, INVALID_PASS);
        ExtentManager.logInfo("Attempted login with wrong password");

        // Verify error message is shown
        Assert.assertTrue(loginPage.isLoginErrorVisible(),
            "Error message should be visible for incorrect password");

        // Verify the exact error text
        String actualError = loginPage.getLoginErrorMessage();
        Assert.assertTrue(
            actualError.contains("Your email or password is incorrect!"),
            "Error should say 'Your email or password is incorrect!' but was: " + actualError
        );

        ExtentManager.logPass("Correct error message shown for invalid login");
    }

    /**
     * Test using dependsOnMethods:
     * This test ONLY runs if testValidLogin() PASSED.
     * If testValidLogin skipped or failed, this test is automatically SKIPPED.
     *
     * USE CASE: testLogout needs a logged-in state, which only happens after login.
     *           No point running logout if login itself failed.
     */
    @Test(
        description       = "TC-003: Verify user can logout successfully",
        groups            = {"regression", "auth"},
        priority          = 3,
        dependsOnMethods  = {"testValidLogin"}  // Only run if TC-001 passed
    )
    public void testLogout() {
        // Setup: login first
        homePage.clickSignupLogin();
        loginPage.login(VALID_EMAIL, VALID_PASSWORD);

        // Verify logged in
        Assert.assertTrue(homePage.isLoggedIn(), "Should be logged in before testing logout");
        ExtentManager.logInfo("User is logged in, proceeding to logout");

        // Click Logout
        homePage.clickLogout();
        ExtentManager.logInfo("Clicked Logout");

        // Verify we're back on the login page
        Assert.assertTrue(loginPage.isLoginPageVisible(),
            "Should be on login page after logout");

        // Verify "Logged in as" text is GONE
        Assert.assertFalse(homePage.isLoggedIn(),
            "User should NOT be logged in after logout");

        ExtentManager.logPass("User successfully logged out");
    }

    /**
     * Skipped test example.
     * enabled = false means TestNG will SKIP this test entirely.
     * Useful for: WIP tests, known broken tests you don't want to delete yet.
     *
     * The test appears as "SKIPPED" in reports (not FAILED).
     */
    @Test(
        description = "TC-004: Placeholder test (currently disabled)",
        groups      = {"regression"},
        enabled     = false    // ← This test is SKIPPED
    )
    public void testPlaceholder() {
        // This method body never runs while enabled = false
        Assert.fail("This should never run");
    }

    /**
     * Test with TIMEOUT.
     * If this test takes longer than 10 seconds, TestNG marks it as FAILED.
     * Useful for catching infinite loops or extremely slow pages.
     */
    @Test(
        description = "TC-005: Verify home page loads within time limit",
        groups      = {"smoke"},
        priority    = 0,        // Priority 0 = runs BEFORE priority 1
        timeOut     = 10000     // 10,000 milliseconds = 10 seconds
    )
    public void testHomePageLoadsQuickly() {
        // @BeforeMethod already navigated to the page
        // Just verify it loaded correctly
        Assert.assertTrue(homePage.isHomePageVisible(),
            "Home page should load within 10 seconds");

        ExtentManager.logPass("Home page loaded within time limit");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DATA PROVIDER — Data-Driven Testing
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @DataProvider: Supplies test data to a @Test method.
     * The method returns a 2D Object array:
     *   - Outer array = rows (each row = one test run)
     *   - Inner array = columns (each column = one parameter)
     *
     * The @Test method with dataProvider = "loginData" will run ONCE PER ROW.
     * 4 rows below = test runs 4 times with different data.
     *
     * @return  2D array of test data: {{email1, password1, expected1}, {email2, ...}, ...}
     */
    @DataProvider(name = "loginData")
    public Object[][] provideLoginData() {
        // Each inner array = one set of inputs for one test run
        // Column 0 = email, Column 1 = password, Column 2 = expected result
        return new Object[][] {
            // Row 1: valid credentials → should succeed
            { "validuser@test.com",   "Test@1234",      "success" },
            // Row 2: wrong password → should fail
            { "validuser@test.com",   "WrongPass123",   "failure" },
            // Row 3: unregistered email → should fail
            { "notexist@test.com",    "Test@1234",      "failure" },
            // Row 4: empty email → should fail
            { "",                     "Test@1234",      "failure" },
        };
    }

    /**
     * DATA-DRIVEN TEST using @DataProvider.
     * This test runs ONCE FOR EACH ROW in loginData (4 times total).
     *
     * TestNG automatically passes the row values as method parameters:
     *   Row 1: testLoginWithMultipleUsers("validuser@test.com", "Test@1234", "success")
     *   Row 2: testLoginWithMultipleUsers("validuser@test.com", "WrongPass123", "failure")
     *   etc.
     *
     * dataProvider = "loginData" links this test to the @DataProvider above.
     */
    @Test(
        description  = "TC-006: Data-driven login test with multiple credentials",
        groups       = {"regression", "auth", "data-driven"},
        priority     = 4,
        dataProvider = "loginData"   // Must match the @DataProvider name exactly
    )
    public void testLoginWithMultipleUsers(String email, String password, String expectedResult) {
        // Log what data this run is using (helps debug when one row fails)
        ExtentManager.logInfo("Testing login with email: " + email + " | expected: " + expectedResult);

        // Navigate to login page
        homePage.clickSignupLogin();

        // Attempt login
        loginPage.login(email, password);

        // Assert based on expected result
        if (expectedResult.equals("success")) {
            Assert.assertTrue(homePage.isLoggedIn(),
                "Login should succeed for: " + email);
            ExtentManager.logPass("Login succeeded as expected");

        } else {
            // Either error message shown OR still on login page
            boolean errorShown  = loginPage.isLoginErrorVisible();
            boolean onLoginPage = loginPage.isLoginPageVisible();
            Assert.assertTrue(errorShown || onLoginPage,
                "Login should fail for: " + email + " but user appears logged in");
            ExtentManager.logPass("Login failed as expected for: " + email);
        }
    }
}
