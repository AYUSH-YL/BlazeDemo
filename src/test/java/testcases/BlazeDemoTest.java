package testcases;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BlazeDemoHomePage;
import pages.FlightListPage;
import pages.PurchasePage;
import pages.ConfirmationPage;
import utilities.ExcelUtils;

public class BlazeDemoTest extends BaseTest {

    private BlazeDemoHomePage homePage;
    private FlightListPage flightListPage;
    private PurchasePage purchasePage;
    private ConfirmationPage confirmationPage;

    private Object[][] globalTestDataMatrix;
    private int currentExcelRowPointer = 0;
    private int totalExcelDataRows = 0;

    private String currentDeparture;
    private String currentDestination;
    private String currentName;
    private String currentAddress;
    private String currentCity;
    private String currentState;
    private String currentZip;
    private String currentCardType;
    private String currentCardNumber;
    private String currentCardName;
    private String cleanName;

    @BeforeClass(alwaysRun = true)
    public void initializeTestData() {
        log.info("Initializing Data Provider Streams for Flight Regression Suite.");
        String excelPath = "src/test/resources/TestData.xlsx";
        String sheetName = "BookingSheet";
        globalTestDataMatrix = ExcelUtils.getTestData(excelPath, sheetName);
        totalExcelDataRows = globalTestDataMatrix.length;
    }

    private void loadCurrentRowData() {
        this.currentDeparture = globalTestDataMatrix[currentExcelRowPointer][0].toString();
        this.currentDestination = globalTestDataMatrix[currentExcelRowPointer][1].toString();
        this.currentName = globalTestDataMatrix[currentExcelRowPointer][2].toString();
        this.currentAddress = globalTestDataMatrix[currentExcelRowPointer][3].toString();
        this.currentCity = globalTestDataMatrix[currentExcelRowPointer][4].toString();
        this.currentState = globalTestDataMatrix[currentExcelRowPointer][5].toString();
        this.currentZip = globalTestDataMatrix[currentExcelRowPointer][6].toString();
        this.currentCardType = globalTestDataMatrix[currentExcelRowPointer][7].toString();
        this.currentCardNumber = globalTestDataMatrix[currentExcelRowPointer][8].toString();
        this.currentCardName = globalTestDataMatrix[currentExcelRowPointer][9].toString();
        this.cleanName = currentName.replaceAll("[^a-zA-Z0-9]", "");
    }

    @Test(priority = 1, groups = { "end-to-end" })
    public void step2() {
        loadCurrentRowData();
        log.info("Selecting Route -> " + currentDeparture + " to " + currentDestination);
        
        driver.get(utilities.ConfigReader.getUrl());
        
        homePage = new BlazeDemoHomePage(driver);
        homePage.selectDepartureCity(currentDeparture);
        homePage.selectDestinationCity(currentDestination);
        homePage.clickFindFlights();
    }

    @Test(priority = 2, dependsOnMethods = { "step2" }, groups = { "end-to-end" })
    public void step3() {
        log.info("Verifying flight grid selection tables.");
        flightListPage = new FlightListPage(driver);
        Assert.assertTrue(flightListPage.isFlightListDisplayed(), "Validation Failure: Flights listing matrix grid is missing.");
        flightListPage.chooseFirstAvailableFlight();
    }

    @Test(priority = 3, dependsOnMethods = { "step3" }, groups = { "end-to-end" })
    public void step4() {
        log.info("Filling passenger details for: " + currentName);
        purchasePage = new PurchasePage(driver);
        purchasePage.fillPersonalAndPaymentDetails(currentName, currentAddress, currentCity, 
                                                   currentState, currentZip, currentCardType, 
                                                   currentCardNumber, currentCardName);
        
        takeCheckpointScreenshot("FormInputCheckpoint_Passenger_" + cleanName);
        purchasePage.clickPurchaseFlight();
    }

    @Test(priority = 4, dependsOnMethods = { "step4" }, groups = { "end-to-end" })
    public void step5() {
        log.info("Asserting completion receipts for passenger: " + currentName);
        confirmationPage = new ConfirmationPage(driver);
        
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        Assert.assertTrue(confirmationMessage.contains("Thank you"), "Validation Failure: Success banner mismatch!");

        takeCheckpointScreenshot("FinalBookingConfirmationReceipt_" + cleanName);

        currentExcelRowPointer++;
        
        if (currentExcelRowPointer < totalExcelDataRows) {
            log.info("Row " + currentExcelRowPointer + " complete. Resetting for next dataset.");
            step2();
            step3();
            step4();
            step5();
        } else {
            log.info("Success: All rows processed.");
        }
    }
}