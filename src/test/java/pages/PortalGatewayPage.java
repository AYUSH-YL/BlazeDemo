package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.WaitUtils;

public class PortalGatewayPage {
    private WebDriver driver;
    
    private By loginEmailField = By.id("email");
    private By loginPasswordField = By.id("password");
    private By loginSubmitButton = By.cssSelector("button[type='submit']");
    
    private By registerLinkButton = By.linkText("Register");

    private By regNameField = By.id("name");
    private By regCompanyField = By.id("company");
    private By regEmailField = By.id("email");
    private By regPasswordField = By.id("password");
    private By regConfirmPasswordField = By.id("password-confirm");
    private By regSubmitButton = By.cssSelector("button[type='submit']");

    public PortalGatewayPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickRegisterLink() {
        WaitUtils.waitForElementClickable(driver, registerLinkButton, 10).click();
    }

    public void executeNegativeRegistration(String name, String company, String email, String password) {
        WaitUtils.waitForElementVisible(driver, regNameField, 10);
        driver.findElement(regNameField).sendKeys(name);
        driver.findElement(regCompanyField).sendKeys(company);
        driver.findElement(regEmailField).sendKeys(email);
        driver.findElement(regPasswordField).sendKeys(password);
        driver.findElement(regConfirmPasswordField).sendKeys(password);
        driver.findElement(regSubmitButton).click();
    }

    public void executeNegativeLogin(String email, String password) {
        WaitUtils.waitForElementVisible(driver, loginEmailField, 10);
        driver.findElement(loginEmailField).sendKeys(email);
        driver.findElement(loginPasswordField).sendKeys(password);
        driver.findElement(loginSubmitButton).click();
    }

    public boolean verify419ErrorPageState() {
        String source = driver.getPageSource();
        return source.contains("419") || source.contains("Page Expired");
    }

    public void navigateBackTwoSteps() {
        driver.navigate().back();
        driver.navigate().back();
    }
}