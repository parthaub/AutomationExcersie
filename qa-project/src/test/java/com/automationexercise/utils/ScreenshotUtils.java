package com.automationexercise.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils.java
 *
 * PURPOSE:
 *   Takes screenshots and attaches them to the Extent report.
 *   Screenshots are essential for debugging failures —
 *   you can see exactly what the browser showed when the test failed.
 *
 * HOW SCREENSHOTS WORK IN SELENIUM:
 *   WebDriver has a TakesScreenshot interface.
 *   We cast WebDriver to TakesScreenshot, then call getScreenshotAs().
 *   This returns the screenshot as a File, byte[], or Base64 String.
 *
 * BEGINNER TIP:
 *   "Casting" means treating an object as a different type.
 *   (TakesScreenshot) driver  →  tells Java: "treat this driver as TakesScreenshot"
 */
public class ScreenshotUtils {

    /**
     * Takes a screenshot, saves it as a PNG file, and returns the file path.
     *
     * @param driver    the WebDriver (browser) to screenshot
     * @param testName  name used for the filename (spaces replaced with underscores)
     * @return          the absolute file path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        // Cast driver to TakesScreenshot so we can use getScreenshotAs()
        TakesScreenshot ts = (TakesScreenshot) driver;

        // Take the screenshot — OutputType.FILE gives us a temporary File object
        File screenshotFile = ts.getScreenshotAs(OutputType.FILE);

        // Create a timestamp so each screenshot has a unique filename
        // Format: yyyyMMdd_HHmmss = e.g., 20240510_143025
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Build the destination path: screenshots/TestName_20240510_143025.png
        String screenshotDir  = ConfigReader.getProperty("screenshot.path");
        String screenshotPath = screenshotDir + testName.replaceAll("\\s+", "_") + "_" + timestamp + ".png";

        // Make sure the screenshots folder exists
        File destDir = new File(screenshotDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // Copy the temporary screenshot file to our permanent location
        try {
            FileUtils.copyFile(screenshotFile, new File(screenshotPath));
        } catch (IOException e) {
            System.err.println("[ScreenshotUtils] Failed to save screenshot: " + e.getMessage());
        }

        System.out.println("[ScreenshotUtils] Screenshot saved: " + screenshotPath);
        return screenshotPath;
    }

    /**
     * Takes a screenshot AND immediately attaches it to the current Extent test node.
     * Call this in @AfterMethod when a test fails.
     *
     * @param driver   the WebDriver
     * @param testName name for the screenshot file
     */
    public static void captureAndAttachToReport(WebDriver driver, String testName) {
        try {
            String path = captureScreenshot(driver, testName);

            // MediaEntityBuilder builds a screenshot media entity for Extent
            // This shows the image inline in the HTML report
            ExtentManager.getTest().fail(
                "Test FAILED — see screenshot below:",
                MediaEntityBuilder.createScreenCaptureFromPath(path).build()
            );
        } catch (Exception e) {
            ExtentManager.logWarning("Could not attach screenshot: " + e.getMessage());
        }
    }
}
