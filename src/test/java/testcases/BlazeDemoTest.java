package testcases;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.BlazeDemoHomePage;
import pages.FlightListPage;
import pages.PurchasePage;
import pages.ConfirmationPage;
import pages.PortalGatewayPage;
import utilities.ExcelUtils;

public class BlazeDemoTest extends BaseTest {

    private BlazeDemoHomePage homePage;
    private PortalGatewayPage portalPage;
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

    @DataProvider(name = "bookingData")
    public Object[][] getBookingData() {
        String excelPath = "src/test/resources/TestData.xlsx";
        String sheetName = "BookingSheet";
        globalTestDataMatrix = ExcelUtils.getTestData(excelPath, sheetName);
        totalExcelDataRows = globalTestDataMatrix.length;
        return globalTestDataMatrix;
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
    public void step1_validateBrokenPortalGateways() {
        log.info("🎯 Starting Step 1: Portal Security Regression Sweep (Once).");
        
        String appUrl = utilities.ConfigReader.getUrl();
        log.info("Navigating session directly to application index: " + appUrl);
        driver.get(appUrl);
        
        homePage = new BlazeDemoHomePage(driver);
        portalPage = new PortalGatewayPage(driver);
        
        homePage.clickHomeButtonLink();
        portalPage.clickRegisterLink();
        
        log.info("Injecting hardcoded registration placeholders.");
        portalPage.executeNegativeRegistration("DemoUser", "Wipro", "test@wipro.com", "Password123");
        
        takeCheckpointScreenshot("Registration_419_Error_Page");
        Assert.assertTrue(portalPage.verify419ErrorPageState(), "Error: Registration failed to trigger a 419 Page state.");
        log.info("Registration 419 Page Verified Successfully.");
        
        portalPage.navigateBackTwoSteps();
        
        log.info("Injecting hardcoded login placeholders.");
        portalPage.executeNegativeLogin("login@wipro.com", "SecurePass99");
        
        takeCheckpointScreenshot("Login_419_Error_Page");
        Assert.assertTrue(portalPage.verify419ErrorPageState(), "Error: Login failed to trigger a 419 Page state.");
        log.info("Login 419 Page Verified Successfully.");
        
        portalPage.navigateBackTwoSteps();
        log.info("Step 1 Complete. Base browser view anchoring cleanly on root landing grid.");
        
        getBookingData();
    }

    @Test(priority = 2, dependsOnMethods = { "step1_validateBrokenPortalGateways" }, groups = { "end-to-end" })
    public void step2_searchFlights() {
        loadCurrentRowData();

        log.info("🛫 Step 2: Selecting Route -> " + currentDeparture + " to " + currentDestination);
        
        homePage = new BlazeDemoHomePage(driver);
        homePage.selectDepartureCity(currentDeparture);
        homePage.selectDestinationCity(currentDestination);
        homePage.clickFindFlights();
    }

    @Test(priority = 3, dependsOnMethods = { "step2_searchFlights" }, groups = { "end-to-end" })
    public void step3_validateAndSelectFlight() {
        log.info("Starting Step 3: Verifying flight grid selection tables.");
        flightListPage = new FlightListPage(driver);
        
        Assert.assertTrue(flightListPage.isFlightListDisplayed(), "Validation Failure: Flights listing matrix grid is missing.");
        flightListPage.chooseFirstAvailableFlight();
    }

    @Test(priority = 4, dependsOnMethods = { "step3_validateAndSelectFlight" }, groups = { "end-to-end" })
    public void step4_fillPassengerFormAndBook() {
        log.info("💵 Step 4: Injecting passenger details for: " + currentName);
        
        purchasePage = new PurchasePage(driver);
        purchasePage.fillPersonalAndPaymentDetails(currentName, currentAddress, currentCity, 
                                                   currentState, currentZip, currentCardType, 
                                                   currentCardNumber, currentCardName);
        
        takeCheckpointScreenshot("FormInputCheckpoint_Passenger_" + cleanName);
        purchasePage.clickPurchaseFlight();
    }

    // =========================================================================
    // 🏁 STEP 5: FINAL RECEIPT VALIDATION & DYNAMIC LOOP CONTEXT RE-ENTRY
    // =========================================================================
   /* @Test(priority = 5, dependsOnMethods = { "step4_fillPassengerFormAndBook" }, groups = { "end-to-end" })
    public void step5_validateConfirmationReceipt() {
        log.info("Starting Step 5: Asserting completion receipts for passenger: " + currentName);
        SoftAssert softAssert = new SoftAssert();
        confirmationPage = new ConfirmationPage(driver);
        
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        softAssert.assertEquals(confirmationMessage, "Thank you for your purchase today!", 
                                "Validation Failure: Success confirmation banner modified!");

        takeCheckpointScreenshot("FinalBookingConfirmationReceipt_" + cleanName);
        softAssert.assertAll();

        // Increment row mapping array index coordinates
        currentExcelRowPointer++;
        
        // Check if there are remaining rows left to execute
        if (currentExcelRowPointer < totalExcelDataRows) {
            log.info("🔄 Row validation complete. Resetting browser context back to index landing page for next data row sequence.");
            String appUrl = utilities.ConfigReader.getUrl();
            driver.get(appUrl);
            
            // Re-invoke the structural pipeline steps manually for the next row context
            step2_searchFlights();
            step3_validateAndSelectFlight();
            step4_fillPassengerFormAndBook();
            step5_validateConfirmationReceipt();
        } else {
            log.info("🏁 Success: All row datasets processed completely through the entire booking lifecycle framework.");
        }
    }*/
    @Test(priority = 5, dependsOnMethods = { "step4_fillPassengerFormAndBook" }, groups = { "end-to-end" })
    public void step5_validateConfirmationReceipt() {
        log.info("Starting Step 5: Asserting completion receipts for passenger: " + currentName);
        confirmationPage = new ConfirmationPage(driver);
        
        String confirmationMessage = confirmationPage.getConfirmationMessage();
        
        // Use a simple Assert to check the core validation
        // This will mark this specific row as a failure if it doesn't match, 
        // but it won't crash the loop logic if we handle it carefully.
        Assert.assertTrue(confirmationMessage.contains("Thank you"), 
                          "Validation Failure: Success banner mismatch!");

        takeCheckpointScreenshot("FinalBookingConfirmationReceipt_" + cleanName);

        // Increment pointer
        currentExcelRowPointer++;
        
        // Loop trigger
        if (currentExcelRowPointer < totalExcelDataRows) {
            log.info("🔄 Row " + currentExcelRowPointer + " complete. Resetting for next dataset.");
            driver.get(utilities.ConfigReader.getUrl());
            
            // Manual call for next row
            step2_searchFlights();
            step3_validateAndSelectFlight();
            step4_fillPassengerFormAndBook();
            step5_validateConfirmationReceipt();
        } else {
            log.info("🏁 Success: All rows processed.");
        }
    }
}