package testcases;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BlazeDemoHomePage;
import pages.FlightListPage;
import pages.PurchasePage;
import pages.ConfirmationPage;
import utilities.ExcelUtils;

public class BlazeDemoTest extends BaseTest {
    @DataProvider(name = "bookingData")
    public Object[][] getBookingData() {
        String excelPath = "src/test/resources/TestData.xlsx";
        String sheetName = "BookingSheet";
        log.info("Fetching test execution metrics from Excel sheet: " + sheetName);
        return ExcelUtils.getTestData(excelPath, sheetName);
    }

    @Test(dataProvider = "bookingData", groups = { "end-to-end" })
    public void testFlightBookingJourney(String departure, String destination, String name, String address, 
                                         String city, String state, String zip, String cardType, 
                                         String cardNumber, String cardName) {
        
        SoftAssert softAssert = new SoftAssert();
        log.info("Starting End-to-End Flight Booking journey validation test loop.");
        String appUrl = utilities.ConfigReader.getUrl();
        log.info("Navigating browser session context directly to: " + appUrl);
        driver.get(appUrl);
        
        BlazeDemoHomePage homePage = new BlazeDemoHomePage(driver);
        log.info("Selecting Departure City: " + departure + ", Destination City: " + destination);
        homePage.selectDepartureCity(departure);
        homePage.selectDestinationCity(destination);
        homePage.clickFindFlights();

        FlightListPage flightListPage = new FlightListPage(driver);
        log.info("Reviewing structural layout grid matching flight routing paths.");
        boolean isFlightListVisible = flightListPage.isFlightListDisplayed();
        softAssert.assertTrue(isFlightListVisible, "Validation Failure: Flight grid header is missing.");
        
        log.info("Clicking first available active airliner option.");
        flightListPage.chooseFirstAvailableFlight();

        PurchasePage purchasePage = new PurchasePage(driver);
        boolean isPurchasePageVisible = purchasePage.isPurchasePageDisplayed();
        softAssert.assertTrue(isPurchasePageVisible, "Validation Failure: Checkout form window is missing.");
        
        String cost = purchasePage.getTotalCost();
        log.info("Extracted total customer checkout pricing summary: " + cost);
        softAssert.assertFalse(cost.isEmpty(), "Validation Failure: Estimated purchase fee string is empty.");

        log.info("Typing reservation customer and payment field sets for: " + name);
        purchasePage.fillPersonalAndPaymentDetails(name, address, city, state, zip, cardType, cardNumber, cardName);
        purchasePage.clickPurchaseFlight();
        
        ConfirmationPage confirmationPage = new ConfirmationPage(driver);
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        log.info("Verification receipt record returned message banner: " + confirmationMessage);
        softAssert.assertEquals(confirmationMessage, "Thank you for your purchase today.", 
                                "Validation Failure: Confirmation screen header banner mismatch.");

        takeCheckpointScreenshot("FlightBookingConfirmation");

        log.info("Concluding test thread block. Compiling assertion diagnostics parameters.");
        softAssert.assertAll();
    }
}