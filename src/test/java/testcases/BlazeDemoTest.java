package testcases;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BlazeDemoHomePage;
import pages.FlightListPage;
import pages.PurchasePage;
import pages.ConfirmationPage;

public class BlazeDemoTest extends BaseTest {

    @DataProvider(name = "bookingData")
    public Object[][] getBookingData() {
        return new Object[][] {
            { "Paris", "Buenos Aires", "John Doe", "123 Main St", "Anytown", "State", "12345", "Visa", "1234567890123456", "John Doe" }
        };
    }

    @Test(dataProvider = "bookingData", groups = { "end-to-end" })
    public void testFlightBookingJourney(String departure, String destination, String name, String address, String city, String state, String zip, String cardType, String cardNumber, String cardName) {
        SoftAssert softAssert = new SoftAssert();

        driver.get("https://blazedemo.com/index.php");
        BlazeDemoHomePage homePage = new BlazeDemoHomePage(driver);
        homePage.selectDepartureCity(departure);
        homePage.selectDestinationCity(destination);
        homePage.clickFindFlights();

        FlightListPage flightListPage = new FlightListPage(driver);
        softAssert.assertTrue(flightListPage.isFlightListDisplayed());
        flightListPage.chooseFirstAvailableFlight();

        PurchasePage purchasePage = new PurchasePage(driver);
        softAssert.assertTrue(purchasePage.isPurchasePageDisplayed());
        
        String cost = purchasePage.getTotalCost();
        softAssert.assertFalse(cost.isEmpty());

        purchasePage.fillPersonalAndPaymentDetails(name, address, city, state, zip, cardType, cardNumber, cardName);
        purchasePage.clickPurchaseFlight();
        
        ConfirmationPage confirmationPage = new ConfirmationPage(driver);
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        softAssert.assertEquals(confirmationMessage, "Thank you for your purchase today!");

        takeCheckpointScreenshot("FlightBookingConfirmation");

        softAssert.assertAll();
    }
}