# AutomationExercise QA Automation Project

A complete, beginner-friendly QA automation project for [automationexercise.com](https://automationexercise.com) using **Java, Maven, Selenium, TestNG, Cucumber, REST Assured, and Extent Reports**.

---

## Project Structure

```
qa-automation-project/
├── pom.xml                                          ← Maven config + all dependencies
├── README.md                                        ← This file
└── src/
    └── test/
        ├── java/com/automationexercise/
        │   ├── pages/                               ← Page Object classes
        │   │   ├── BasePage.java                    ← Parent: shared browser methods
        │   │   ├── HomePage.java
        │   │   ├── LoginPage.java
        │   │   ├── RegisterPage.java
        │   │   ├── ProductsPage.java
        │   │   ├── CartPage.java
        │   │   └── CheckoutPage.java
        │   │
        │   ├── stepDefinitions/                     ← Cucumber step definitions
        │   │   ├── LoginSteps.java
        │   │   ├── ProductSteps.java
        │   │   └── CheckoutSteps.java
        │   │
        │   ├── hooks/
        │   │   └── Hooks.java                       ← @Before/@After for Cucumber
        │   │
        │   ├── runners/
        │   │   ├── TestRunner.java                  ← Run @regression scenarios
        │   │   └── SmokeRunner.java                 ← Run @smoke scenarios only
        │   │
        │   ├── testng_examples/                     ← Pure TestNG test classes
        │   │   ├── LoginTest.java                   ← ALL TestNG annotations demo
        │   │   └── EndToEndTest.java                ← Full E2E flow test
        │   │
        │   ├── api/
        │   │   └── ProductsApiTest.java             ← REST Assured API tests
        │   │
        │   ├── listeners/
        │   │   ├── TestListener.java                ← ITestListener implementation
        │   │   └── RetryAnalyzer.java               ← IRetryAnalyzer implementation
        │   │
        │   ├── models/
        │   │   └── TestDataModel.java               ← POJO for test data + Builder
        │   │
        │   └── utils/
        │       ├── ConfigReader.java                ← Reads config.properties
        │       ├── DriverFactory.java               ← Creates/manages WebDriver
        │       ├── WaitHelper.java                  ← Explicit wait methods
        │       ├── ExtentManager.java               ← Extent report singleton
        │       ├── ScreenshotUtils.java             ← Screenshot + attach to report
        │       └── ExcelUtils.java                  ← Apache POI Excel reader
        │
        └── resources/
            ├── features/                            ← Cucumber feature files (Gherkin)
            │   ├── login.feature
            │   ├── products.feature
            │   └── checkout.feature
            ├── config/
            │   └── config.properties                ← URL, browser, timeouts, paths
            └── suites/
                ├── testng.xml                       ← Full suite (all tests)
                ├── smoke.xml                        ← Smoke tests only
                └── api_tests.xml                    ← API tests only
```

---

## Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 11 or 17 | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org |
| Google Chrome | Latest | https://www.google.com/chrome |
| IntelliJ IDEA | Community | https://www.jetbrains.com/idea |

**Verify installations:**
```bash
java -version      # Should show 11.x or 17.x
mvn -version       # Should show 3.8.x or higher
git --version      # Should show 2.x
```

---

## How to Run

### Run Full Suite (all tests):
```bash
mvn clean test
```

### Run Smoke Tests Only:
```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke.xml
```

### Run API Tests Only (no browser):
```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/api_tests.xml
```

### Run Specific Cucumber Tag:
```bash
mvn test -Dcucumber.filter.tags=@smoke
mvn test -Dcucumber.filter.tags=@regression
mvn test -Dcucumber.filter.tags="@smoke and @auth"
```

### Run in IntelliJ:
1. Right-click `testng.xml` → Run
2. Right-click any `@Test` method → Run
3. Right-click a `.feature` file → Run

---

## TestNG Annotations — Quick Reference

| Annotation | When It Runs | Use For |
|------------|-------------|---------|
| `@BeforeSuite` | Once before all tests | Init report, global config |
| `@AfterSuite` | Once after all tests | Flush report, global cleanup |
| `@BeforeTest` | Before each `<test>` block in XML | Environment setup |
| `@AfterTest` | After each `<test>` block | Test-level cleanup |
| `@BeforeClass` | Once before first @Test in class | Class-level setup |
| `@AfterClass` | Once after last @Test in class | Class-level cleanup |
| `@BeforeMethod` | Before each @Test method | Open browser, navigate |
| `@AfterMethod` | After each @Test method | Close browser |
| `@Test` | The test itself | The test logic + assertions |
| `@DataProvider` | Supplies data to @Test | Data-driven testing |

### @Test Attributes:
```java
@Test(
    description      = "Human-readable test name",
    groups           = {"smoke", "regression"},    // Tag for group filtering
    priority         = 1,                          // Lower = runs first
    enabled          = true,                       // false = skip this test
    retryAnalyzer    = RetryAnalyzer.class,        // Auto-retry on failure
    dependsOnMethods = {"otherTestMethod"},         // Skip if dependency failed
    timeOut          = 30000,                      // Fail if takes >30 seconds
    dataProvider     = "myDataProvider",           // Use @DataProvider data
    alwaysRun        = true                        // Run even if dependency failed
)
```

---

## Cucumber Tags — Quick Reference

```gherkin
@smoke         # Critical tests, run on every build
@regression    # All tests, run nightly
@e2e           # Full end-to-end flow tests
@negative      # Tests for error conditions
@api           # API tests (no browser)
@data-driven   # Tests using Scenario Outline / DataProvider
```

Run by tag: `mvn test -Dcucumber.filter.tags=@smoke`

---

## Reports

After running tests, find reports in `test-output/`:

| Report | Location | Description |
|--------|----------|-------------|
| **Extent HTML** | `test-output/ExtentReport.html` | Main report with charts, screenshots |
| **Cucumber HTML** | `test-output/cucumber-reports/index.html` | BDD scenario report |
| **Cucumber JSON** | `test-output/cucumber.json` | For CI/CD / Xray import |
| **Surefire XML** | `target/surefire-reports/*.xml` | TestNG XML results |
| **Screenshots** | `test-output/screenshots/` | Auto-captured on failure |

Open `test-output/ExtentReport.html` in any browser to see the full test run report.

---

## Key Concepts

### Page Object Model (POM)
Each web page = one Java class. Tests call page methods, not raw Selenium commands.
```java
// Without POM (bad):
driver.findElement(By.cssSelector("a[href='/login']")).click();
driver.findElement(By.cssSelector("input[data-qa='login-email']")).sendKeys("a@b.com");

// With POM (good):
homePage.clickSignupLogin();
loginPage.enterLoginEmail("a@b.com");
```

### ThreadLocal for Parallel Tests
Each test thread gets its own `WebDriver` instance:
```java
private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
driver.set(new ChromeDriver());   // Store for THIS thread
driver.get();                     // Get for THIS thread only
driver.remove();                  // Clean up
```

### Explicit Waits (not Thread.sleep!)
```java
// BAD — always waits 5 full seconds
Thread.sleep(5000);

// GOOD — waits UP TO 20 seconds, returns as soon as element appears
wait.waitForElementVisible(By.id("result"));
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `SessionNotCreatedException` | Chrome and ChromeDriver version mismatch. WebDriverManager auto-fixes this. |
| `ElementNotInteractableException` | Add explicit wait: `wait.waitForElementClickable(locator)` |
| `NoSuchElementException` | Check locator in browser DevTools (F12 → Inspector) |
| `StaleElementReferenceException` | Re-find the element after page reload |
| Tests run but report is empty | Check that `ExtentManager.flushReport()` is called in `@AfterSuite` |
| Maven can't find tests | Check `testng.xml` path in `pom.xml` surefire configuration |
