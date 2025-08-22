package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwitchUserPage {
    private WebDriver driver;
    private WebDriverWait wait;

    //Locators
    private By profileImage = By.xpath("//*[@id='ProfileImage']");
    private By switchUserLink = By.xpath("//*[@id='li_SwitchUser']/a");
    private By modalTitle = By.xpath("//*[@id='dv_modal_AdminAsOtherUsers']/div/div[1]/h2");
    private By userTypeDropdown = By.xpath("//*[@id='dv_AdminAOU']/div/div/button");
    private By partnerUserOption = By.xpath("//span[contains(text(),'Partner user')]");
    private By partnerUserSearchBox = By.xpath("//*[@id='dv_AdminAOU']/div/div/div/div[1]/input");
    private By partnerUserResult = By.xpath("//*[contains(@id,'bs-select-') and contains(@class,'dropdown-menu')]//span[1]");
    private By partnerRoleDropdown = By.xpath("//*[@id='dv_PartnerUsers']/div/div/button");
    private By partnerRoleSearchBox = By.xpath("//*[@id='dv_PartnerUsers']/div/div/div/div[1]/input");
    private By partnerRoleResult = By.xpath("//*[contains(@id,'bs-select-') and contains(@class,'dropdown-menu')]//span[contains(text(),'Partner cluster member')]");
    private By switchUserButton = By.xpath("//*[@id='dv_modal_AdminAsOtherUsers']/div/div[3]/button[2]");
    private By loggedInEmail = By.xpath("//*[@id='Layout_EamilId']");
    private By toastMessage = By.xpath("//div[contains(@class,'toast-message')]");

    //Constructor
    public SwitchUserPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    //Methods
    public void clickProfileImage() {
        waitForToastToDisappear();
        wait.until(ExpectedConditions.elementToBeClickable(profileImage)).click();
    }

    public void clickSwitchUserLink() {
        wait.until(ExpectedConditions.elementToBeClickable(switchUserLink)).click();
    }

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle)).getText();
    }

    public void selectUserType(String userType) {
        wait.until(ExpectedConditions.elementToBeClickable(userTypeDropdown)).click();

        // Find the search box inside the dropdown and enter the userType
        By userTypeSearchBox = By.xpath("//*[@id='dv_AdminAOU']/div/div/div/div[1]/input");
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(userTypeSearchBox));
        searchBox.clear();
        searchBox.sendKeys(userType);

        // Wait for the dropdown result and click the first matching option
        By firstResult = By.xpath("//*[contains(@id,'bs-select-') and contains(@class,'dropdown-menu')]//span[contains(text(),'" + userType + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(firstResult)).click();

    }

    public void waitForLoaderToDisappear() {
        By loader = By.xpath("//img[contains(@alt, 'loading')]");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loader));
        } catch (Exception ignored) {
            // If loader is not present, continue
        }
    }

    public String searchAndSelectPartnerUser(String userName) {
        // Wait for loader to disappear
        waitForLoaderToDisappear();

        // Click the dropdown to activate the search box
        wait.until(ExpectedConditions.elementToBeClickable(partnerRoleDropdown)).click();

        // Enter the search term
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(partnerRoleSearchBox));
        searchBox.clear();
        searchBox.sendKeys(userName);

        // Wait for loader to disappear after typing
        waitForLoaderToDisappear();

        // Use the specific locator for the first result
        By firstResult = By.xpath("//*[contains(@id,'bs-select-48')]//span[2]");
        WebElement result = wait.until(ExpectedConditions.elementToBeClickable(firstResult));

        // Capture the displayed text (email or name) before clicking
        String fullText = result.getText().trim();

        // Extract email from square brackets
        String selectedEmail = extractEmailFromText(fullText);

        // Click the result
        result.click();

        return selectedEmail;
    }

    private String extractEmailFromText(String text) {
        // Regex to extract the first email from square brackets
        Pattern pattern = Pattern.compile("\\[(.*?@.*?)\\s*,");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return ""; // Return empty string if no email found
    }

    public void waitForDashboardAndCloseToast() {
        // Wait for the dashboard to be visible
        By dashboardLocator = By.xpath("//*[@id='MyTickets_Dashboard' and text()='My Info']");
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(dashboardLocator));

        // Check for toast close button and click if present
        By toastCloseBtn = By.xpath("//*[@id='toast-container']/div/button");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement closeBtn = shortWait.until(ExpectedConditions.visibilityOfElementLocated(toastCloseBtn));
            if (closeBtn.isDisplayed()) {
                closeBtn.click();
                // Wait for the toast to disappear
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.invisibilityOfElementLocated(toastCloseBtn));
            }
        } catch (Exception ignored) {
            // Toast notification is not present, continue
        }
    }

    public void searchAndSelectPartnerRole(String roleName) {
        wait.until(ExpectedConditions.elementToBeClickable(partnerRoleDropdown)).click();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(partnerRoleSearchBox));
        searchBox.sendKeys(roleName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(partnerRoleResult)).click();
    }

    public void clickSwitchUserButton() {
        wait.until(ExpectedConditions.elementToBeClickable(switchUserButton)).click();
    }

    public void waitForToastToDisappear() {
        By toast = By.cssSelector("div.toast.toast-info");
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(toast));
        } catch (Exception ignored) {
            // If toast is not present, continue
        }
    }

    // Get the text of the toast message
    public String getToastErrorMessageText() {
        WebElement toastElement = wait.until(ExpectedConditions.visibilityOfElementLocated(toastMessage));
        return toastElement.getText().trim();
    }

    public String getLoggedInEmail() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInEmail)).getText();
    }
}
