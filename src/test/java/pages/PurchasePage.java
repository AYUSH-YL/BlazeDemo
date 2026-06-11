package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utilities.WaitUtils;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PurchasePage {
	private static final Logger log = LogManager.getLogger(PurchasePage.class);
    private WebDriver driver;

    private By purchaseHeader = By.xpath("//h2[contains(text(),'Your flight')]");
    private By totalCostText = By.xpath("//p[contains(text(),'Total Cost:')]/em");
    
    private By inputName = By.id("inputName");
    private By addressField = By.id("address");
    private By cityField = By.id("city");
    private By stateField = By.id("state");
    private By zipCodeField = By.id("zipCode");
    private By cardTypeDropdown = By.id("cardType");
    private By creditCardNumberField = By.id("creditCardNumber");
    private By nameOnCardField = By.id("nameOnCard");
    private By purchaseFlightButton = By.cssSelector("input[type='submit'][value='Purchase Flight']");

    public PurchasePage(WebDriver driver) {
        this.driver = driver;
    }
    
    public boolean isPurchasePageDisplayed() {
        return WaitUtils.waitForElementVisible(driver, purchaseHeader, 10).isDisplayed();
    }

    public String getTotalCost() {
        return driver.findElement(totalCostText).getText().trim();
    }

    public void fillPersonalAndPaymentDetails(String name, String address, String city, String state, String zip, String cardType, String cardNumber, String cardName) {
        driver.findElement(inputName).sendKeys(name);
        driver.findElement(addressField).sendKeys(address);
        driver.findElement(cityField).sendKeys(city);
        driver.findElement(stateField).sendKeys(state);
        driver.findElement(zipCodeField).sendKeys(zip);
        
        Select selectCard = new Select(driver.findElement(cardTypeDropdown));
        try {
            selectCard.selectByValue(cardType.toLowerCase());
        } catch (NoSuchElementException e) {
            log.warn("Card type '" + cardType + "' not found. Defaulting to 'Visa'.");
            selectCard.selectByVisibleText("Visa");
        }
        
        driver.findElement(creditCardNumberField).sendKeys(cardNumber);
        driver.findElement(nameOnCardField).sendKeys(cardName);
    }
    
    public void clickPurchaseFlight() {
        driver.findElement(purchaseFlightButton).click();
    }
}