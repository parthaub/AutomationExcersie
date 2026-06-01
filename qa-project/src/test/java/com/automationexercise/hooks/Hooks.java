package com.automationexercise.hooks;

import com.automationexercise.utils.DriverFactory;
import com.automationexercise.utils.ExtentManager;
import com.automationexercise.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.testng.CucumberOptions;

/**
 * Hooks.java
 *
 * PURPOSE:
 *   Cucumber Hooks are methods that run automatically BEFORE and AFTER
 *   every scenario, without being mentioned in the feature file.
 *   Think of them as @BeforeMethod / @AfterMethod but for Cucumber scenarios.
 *
 * CUCUMBER HOOK ANNOTATIONS:
 *   @Before    → runs before EACH scenario
 *   @After     → runs after EACH scenario
 *   @BeforeAll → runs ONCE before all scenarios (requires static method)
 *   @AfterAll  → runs ONCE after all scenarios (requires static method)
 *
 * ORDER OF EXECUTION (for one scenario):
 *   @BeforeAll (once)
 *   @Before (for scenario 1)
 *   Scenario 1 steps run
 *   @After  (for scenario 1)
 *   @Before (for scenario 2)
 *   Scenario 2 steps run
 *   @After  (for scenario 2)
 *   @AfterAll (once)
 *
 * TAGGED HOOKS:
 *   @Before("@smoke")  → only runs before scenarios tagged with @smoke
 *   @After("@api")     → only runs after scenarios tagged with @api
 */
public class Hooks {

    /**
     * Runs ONCE before all scenarios in the entire test run.
     * Good for: creating output directories, initializing singletons.
     */
    @BeforeAll
    public static void globalSetup() {
        System.out.println("\n======================================");
        System.out.println("  CUCUMBER TEST RUN STARTING");
        System.out.println("======================================\n");

        // Initialize Extent report singleton (creates the HTML file)
        ExtentManager.getInstance();
    }

    /**
     * Runs before EACH scenario.
     * The Scenario parameter gives us the scenario's name, tags, and status.
     *
     * @param scenario  info about the current scenario (name, tags, etc.)
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println("\n--- Starting Scenario: " + scenario.getName() + " ---");
        System.out.println("    Tags: " + scenario.getSourceTagNames());

        // Create a new test node in Extent report for this scenario
        // scenario.getName() = the scenario title from the feature file
        // scenario.getSourceTagNames() = the @tags as a Set
        ExtentManager.startTest(
            scenario.getName(),
            "Tags: " + scenario.getSourceTagNames()
        );

        // Open the browser (ChromeDriver)
        DriverFactory.initDriver();

        ExtentManager.logInfo("Browser launched for scenario: " + scenario.getName());
    }

    /**
     * Runs after EACH scenario (whether it passed, failed, or was skipped).
     * This is where we:
     *   1. Take a screenshot if the scenario FAILED
     *   2. Log pass/fail to the Extent report
     *   3. Close the browser
     *
     * @param scenario  info about the scenario that just ran
     */
    @After
    public void afterScenario(Scenario scenario) {
        // Check if this scenario failed
        if (scenario.isFailed()) {
            System.err.println("--- FAILED: " + scenario.getName() + " ---");

            // Take screenshot and attach to Extent report
            try {
                if (DriverFactory.getDriver() != null) {
                    ScreenshotUtils.captureAndAttachToReport(
                        DriverFactory.getDriver(),
                        scenario.getName()
                    );
                }
            } catch (Exception e) {
                ExtentManager.logWarning("Screenshot capture failed: " + e.getMessage());
            }

            // Log failure in report
            ExtentManager.logFail("Scenario FAILED: " + scenario.getName());

        } else {
            System.out.println("--- PASSED: " + scenario.getName() + " ---");
            // Log success in report
            ExtentManager.logPass("Scenario PASSED: " + scenario.getName());
        }

        // ALWAYS close the browser after the scenario, pass or fail
        DriverFactory.quitDriver();
    }

    /**
     * Runs ONCE after ALL scenarios have finished.
     * CRITICAL: Must call flush() here to write the report to disk!
     */
    @AfterAll
    public static void globalTearDown() {
        System.out.println("\n======================================");
        System.out.println("  ALL SCENARIOS COMPLETE");
        System.out.println("======================================\n");

        // Save the Extent HTML report to disk
        // Without flush(), the report file will be empty!
        ExtentManager.flushReport();
    }
}
