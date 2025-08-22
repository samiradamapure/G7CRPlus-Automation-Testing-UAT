package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
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
    private Path tempProfile;

    @BeforeMethod
    public void setUp() throws IOException {
        // Create a truly unique Chrome profile directory for this test run
        tempProfile = Files.createTempDirectory("chrome-profile-");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-data-dir=" + tempProfile.toString());

        // Enable headless mode only if requested
        String headless = System.getProperty("headless", "false");
        if (headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        loginPage = new LoginPage(driver);
        switchUserPage = new SwitchUserPage(driver);

        driver.get(ConfigReader.get("app.url"));
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

        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
                Objects.requireNonNull(webDriver.getCurrentUrl()).contains("/Customer/CustomerDashboard"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/Customer/CustomerDashboard"),
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

        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
                Objects.requireNonNull(webDriver.getCurrentUrl()).contains("/Provider/ProviderDashboard"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/Provider/ProviderDashboard"),
                "Login failed for Partner Admin. Current URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 3)
    public void testInvalidPassword() {
        String email = ConfigReader.get("customer.admin.email");
        String password = ConfigReader.get("invalid.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Expected error message was not displayed for invalid password.");
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

        new WebDriverWait(driver, Duration.ofSeconds(40)).until(webDriver ->
                Objects.requireNonNull(webDriver.getCurrentUrl()).endsWith("/Provider/ProviderDashboard"));

        Assert.assertTrue(driver.getCurrentUrl().endsWith("/Provider/ProviderDashboard"),
                "Login failed for Native User. Current URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 5)
    public void testAdminSwitchUserLogin() {
        String email = ConfigReader.get("partner.admin.email");
        String password = ConfigReader.get("partner.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        switchUserPage.clickProfileImage();
        switchUserPage.clickSwitchUserLink();
        Assert.assertEquals(switchUserPage.getModalTitle(), "ADMIN AS OTHER USER");

        String expectedEmail = switchUserPage.searchAndSelectPartnerUser("Partner Cluster Head");
        switchUserPage.clickSwitchUserButton();
        switchUserPage.waitForDashboardAndCloseToast();

        switchUserPage.clickProfileImage();
        String actualEmail = switchUserPage.getLoggedInEmail();

        Assert.assertEquals(actualEmail, expectedEmail.toLowerCase(), "Switched user email mismatch.");
    }

    @Test(priority = 6)
    public void testSwitchUserFunctionalityWithoutSelectingPartnerUser1() {
        String email = ConfigReader.get("partner.admin.email");
        String password = ConfigReader.get("partner.admin.password");
        loginPage.enterEmail(email);
        loginPage.clickMicrosoftLogin();
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        loginPage.clickStaySignedInYes();

        switchUserPage.clickProfileImage();
        switchUserPage.clickSwitchUserLink();
        Assert.assertEquals(switchUserPage.getModalTitle(), "ADMIN AS OTHER USER");

        switchUserPage.clickSwitchUserButton();
        String actualMessage = switchUserPage.getToastErrorMessageText();
        Assert.assertEquals(actualMessage, "Opps... Please select partner user.", "Error message mismatch.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws IOException {
        if (driver != null) driver.quit();
        if (tempProfile != null) {
            try {
                Files.walk(tempProfile)
                        .sorted((a, b) -> b.compareTo(a)) // delete children before parent
                        .forEach(p -> p.toFile().delete());
            } catch (IOException ignored) {
            }
        }
    }
}
