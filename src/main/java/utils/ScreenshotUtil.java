package utils;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScreenshotUtil {
    private static final Logger logger = Logger.getLogger(ScreenshotUtil.class.getName());

    public static void captureScreenshot(WebDriver driver, String testName){
        try {
            // Correct casting and screenshot capture
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Get today's date in yyyy-MM-dd format
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Define the destination path for the screenshot
            String timestamp = String.valueOf(System.currentTimeMillis());
            Path destination = Path.of("Screenshots",dateFolder, testName + "_" + timestamp + ".png");

            // Ensure the directory exists
            Files.createDirectories(destination.getParent());

            // Copy a screenshot file
            Files.copy(src.toPath(), destination);

            logger.info("Screenshot saved to: " + destination.toAbsolutePath());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to capture screenshot for test: " + testName, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred while capturing screenshot for test: " + testName, e);
        }
    }
}