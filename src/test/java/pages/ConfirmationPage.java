package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.WaitUtils;

public class ConfirmationPage {
    private WebDriver driver;

    private By thankYouHeader = By.xpath("//h1[contains(text(),'Thank you for your purchase today!')]");

    public ConfirmationPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getConfirmationMessage() {
        return WaitUtils.waitForElementVisible(driver, thankYouHeader, 10).getText().trim();
    }
}