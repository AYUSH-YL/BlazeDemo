package testcases;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.BlazeDemoHomePage;
import pages.ConfirmationPage;
import pages.FlightListPage;
import pages.PurchasePage;
import utilities.ExcelUtils;

public class BlazeDemoTest extends BaseTest {

    private BlazeDemoHomePage homePage;
    private FlightListPage flightListPage;
    private PurchasePage purchasePage;
    private ConfirmationPage confirmationPage;

    private final String departure;
    private final String destination;
    private final String name;
    private final String address;
    private final String city;
    private final String state;
    private final String zip;
    private final String cardType;
    private final String cardNumber;
    private final String cardName;

    @DataProvider(name = "bookingData")
    public static Object[][] bookingData() {
        return ExcelUtils.getTestData("src/test/resources/TestData.xlsx", "BookingSheet");
    }

    @Factory(dataProvider = "bookingData")
    public BlazeDemoTest(String departure, String destination, String name, String address,
                          String city, String state, String zip, String cardType,
                          String cardNumber, String cardName) {
        this.departure = departure;
        this.destination = destination;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardName = cardName;
    }

    @Test(priority = 1, groups = { "end-to-end" })
    public void step2_searchFlights() {
        log.info("Selecting Route -> " + departure + " to " + destination);

        driver.get(utilities.ConfigReader.getUrl());

        homePage = new BlazeDemoHomePage(driver);
        Assert.assertEquals(driver.getTitle(), "BlazeDemo", "Page title mismatch on Home Page");
        Assert.assertEquals(driver.getCurrentUrl(), "https://blazedemo.com/index.php", "Home Page URL mismatch");

        homePage.selectDepartureCity(departure);
        homePage.selectDestinationCity(destination);
        homePage.clickFindFlights();
    }

    @Test(priority = 2, dependsOnMethods = { "step2_searchFlights" }, groups = { "end-to-end" })
    public void step3_validateAndSelectFlight() {
        log.info("Verifying flight grid selection table.");
        flightListPage = new FlightListPage(driver);
        Assert.assertTrue(flightListPage.isFlightListDisplayed(),
                "Validation Failure: Flights listing table is missing.");
        flightListPage.chooseFirstAvailableFlight();
    }

    @Test(priority = 3, dependsOnMethods = { "step3_validateAndSelectFlight" }, groups = { "end-to-end" })
    public void step4_fillPassengerFormAndBook() {
        log.info("Filling passenger details for: " + name);

        purchasePage = new PurchasePage(driver);
        Assert.assertTrue(purchasePage.isPurchasePageDisplayed(),
                "Validation Failure: Purchase page header not displayed.");

        String cleanName = name.replaceAll("[^a-zA-Z0-9]", "");

        purchasePage.fillPersonalAndPaymentDetails(name, address, city, state, zip,
                cardType, cardNumber, cardName);

        takeCheckpointScreenshot("FormInputCheckpoint_Passenger_" + cleanName);
        purchasePage.clickPurchaseFlight();
    }

    @Test(priority = 4, dependsOnMethods = { "step4_fillPassengerFormAndBook" }, groups = { "end-to-end" })
    public void step5_validateConfirmationReceipt() {
        log.info("Asserting completion receipts for passenger: " + name);

        confirmationPage = new ConfirmationPage(driver);
        String confirmationMessage = confirmationPage.getConfirmationMessage();

        Assert.assertEquals(confirmationMessage, "Thank you for your purchase today!",
                "Validation Failure: Success banner mismatch!");

        String cleanName = name.replaceAll("[^a-zA-Z0-9]", "");
        takeCheckpointScreenshot("FinalBookingConfirmationReceipt_" + cleanName);
    }
}