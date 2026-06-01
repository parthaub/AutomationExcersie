package com.automationexercise.listeners;

import com.automationexercise.utils.ExtentManager;
import com.automationexercise.utils.ScreenshotUtils;
import com.automationexercise.utils.DriverFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener.java
 *
 * PURPOSE:
 *   A TestNG Listener that AUTOMATICALLY runs code at key moments
 *   of the test lifecycle — without modifying each test class.
 *
 * WHAT IS A LISTENER?
 *   A listener is a class that "listens" for TestNG events and reacts.
 *   Events include: test started, test passed, test failed, suite finished, etc.
 *
 * WHY USE A LISTENER?
 *   Without a listener, you'd add screenshot + report code to every @AfterMethod.
 *   With a listener, you write it ONCE here and it applies to ALL tests automatically.
 *
 * HOW TO REGISTER A LISTENER:
 *   Option 1: In testng.xml:
 *     <listeners>
 *       <listener class-name="com.automationexercise.listeners.TestListener"/>
 *     </listeners>
 *
 *   Option 2: On the test class with @Listeners annotation:
 *     @Listeners(TestListener.class)
 *     public class LoginTest { ... }
 *
 * ITestListener METHODS (we implement these):
 *   onStart()         → before any test in the suite starts
 *   onFinish()        → after ALL tests in the suite finish
 *   onTestStart()     → before each @Test method starts
 *   onTestSuccess()   → after a @Test method passes
 *   onTestFailure()   → after a @Test method fails
 *   onTestSkipped()   → after a @Test method is skipped
 */
public class TestListener implements ITestListener {

    /**
     * Called ONCE before ANY test in the suite runs.
     * Good place for suite-level setup logging.
     */
    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n====================================");
        System.out.println("[TestListener] Test Suite Starting: " + context.getName());
        System.out.println("====================================\n");
    }

    /**
     * Called ONCE after ALL tests in the suite finish.
     * This is where we flush (save) the Extent report to disk.
     * Without calling flush(), the HTML report will be incomplete!
     */
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n====================================");
        System.out.println("[TestListener] Suite Finished: " + context.getName());
        System.out.println("  PASSED  : " + context.getPassedTests().size());
        System.out.println("  FAILED  : " + context.getFailedTests().size());
        System.out.println("  SKIPPED : " + context.getSkippedTests().size());
        System.out.println("====================================\n");

        // IMPORTANT: flush saves the report HTML to disk
        ExtentManager.flushReport();
    }

    /**
     * Called BEFORE each @Test method starts.
     * We create a new node in the Extent report for this test.
     *
     * @param result  contains test name, class name, etc.
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName  = result.getName();
        String className = result.getTestClass().getName();

        System.out.println("[TestListener] Test STARTED: " + testName);

        // Create a new row in the Extent report for this test
        // We use testName as the title and className as the description
        ExtentManager.startTest(testName, "Class: " + className);
        ExtentManager.logInfo("Test started: " + testName);
    }

    /**
     * Called when a @Test method PASSES.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TestListener] Test PASSED: " + result.getName());

        // Log a green PASS in the Extent report
        ExtentManager.logPass("Test PASSED: " + result.getName());
    }

    /**
     * Called when a @Test method FAILS.
     * We take a screenshot and log the failure details.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("[TestListener] Test FAILED: " + result.getName());

        // Log the exception message to the report
        ExtentManager.logFail("Test FAILED: " + result.getThrowable().getMessage());

        // Take a screenshot so we can see what the browser looked like
        try {
            if (DriverFactory.getDriver() != null) {
                ScreenshotUtils.captureAndAttachToReport(
                    DriverFactory.getDriver(),
                    result.getName()
                );
            }
        } catch (Exception e) {
            ExtentManager.logWarning("Could not take screenshot: " + e.getMessage());
        }
    }

    /**
     * Called when a @Test method is SKIPPED.
     * Tests are skipped when a method they depend on has failed,
     * or when @Test(enabled = false) is used.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TestListener] Test SKIPPED: " + result.getName());

        // Log as warning in Extent report (yellow)
        ExtentManager.logWarning("Test SKIPPED: " + result.getName());
    }
}
