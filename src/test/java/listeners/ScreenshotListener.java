package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtil;

import java.lang.reflect.Field;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testClass = result.getInstance();
        try {
            Field driverField = testClass.getClass().getDeclaredField("driver");
            driverField.setAccessible(true); // Allow access to private fields
            WebDriver driver = (WebDriver) driverField.get(testClass);
            if (driver != null) {
                // Capture screenshot if a driver is not null
                ScreenshotUtil.captureScreenshot(driver, result.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}