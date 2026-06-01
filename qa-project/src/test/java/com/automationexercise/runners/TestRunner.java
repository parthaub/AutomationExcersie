package com.automationexercise.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunner.java
 *
 * PURPOSE:
 *   The entry point that tells Cucumber AND TestNG:
 *     - WHERE to find the .feature files
 *     - WHERE to find the step definition Java classes (glue)
 *     - HOW to generate reports
 *     - WHICH scenarios to run (by tag)
 *
 * HOW IT WORKS:
 *   - Extends AbstractTestNGCucumberTests (from cucumber-testng dependency)
 *   - @CucumberOptions configures Cucumber behavior
 *   - TestNG sees this as a normal test class and runs it via testng.xml
 *
 * RUNNING SPECIFIC TAGS:
 *   To run only @smoke tests:    change tags = "@smoke"
 *   To run only @regression:     tags = "@regression"
 *   To run all except @skip:     tags = "not @skip"
 *   To run @smoke AND @login:    tags = "@smoke and @login"
 *   To run @smoke OR @regression: tags = "@smoke or @regression"
 */
@CucumberOptions(

    // WHERE to find .feature files
    // "src/test/resources/features" = scan this folder (and subfolders) for all .feature files
    features = "src/test/resources/features",

    // WHERE to find Step Definitions AND Hooks
    // Must include BOTH packages — separate with comma
    glue = {
        "com.automationexercise.stepDefinitions",   // Step def classes
        "com.automationexercise.hooks"              // Hooks.java
    },

    // WHICH tags to run (comment/change to switch which tests run)
    tags = "@regression",  // Run all @regression scenarios

    // HOW to format/display output
    plugin = {
        "pretty",                                                    // Colorful console output
        "html:test-output/cucumber-reports/index.html",             // HTML report
        "json:test-output/cucumber.json",                           // JSON (for CI/CD import)
        "rerun:test-output/failed_tests.txt"                        // Saves failed test paths for re-run
    },

    // monochrome = true removes ANSI color codes from console (cleaner in some terminals)
    monochrome = true,

    // Publish = true uploads results to Cucumber cloud (requires account — can set to false)
    publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * This @DataProvider overrides the parent class's scenarios() method.
     *
     * With parallel = true, Cucumber scenarios run in PARALLEL (simultaneously).
     * With parallel = false (default), scenarios run one at a time (sequential).
     *
     * For beginners: keep parallel = false until you understand ThreadLocal properly.
     * For CI/CD:     parallel = true makes tests run faster.
     */
    @Override
    @DataProvider(parallel = false)   // Change to parallel = true for faster parallel execution
    public Object[][] scenarios() {
        // Calls the parent class method which returns all scenarios as a 2D array
        // TestNG treats each scenario as a separate "test" in the DataProvider
        return super.scenarios();
    }
}
