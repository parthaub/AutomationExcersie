package com.automationexercise.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * SmokeRunner.java
 *
 * PURPOSE:
 *   Runs ONLY scenarios tagged with @smoke.
 *   Used for quick sanity checks after a deployment.
 *   Should complete in under 5 minutes.
 *
 * HOW TO RUN:
 *   Option 1: Change testng.xml to point to SmokeRunner instead of TestRunner
 *   Option 2: mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke.xml
 *
 * DIFFERENCE FROM TestRunner.java:
 *   TestRunner: tags = "@regression"  (all regression tests)
 *   SmokeRunner: tags = "@smoke"      (critical smoke tests only)
 */
@CucumberOptions(
    features    = "src/test/resources/features",
    glue        = {
        "com.automationexercise.stepDefinitions",
        "com.automationexercise.hooks"
    },
    tags        = "@smoke",   // Only run @smoke scenarios
    plugin      = {
        "pretty",
        "html:test-output/smoke-reports/index.html",
        "json:test-output/smoke.json"
    },
    monochrome  = true
)
public class SmokeRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
