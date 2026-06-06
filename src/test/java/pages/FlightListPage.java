package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.WaitUtils;

public class FlightListPage {
    private WebDriver driver;

    private By flightsHeader = By.xpath("//h3[contains(text(),'Flights from')]");
    private By chooseFlightButton = By.cssSelector("input[type='submit'][value='Choose This Flight']");

    public FlightListPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isFlightListDisplayed() {
        return WaitUtils.waitForElementVisible(driver, flightsHeader, 10).isDisplayed();
    }

    public void chooseFirstAvailableFlight() {
        WaitUtils.waitForElementClickable(driver, chooseFlightButton, 10).click();
    }
}