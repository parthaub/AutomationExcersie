package com.automationexercise.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

/**
 * ExtentManager.java
 *
 * PURPOSE:
 *   Manages the Extent HTML report that is generated after every test run.
 *   This creates a beautiful report showing: Pass/Fail, screenshots,
 *   test steps, system info, and charts.
 *
 * KEY CLASSES:
 *   ExtentReports      = the main report object (one per test run)
 *   ExtentSparkReporter = generates the HTML file
 *   ExtentTest         = represents ONE test case in the report
 *
 * SINGLETON PATTERN:
 *   We use a "singleton" so only ONE ExtentReports object ever exists.
 *   If we created a new one per test, each test would write a separate report.
 *
 * THREADLOCAL for ExtentTest:
 *   Each running test (thread) needs its own ExtentTest node in the report.
 *   ThreadLocal<ExtentTest> ensures parallel tests write to their own node.
 */
public class ExtentManager {

    // The one and only ExtentReports instance for the whole test run
    private static ExtentReports extentReports;

    // Each thread (each running test) gets its own ExtentTest node
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Returns the singleton ExtentReports instance.
     * Creates it on first call; returns the existing one on later calls.
     * This is called the "lazy singleton" pattern.
     */
    public static synchronized ExtentReports getInstance() {
        // Only create a new ExtentReports if one doesn't exist yet
        if (extentReports == null) {

            // Read report path from config (e.g., "test-output/ExtentReport.html")
            String reportPath = ConfigReader.getProperty("extent.report.path");

            // Make sure the output folder exists (create it if it doesn't)
            File reportDir = new File(reportPath).getParentFile();
            if (reportDir != null && !reportDir.exists()) {
                reportDir.mkdirs(); // mkdirs() creates the folder AND any parent folders
            }

            // ExtentSparkReporter creates the actual HTML file
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            // Configure how the report looks
            sparkReporter.config().setDocumentTitle("AutomationExercise Test Report");
            sparkReporter.config().setReportName("Regression Test Results");
            sparkReporter.config().setTheme(Theme.DARK);         // Dark theme looks professional
            sparkReporter.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

            // Create the main ExtentReports object
            extentReports = new ExtentReports();

            // Attach the HTML reporter to it
            extentReports.attachReporter(sparkReporter);

            // Add system info that appears at the top of the report
            extentReports.setSystemInfo("Application", "AutomationExercise.com");
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", ConfigReader.getProperty("browser"));
            extentReports.setSystemInfo("Tester", "QA Automation Engineer");

            System.out.println("[ExtentManager] Extent report initialized at: " + reportPath);
        }

        return extentReports;
    }

    /**
     * Creates a new test node in the report for the current test.
     * Call this at the START of each test (in @BeforeMethod or @Before).
     *
     * @param testName        name that appears in the report (e.g., "Login Test")
     * @param testDescription short description (e.g., "Verify valid login works")
     */
    public static void startTest(String testName, String testDescription) {
        // createTest() adds a new row/node to the report
        ExtentTest test = getInstance().createTest(testName, testDescription);

        // Store this test node for THIS thread only
        extentTest.set(test);
    }

    /**
     * Returns the ExtentTest for the CURRENT running test.
     * Use this to log steps, pass/fail, screenshots.
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Logs a PASS (green) message for the current test step.
     * @param message description of what passed
     */
    public static void logPass(String message) {
        getTest().pass(message);
    }

    /**
     * Logs a FAIL (red) message for the current test step.
     * @param message description of what failed
     */
    public static void logFail(String message) {
        getTest().fail(message);
    }

    /**
     * Logs an INFO (blue) message — useful for logging test steps.
     * @param message informational message
     */
    public static void logInfo(String message) {
        getTest().info(message);
    }

    /**
     * Logs a WARNING (yellow) message.
     * @param message warning message
     */
    public static void logWarning(String message) {
        getTest().warning(message);
    }

    /**
     * Writes the report to disk. MUST be called at the end of the test run.
     * Without flush(), the HTML file will be empty or incomplete!
     * Call in @AfterSuite or a TestNG listener's onFinish() method.
     */
    public static void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
            System.out.println("[ExtentManager] Report flushed/saved to disk.");
        }
    }
}
