package com.g7crplus.uat.tests;

import com.g7crplus.uat.pages.LoginPage;
import com.g7crplus.uat.pages.SwitchUserPage;
import com.g7crplus.uat.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private SwitchUserPage switchUserPage;

    @BeforeClass
    public void setUpClass() {
        ChromeOptions options = new ChromeOptions();

        // CI safe args
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");

        // Headless for Azure DevOps
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        loginPage = new LoginPage(driver);
        switchUserPage = new SwitchUserPage(driver);
    }

    @BeforeMethod
    public void navigateToApp() {
        driver.get(ConfigReader.get("app.url"));
    }

    @Test
    public void validLoginTest() {
        loginPage.login(
                ConfigReader.get("customer.admin.email"),
                ConfigReader.get("customer.admin.password")
        );
        Assert.assertTrue(switchUserPage.isLoaded(),
                "Switch User page should be loaded after valid login.");
    }

    @Test
    public void invalidLoginTest() {
        loginPage.login(
                ConfigReader.get("customer.admin.email"),
                ConfigReader.get("invalid.password")
        );
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for invalid login.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}
