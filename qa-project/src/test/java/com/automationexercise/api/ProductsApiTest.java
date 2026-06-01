package com.automationexercise.api;

import com.automationexercise.listeners.RetryAnalyzer;
import com.automationexercise.listeners.TestListener;
import com.automationexercise.utils.ConfigReader;
import com.automationexercise.utils.ExtentManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * ProductsApiTest.java
 *
 * PURPOSE:
 *   Tests the AutomationExercise REST API using REST Assured.
 *   These tests send real HTTP requests to the API and verify the responses.
 *   NO browser is opened — purely backend/API testing.
 *
 * REST ASSURED BASICS:
 *   REST Assured uses a fluent "given-when-then" syntax:
 *
 *   given()   → set up the request (headers, body, params, base URL)
 *   .when()   → specify the HTTP method and endpoint
 *   .then()   → assert the response (status code, body content)
 *
 * EXAMPLE:
 *   given()
 *     .baseUri("https://api.example.com")
 *     .contentType(ContentType.JSON)
 *   .when()
 *     .get("/users")
 *   .then()
 *     .statusCode(200)
 *     .body("users.size()", greaterThan(0));
 *
 * API ENDPOINTS WE TEST (from AutomationExercise docs):
 *   GET  /api/productsList          → returns all products
 *   POST /api/productsList          → 405 (method not supported)
 *   GET  /api/brandsList            → returns all brands
 *   POST /api/searchProduct         → searches products by keyword
 *   POST /api/verifyLogin           → validates user credentials
 *   POST /api/createAccount         → creates a new user
 *   DELETE /api/deleteAccount       → deletes a user
 *   PUT  /api/updateAccount         → updates user details
 *   GET  /api/getUserDetailByEmail  → gets user info by email
 */
@Listeners(TestListener.class)   // Attach listener for Extent report logging
public class ProductsApiTest {

    // The base URL prefix used for all API requests in this class
    private String apiBaseUrl;

    // RequestSpecification holds COMMON settings (base URL, content type)
    // so we don't repeat them in every test method
    private RequestSpecification requestSpec;

    // ─────────────────────────────────────────────────────────────────────────
    //  SETUP & TEARDOWN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * @BeforeSuite: Runs once. Initializes Extent report for this API test run.
     */
    @BeforeSuite
    public void initReport() {
        ExtentManager.getInstance();
    }

    /**
     * @AfterSuite: Saves the Extent report to disk after all API tests finish.
     */
    @AfterSuite
    public void flushReport() {
        ExtentManager.flushReport();
    }

    /**
     * @BeforeClass: Runs once before any @Test in this class.
     * We configure the base URL and common request settings here.
     *
     * RestAssured.baseURI sets a global default so we only type the ENDPOINT
     * path in each test (e.g., "/productsList") not the full URL every time.
     */
    @BeforeClass
    public void setupApiConfig() {
        // Read API base URL from config.properties
        apiBaseUrl = ConfigReader.getProperty("api.base.url");

        // Set the global base URI for all RestAssured requests in this class
        // After this, .get("/productsList") is equivalent to .get(apiBaseUrl + "/productsList")
        RestAssured.baseURI = apiBaseUrl;

        // Disable URL encoding (some APIs need raw params)
        RestAssured.urlEncodingEnabled = false;

        // Enable logging of ALL requests and responses to the console
        // Great for debugging — remove or reduce in production for cleaner output
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Build a reusable RequestSpecification with settings every request needs
        // This avoids copy-pasting header setup in every test
        requestSpec = RestAssured.given()
            .accept(ContentType.JSON);       // We expect JSON responses

        System.out.println("[ProductsApiTest] API base URL: " + apiBaseUrl);
    }

    /**
     * @BeforeMethod: Runs before EACH @Test method.
     * Creates a new Extent test node for this test.
     */
    @BeforeMethod
    public void beforeEachTest() {
        // Note: The test name is set properly by TestListener.onTestStart()
        // This is just extra initialization if needed
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PRODUCTS API TESTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-API-001: GET /api/productsList
     * Expected: 200 OK, JSON body with "products" array containing items.
     */
    @Test(
        description   = "TC-API-001: GET productsList returns 200 and non-empty list",
        groups        = {"api", "smoke", "products"},
        priority      = 1,
        retryAnalyzer = RetryAnalyzer.class
    )
    public void testGetAllProductsReturns200() {
        ExtentManager.logInfo("Sending GET request to /api/productsList");

        // ── given: set up the request ──────────────────────────────────────
        Response response = requestSpec
            .given()
                .log().uri()       // Log the full URL to console

        // ── when: send the request ──────────────────────────────────────────
            .when()
                .get("/productsList")

        // ── then: assert the response ───────────────────────────────────────
            .then()
                .log().status()    // Log response status code to console
                .statusCode(200)   // Assert HTTP 200 OK
                .extract()
                .response();       // Extract the full response object for further checks

        // ── Additional assertions on the response body ──────────────────────

        // Extract "responseCode" field from JSON body
        // JSON body looks like: { "responseCode": 200, "products": [...] }
        int responseCode = response.jsonPath().getInt("responseCode");
        Assert.assertEquals(responseCode, 200,
            "responseCode in body should be 200");

        // Extract the "products" array from the JSON body
        // jsonPath().getList("products") returns a List<Map<String, Object>>
        // Each map = one product object from the JSON array
        List<Map<String, Object>> products = response.jsonPath().getList("products");

        // Verify the list is not null
        Assert.assertNotNull(products, "Products array should not be null");

        // Verify at least one product exists
        Assert.assertTrue(products.size() > 0,
            "Products list should contain at least one product, but found: " + products.size());

        // Verify the FIRST product has required fields
        Map<String, Object> firstProduct = products.get(0);
        Assert.assertNotNull(firstProduct.get("id"),    "Product 'id' field is missing");
        Assert.assertNotNull(firstProduct.get("name"),  "Product 'name' field is missing");
        Assert.assertNotNull(firstProduct.get("price"), "Product 'price' field is missing");

        ExtentManager.logPass("GET /productsList returned 200 with " + products.size() + " products");
        System.out.println("[TC-API-001] Products found: " + products.size());
    }

    /**
     * TC-API-002: POST /api/productsList
     * Expected: 405 Method Not Allowed (the API doesn't support POST on this endpoint).
     * This is a NEGATIVE TEST — we're verifying the API correctly rejects wrong methods.
     */
    @Test(
        description   = "TC-API-002: POST productsList returns 405 Method Not Allowed",
        groups        = {"api", "regression", "negative"},
        priority      = 2,
        retryAnalyzer = RetryAnalyzer.class
    )
    public void testPostProductsListReturns405() {
        ExtentManager.logInfo("Sending POST request to /api/productsList (should be rejected)");

        // Send a POST request — the API should reject this with 405
        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)  // POST needs a content type
            .when()
                .post("/productsList")             // POST instead of GET
            .then()
                .log().status()
                .statusCode(405)                   // Expect 405 Not Allowed
                .extract().response();

        // Verify the body contains an error message about unsupported method
        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(message, "Response should contain a 'message' field");
        Assert.assertTrue(
            message.contains("request method is not supported") ||
            message.contains("This request method is not supported"),
            "Error message should mention unsupported method. Got: " + message
        );

        ExtentManager.logPass("POST /productsList correctly returned 405 with message: " + message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BRANDS API TESTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-API-003: GET /api/brandsList
     * Expected: 200 OK, JSON with "brands" array.
     */
    @Test(
        description = "TC-API-003: GET brandsList returns 200 and brand data",
        groups      = {"api", "regression", "brands"},
        priority    = 3
    )
    public void testGetBrandsListReturns200() {
        ExtentManager.logInfo("Sending GET request to /api/brandsList");

        Response response = requestSpec
            .given()
                .log().uri()
            .when()
                .get("/brandsList")
            .then()
                .statusCode(200)
                .extract().response();

        // Parse the brands array from the JSON response
        List<Map<String, Object>> brands = response.jsonPath().getList("brands");

        Assert.assertNotNull(brands, "Brands array should not be null");
        Assert.assertTrue(brands.size() > 0, "At least one brand should exist");

        // Log all brand names for visibility
        brands.forEach(brand -> System.out.println("  Brand: " + brand.get("brand")));

        ExtentManager.logPass("GET /brandsList returned " + brands.size() + " brands");
    }

    /**
     * TC-API-004: PUT /api/brandsList
     * Expected: 405 — another negative test for wrong HTTP method.
     */
    @Test(
        description = "TC-API-004: PUT brandsList returns 405",
        groups      = {"api", "regression", "negative"},
        priority    = 4
    )
    public void testPutBrandsListReturns405() {
        ExtentManager.logInfo("Sending PUT request to /api/brandsList (should fail)");

        requestSpec
            .given()
                .contentType(ContentType.URLENC)
            .when()
                .put("/brandsList")
            .then()
                .statusCode(405);

        ExtentManager.logPass("PUT /brandsList correctly returned 405");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  SEARCH API TESTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-API-005: POST /api/searchProduct
     * Expected: 200 OK, "products" array with matching items.
     *
     * REQUEST BODY: form-data with field "search_product" = "top"
     *
     * NOTE: This API uses application/x-www-form-urlencoded (form data),
     * NOT JSON. So we use .formParam() instead of .body().
     */
    @Test(
        description   = "TC-API-005: POST searchProduct with keyword returns matching results",
        groups        = {"api", "smoke", "search"},
        priority      = 5,
        retryAnalyzer = RetryAnalyzer.class
    )
    public void testSearchProductReturnsResults() {
        String searchKeyword = "top";
        ExtentManager.logInfo("Searching for product keyword: " + searchKeyword);

        Response response = requestSpec
            .given()
                // formParam() adds a key=value pair to the request body
                // as application/x-www-form-urlencoded (standard HTML form data)
                .contentType(ContentType.URLENC)
                .formParam("search_product", searchKeyword)
                .log().all()     // Log everything (url, headers, body) for debugging
            .when()
                .post("/searchProduct")
            .then()
                .log().body()    // Log the response body
                .statusCode(200)
                .extract().response();

        // Verify response code in body
        int responseCode = response.jsonPath().getInt("responseCode");
        Assert.assertEquals(responseCode, 200, "responseCode in body should be 200");

        // Verify products array is not empty
        List<Map<String, Object>> products = response.jsonPath().getList("products");
        Assert.assertNotNull(products, "products array should exist");
        Assert.assertTrue(products.size() > 0,
            "Search for '" + searchKeyword + "' should return at least 1 product");

        ExtentManager.logPass("Search for '" + searchKeyword + "' returned " + products.size() + " products");
    }

    /**
     * TC-API-006: POST /api/searchProduct with NO search_product parameter.
     * Expected: 400 Bad Request — the required parameter is missing.
     */
    @Test(
        description = "TC-API-006: POST searchProduct without parameter returns 400",
        groups      = {"api", "regression", "negative", "search"},
        priority    = 6
    )
    public void testSearchProductWithoutParamReturns400() {
        ExtentManager.logInfo("Sending search request WITHOUT required parameter");

        // Send POST without the required "search_product" form param
        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                // Intentionally NOT adding .formParam("search_product", ...)
            .when()
                .post("/searchProduct")
            .then()
                .statusCode(400)   // Expect 400 Bad Request
                .extract().response();

        // Verify error message mentions the missing parameter
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(
            message.contains("Bad request") || message.contains("search_product"),
            "Error message should mention missing parameter. Got: " + message
        );

        ExtentManager.logPass("Missing parameter correctly returned 400: " + message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  USER ACCOUNT API TESTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * TC-API-007: POST /api/verifyLogin
     * Expected: 200 — user credentials are valid.
     *
     * NOTE: For this test to pass, the email/password must belong to
     * a REAL registered account. The test uses pre-existing test data.
     */
    @Test(
        description = "TC-API-007: POST verifyLogin with valid credentials returns 200",
        groups      = {"api", "regression", "auth"},
        priority    = 7
    )
    public void testVerifyLoginWithValidCredentials() {
        // These credentials must exist in the system
        // In a real project, read from config or Excel
        String testEmail    = "automation_test_user@test.com";
        String testPassword = "Test@1234";

        ExtentManager.logInfo("Verifying login for: " + testEmail);

        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                .formParam("email",    testEmail)
                .formParam("password", testPassword)
            .when()
                .post("/verifyLogin")
            .then()
                .statusCode(200)
                .extract().response();

        // The API returns { "responseCode": 200, "message": "User exists!" }
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(
            message.contains("User exists"),
            "Message should confirm user exists. Got: " + message
        );

        ExtentManager.logPass("Login verified successfully: " + message);
    }

    /**
     * TC-API-010: POST /api/verifyLogin with INVALID credentials.
     * Expected: 404 — user not found.
     */
    @Test(
        description = "TC-API-010: POST verifyLogin with invalid credentials returns 404",
        groups      = {"api", "regression", "negative", "auth"},
        priority    = 10
    )
    public void testVerifyLoginWithInvalidCredentials() {
        ExtentManager.logInfo("Testing login with wrong credentials");

        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                .formParam("email",    "definitely_not_registered@fake.com")
                .formParam("password", "wrong_password_999")
            .when()
                .post("/verifyLogin")
            .then()
                .statusCode(404)   // 404 = user not found
                .extract().response();

        String message = response.jsonPath().getString("message");
        Assert.assertTrue(
            message.contains("User not found") || message.contains("not found"),
            "Should say user not found. Got: " + message
        );

        ExtentManager.logPass("Invalid credentials correctly returned 404: " + message);
    }

    /**
     * TC-API-011: POST /api/createAccount
     * Expected: 201 Created — new user account created.
     *
     * This test creates a new account, so we need to clean it up in TC-API-012.
     * We use a timestamp in the email to make it unique each test run.
     */
    @Test(
        description = "TC-API-011: POST createAccount with full details returns 201",
        groups      = {"api", "regression", "auth"},
        priority    = 11
    )
    public void testCreateAccountReturns201() {
        // Generate a unique email for this test run using timestamp
        // Without this, the second run would fail with "email already exists"
        long timestamp  = System.currentTimeMillis();
        String newEmail = "api_test_" + timestamp + "@test.com";

        ExtentManager.logInfo("Creating new account with email: " + newEmail);

        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                // All required fields for account creation
                .formParam("name",          "API Test User")
                .formParam("email",         newEmail)
                .formParam("password",      "ApiTest@1234")
                .formParam("title",         "Mr")
                .formParam("birth_date",    "15")
                .formParam("birth_month",   "6")
                .formParam("birth_year",    "1995")
                .formParam("firstname",     "API")
                .formParam("lastname",      "TestUser")
                .formParam("company",       "Test Corp")
                .formParam("address1",      "123 Test Street")
                .formParam("address2",      "Suite 100")
                .formParam("country",       "United States")
                .formParam("zipcode",       "10001")
                .formParam("state",         "New York")
                .formParam("city",          "New York City")
                .formParam("mobile_number", "1234567890")
            .when()
                .post("/createAccount")
            .then()
                .log().body()
                .statusCode(201)   // 201 = Created (standard for successful POST)
                .extract().response();

        // Verify the success message
        String message = response.jsonPath().getString("message");
        Assert.assertTrue(
            message.contains("User created"),
            "Should confirm user created. Got: " + message
        );

        ExtentManager.logPass("Account created successfully: " + message);
        System.out.println("[TC-API-011] New account email: " + newEmail);

        // Store the email in a system property so TC-API-012 can delete it
        // (In a real project, use a shared TestContext object instead)
        System.setProperty("created_email",    newEmail);
        System.setProperty("created_password", "ApiTest@1234");
    }

    /**
     * TC-API-012: DELETE /api/deleteAccount
     * Expected: 200 — user account deleted.
     *
     * Uses the account created by TC-API-011.
     * dependsOnMethods ensures this only runs if TC-API-011 passed.
     */
    @Test(
        description      = "TC-API-012: DELETE deleteAccount removes user and returns 200",
        groups           = {"api", "regression", "auth"},
        priority         = 12,
        dependsOnMethods = {"testCreateAccountReturns201"}  // Only run after TC-011
    )
    public void testDeleteAccountReturns200() {
        // Get the email that was created by TC-API-011
        String emailToDelete    = System.getProperty("created_email");
        String passwordToDelete = System.getProperty("created_password");

        if (emailToDelete == null) {
            // If TC-011 didn't set the property, skip with a clear message
            throw new org.testng.SkipException("No email from createAccount test — skipping delete");
        }

        ExtentManager.logInfo("Deleting account: " + emailToDelete);

        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                .formParam("email",    emailToDelete)
                .formParam("password", passwordToDelete)
            .when()
                .delete("/deleteAccount")    // HTTP DELETE method
            .then()
                .log().body()
                .statusCode(200)
                .extract().response();

        String message = response.jsonPath().getString("message");
        Assert.assertTrue(
            message.contains("Account deleted"),
            "Should confirm account deleted. Got: " + message
        );

        ExtentManager.logPass("Account deleted successfully: " + message);

        // Clean up the system properties we set in TC-011
        System.clearProperty("created_email");
        System.clearProperty("created_password");
    }

    /**
     * TC-API-014: GET /api/getUserDetailByEmail
     * Expected: 200, returns user's details as JSON.
     *
     * NOTE: Email is passed as a QUERY PARAMETER (?email=...) not in the body.
     */
    @Test(
        description = "TC-API-014: GET getUserDetailByEmail returns user details",
        groups      = {"api", "regression", "auth"},
        priority    = 14
    )
    public void testGetUserDetailByEmail() {
        String email = "automation_test_user@test.com";
        ExtentManager.logInfo("Getting user details for: " + email);

        Response response = requestSpec
            .given()
                // queryParam() adds ?email=... to the URL
                // Full URL becomes: /api/getUserDetailByEmail?email=automation_test_user@test.com
                .queryParam("email", email)
                .log().uri()
            .when()
                .get("/getUserDetailByEmail")
            .then()
                .statusCode(200)
                .extract().response();

        // The response contains a nested "user" object
        // JSON: { "responseCode": 200, "user": { "id": 1, "name": "...", "email": "..." } }
        String returnedEmail = response.jsonPath().getString("user.email");
        String returnedName  = response.jsonPath().getString("user.name");

        Assert.assertNotNull(returnedEmail, "User email should be in response");
        Assert.assertEquals(returnedEmail.toLowerCase(), email.toLowerCase(),
            "Returned email should match requested email");

        ExtentManager.logPass("User details retrieved — name: " + returnedName + ", email: " + returnedEmail);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DATA-DRIVEN API TEST
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Provides multiple search keywords for data-driven API search testing.
     */
    @DataProvider(name = "searchKeywords")
    public Object[][] provideSearchKeywords() {
        return new Object[][] {
            { "top",    true  },   // keyword, shouldFindResults
            { "tshirt", true  },
            { "jeans",  true  },
            { "xyznotexist123", false },   // This keyword should find nothing
        };
    }

    /**
     * Data-driven API test: search with different keywords.
     * Runs once per row in "searchKeywords" DataProvider.
     *
     * @param keyword           word to search for
     * @param shouldFindResults whether we expect results to come back
     */
    @Test(
        description  = "TC-API-005b: Data-driven search API test",
        groups       = {"api", "regression", "search", "data-driven"},
        priority     = 15,
        dataProvider = "searchKeywords"
    )
    public void testSearchApiWithMultipleKeywords(String keyword, boolean shouldFindResults) {
        ExtentManager.logInfo("API search test for keyword: '" + keyword + "'");

        Response response = requestSpec
            .given()
                .contentType(ContentType.URLENC)
                .formParam("search_product", keyword)
            .when()
                .post("/searchProduct")
            .then()
                .statusCode(200)
                .extract().response();

        List<Map<String, Object>> products = response.jsonPath().getList("products");
        int count = (products != null) ? products.size() : 0;

        if (shouldFindResults) {
            Assert.assertTrue(count > 0,
                "Expected results for keyword '" + keyword + "' but got 0");
            ExtentManager.logPass("'" + keyword + "' returned " + count + " products as expected");
        } else {
            Assert.assertEquals(count, 0,
                "Expected NO results for keyword '" + keyword + "' but got " + count);
            ExtentManager.logPass("'" + keyword + "' correctly returned no results");
        }
    }
}
