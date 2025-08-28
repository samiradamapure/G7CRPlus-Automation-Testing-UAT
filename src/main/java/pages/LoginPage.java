package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By emailInput = By.id("login_username");
    private final By msLoginButton = By.id("btn_defaultLogin");
    private final By passwordInput = By.name("passwd");
    private final By signInButton = By.id("idSIButton9");
    private final By errorMessage = By.id("passwordError");
    private final By staySignedInYes = By.id("idSIButton9");
    private final By directorySelection = By.id("Dv_PrimaryTenant");


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void enterEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void clickMicrosoftLogin() {
        driver.findElement(msLoginButton).click();
    }

    public void enterPassword(String password) {
        // Wait for the password field to be visible
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickSignIn() {
        driver.findElement(signInButton).click();
    }

//    public boolean isErrorMessageDisplayed1() {
//        return driver.findElement(errorMessage).isDisplayed();
//    }

    public boolean isErrorMessageDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("passwordError")));
            return error.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickStaySignedInYes() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(staySignedInYes)).click();
    }

    public boolean isDirectorySelectionDisplayed() {
        return !driver.findElements(directorySelection).isEmpty();
    }

}
