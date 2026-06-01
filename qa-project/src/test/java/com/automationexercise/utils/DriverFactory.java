package com.automationexercise.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * DriverFactory.java
 *
 * PURPOSE:
 *   Creates, provides, and closes the WebDriver (browser) instance.
 *   WebDriver is what Selenium uses to control a real browser.
 *
 * KEY CONCEPT — ThreadLocal:
 *   If you run tests in PARALLEL (multiple tests at the same time),
 *   each test needs its OWN separate browser instance.
 *   ThreadLocal<WebDriver> gives each "thread" (parallel test) its own copy.
 *   Think of it like: every test gets its own private browser locker.
 *
 * USAGE:
 *   DriverFactory.initDriver();        // opens the browser
 *   DriverFactory.getDriver();         // gets the browser for THIS test
 *   DriverFactory.quitDriver();        // closes the browser
 */
public class DriverFactory {

    // ThreadLocal stores a separate WebDriver for each running thread
    // This makes parallel test execution safe (no shared browser between tests)
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Opens a browser and stores it in ThreadLocal.
     * Call this in @BeforeMethod (TestNG) or @Before (Cucumber Hook).
     */
    public static void initDriver() {
        // Read which browser to use from config.properties
        String browser = ConfigReader.getProperty("browser");
        WebDriver webDriver;

        // Open the correct browser based on config value
        if (browser.equalsIgnoreCase("chrome")) {
            // WebDriverManager automatically downloads the correct ChromeDriver
            // No manual chromedriver.exe setup needed!
            WebDriverManager.chromedriver().setup();

            // ChromeOptions lets us configure Chrome's behavior
            ChromeOptions options = new ChromeOptions();
            // options.addArguments("--headless");  // Uncomment to run without GUI (CI/CD)
            options.addArguments("--start-maximized");    // Open browser maximized
            options.addArguments("--disable-notifications"); // Block pop-up notifications

            webDriver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver();

        } else {
            // If browser value is something unexpected, throw a clear error
            throw new IllegalArgumentException(
                "Browser '" + browser + "' not supported. Use 'chrome' or 'firefox' in config.properties"
            );
        }

        // Set implicit wait: Selenium will wait up to X seconds for elements to appear
        // This prevents "ElementNotFoundException" on slow-loading pages
        webDriver.manage().timeouts().implicitlyWait(
            java.time.Duration.ofSeconds(
                Long.parseLong(ConfigReader.getProperty("implicit.wait"))
            )
        );

        // Store the created driver in ThreadLocal for this thread
        driver.set(webDriver);

        System.out.println("[DriverFactory] " + browser + " browser launched.");
    }

    /**
     * Returns the WebDriver for the CURRENT thread/test.
     * Call this whenever you need to interact with the browser.
     *
     * @return the WebDriver instance for this test
     */
    public static WebDriver getDriver() {
        // driver.get() returns the WebDriver stored for THIS thread
        return driver.get();
    }

    /**
     * Closes the browser and removes the driver from ThreadLocal.
     * Call this in @AfterMethod (TestNG) or @After (Cucumber Hook).
     *
     * WHY remove()? Without it, memory leaks happen in long test runs.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();  // Closes ALL windows and ends the WebDriver session
            driver.remove();      // Removes from ThreadLocal to free memory
            System.out.println("[DriverFactory] Browser closed.");
        }
    }
}
