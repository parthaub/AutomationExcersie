package com.automationexercise.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer.java
 *
 * PURPOSE:
 *   Automatically re-runs a FAILED test up to MAX_RETRY_COUNT times.
 *   This handles "flaky tests" — tests that sometimes fail due to
 *   timing issues, network hiccups, or slow pages, not real bugs.
 *
 * HOW IT WORKS:
 *   TestNG calls retry() every time a test fails.
 *   If retry() returns TRUE  → TestNG runs the test again.
 *   If retry() returns FALSE → TestNG marks it as final FAIL.
 *
 * HOW TO USE IT ON A TEST:
 *   Option 1: Add to individual @Test annotation:
 *     @Test(retryAnalyzer = RetryAnalyzer.class)
 *
 *   Option 2: Apply to ALL tests via the TestNG Listener (see TestListener.java)
 *
 * IMPORTANT: Set MAX_RETRY_COUNT to 2 or 3 — not too high.
 *            Real bugs should still fail, not keep retrying forever.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    // How many times to retry a failed test (not counting the first attempt)
    // So with MAX_RETRY_COUNT = 2:
    //   First run = attempt 1
    //   Retry 1   = attempt 2
    //   Retry 2   = attempt 3
    //   Then it's marked as FAIL
    private static final int MAX_RETRY_COUNT = 2;

    // Tracks how many retries have been done for the CURRENT test
    // retryCount starts at 0; increments each time retry() returns true
    private int retryCount = 0;

    /**
     * Called by TestNG every time a test FAILS.
     *
     * @param result  information about the failed test (name, exception, etc.)
     * @return true   = retry the test again
     *         false  = give up, mark it as FAILED
     */
    @Override
    public boolean retry(ITestResult result) {
        // If we haven't reached the max retries yet, try again
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;  // Increment counter

            System.out.println("[RetryAnalyzer] RETRYING test: '"
                + result.getName() + "' — attempt " + retryCount + " of " + MAX_RETRY_COUNT);

            return true;   // Tell TestNG to re-run this test
        }

        // We've used all our retries — final failure
        System.out.println("[RetryAnalyzer] Test '" + result.getName()
            + "' FAILED after " + MAX_RETRY_COUNT + " retries.");

        return false;  // Tell TestNG this test is done, mark as FAIL
    }
}
