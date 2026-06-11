package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utilities.WaitUtils;

public class BlazeDemoHomePage {
    private WebDriver driver;

    private By departureDropdown = By.name("fromPort");
    private By destinationDropdown = By.name("toPort");
    private By findFlightsButton = By.cssSelector("input[type='submit'].btn-primary");
    private By homeButtonLink = By.linkText("home");

    public BlazeDemoHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickHomeButtonLink() {
        WaitUtils.waitForElementClickable(driver, homeButtonLink, 10).click();
    }

    public void selectDepartureCity(String departure) {
        WaitUtils.waitForElementVisible(driver, departureDropdown, 10);
        Select select = new Select(driver.findElement(departureDropdown));
        select.selectByValue(departure);
    }

    public void selectDestinationCity(String destination) {
        Select select = new Select(driver.findElement(destinationDropdown));
        select.selectByValue(destination);
    }

    public void clickFindFlights() {
        driver.findElement(findFlightsButton).click();
    }
}