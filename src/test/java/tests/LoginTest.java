package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SwitchUserPage;
import utils.ConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

@Listeners(listeners.ScreenshotListener.class)
public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private SwitchUserPage switchUserPage;

    @BeforeMethod
    public void setUp() throws IOException {

        // Set up ChromeOptions with a unique user data directory
        ChromeOptions options = new ChromeOptions();

        // Create a unique temporary directory for user data to avoid DevOps Chrome conflict
        Path userDataDir = Files.createTempDirectory("chrome-user-data");
        options.addArguments("--user-data-dir=" + userDataDir.toString());

        // Check system property (default: false for local, true in pipeline)
        String headless = System.getProperty("headless", "false");

        if (headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        }

        // Initialize WebDriver with options (e.g., ChromeDriver, FirefoxDriver)
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Initialize the LoginPage with the WebDriver instance
        loginPage = new LoginPage(driver);

        // Get URL from config.properties
        String appUrl = ConfigReader.get("app.url");

        // Navigate to the login page
        driver.get(appUrl);

        // Initialize the SwitchUserPage with the WebDriver instance
        switchUserPage = new SwitchUserPage(driver);
    }

    @Test(priority = 1)
    public void testValidLoginCustomerAdmin() {
        String email = ConfigReader.get("customer.admin.email");
        String password = ConfigReader.get("customer.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        // Wait for the page to load and check the URL
        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
            Objects.requireNonNull(webDriver.getCurrentUrl()).contains("/Customer/CustomerDashboard"));

        // Assert that the URL contains the expected path for Customer Admin
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/Customer/CustomerDashboard"),
                "Login failed for Customer Admin. Current URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 2)
    public void testValidLoginPartnerAdmin() {
        String email = ConfigReader.get("partner.admin.email");
        String password = ConfigReader.get("partner.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        // Wait for the page to load and check the URL
        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
            Objects.requireNonNull(webDriver.getCurrentUrl()).contains("/Provider/ProviderDashboard"));

        // Assert that the URL contains the expected path for Partner Admin
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/Provider/ProviderDashboard"));
    }

    @Test(priority = 3)
    public void testInvalidPassword() {
        String email = ConfigReader.get("customer.admin.email");
        String password = ConfigReader.get("invalid.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        Assert.assertTrue(loginPage.isErrorMessageDisplayed());
    }

    @Test(priority = 4)
    public void testNativeUserDirectLogin() {
        String email = ConfigReader.get("native.user.email");
        String password = ConfigReader.get("native.user.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        // Wait for the expected URL
        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
                Objects.requireNonNull(webDriver.getCurrentUrl()).endsWith("/Provider/ProviderDashboard"));

        // Wait for the page to load and check the URL
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).endsWith("/Provider/ProviderDashboard"),
                "Login failed for Native User. Current URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 5)
    public void testAdminSwitchUserLogin() {
        // Log in as a Partner Admin
        String email = ConfigReader.get("partner.admin.email");
        String password = ConfigReader.get("partner.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        // Click on the profile image to open the switch user modal
        switchUserPage.clickProfileImage();
        switchUserPage.clickSwitchUserLink();

        // Verify the modal title
        Assert.assertEquals(switchUserPage.getModalTitle(), "ADMIN AS OTHER USER");

        // Select a user type and search for a partner user and store selected email
        String expectedEmail = switchUserPage.searchAndSelectPartnerUser("Partner Cluster Head");

        // Click the switch user button
        switchUserPage.clickSwitchUserButton();

        // Wait for dashboard and close toast if present
        switchUserPage.waitForDashboardAndCloseToast();

        // Click on the profile image again to verify the switch
        switchUserPage.clickProfileImage();

        // Get actual logged-in email
        String actualEmail = switchUserPage.getLoggedInEmail();

        // Verify that the logged-in email matches the selected partner user
        Assert.assertEquals(actualEmail, expectedEmail.toLowerCase(), "Switched user email mismatch.");
    }

    @Test(priority = 6)
    public void testSwitchUserFunctionalityWithoutSelectingPartnerUser1() {
        // Log in as a Partner Admin
        String email = ConfigReader.get("partner.admin.email");
        String password = ConfigReader.get("partner.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        // Click on the profile image to open the switch user modal
        switchUserPage.clickProfileImage();
        switchUserPage.clickSwitchUserLink();

        // Verify the modal title
        Assert.assertEquals(switchUserPage.getModalTitle(), "ADMIN AS OTHER USER");

        // Click the switch user button
        switchUserPage.clickSwitchUserButton();

        // Validate error message
        String actualMessage = switchUserPage.getToastErrorMessageText();
        Assert.assertEquals(actualMessage, "Opps... Please select partner user.", "Error message mismatch.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
