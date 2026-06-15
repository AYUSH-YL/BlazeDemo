package testcases;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.BlazeDemoHomePage;
import pages.ConfirmationPage;
import pages.PortalGatewayPage;
import pages.PurchasePage;
import utilities.ConfigReader;

public class NegativeTest extends BaseTest {

    private BlazeDemoHomePage homePage;
    private PortalGatewayPage portalPage;
    private PurchasePage purchasePage;
    private ConfirmationPage confirmationPage;

    @DataProvider(name = "userData")
    public Object[][] userData() {
        return new Object[][] {
            { "Test User", "Wipro", "negativetest@wipro.com", "Test@123", "Test@123" }
        };
    }

    @DataProvider(name = "purchaseNegativeData")
    public Object[][] purchaseNegativeData() {
        return new Object[][] {
            { "", "", "", "", "", "visa", "", "", "", "" }
        };
    }

    @Test(priority = 1, dataProvider = "userData", groups = { "negative" })
    public void verifyRegistrationNegative(String name, String company, String email,
                                            String password, String confirmPassword) {
        driver.get(ConfigReader.getUrl().replace("index.php", "") + "register");

        portalPage = new PortalGatewayPage(driver);
        portalPage.executeNegativeRegistration(name, company, email, password);

        Assert.assertTrue(portalPage.verify419ErrorPageState(),
                "Known Defect: Registration request results in HTTP 419 Page Expired error.");

        takeCheckpointScreenshot("Registration_419_Error_Page");
    }

    @Test(priority = 2, dataProvider = "userData", groups = { "negative" })
    public void verifyLoginNegative(String name, String company, String email,
                                     String password, String confirmPassword) {
        driver.get(ConfigReader.getUrl().replace("index.php", "") + "login");

        portalPage = new PortalGatewayPage(driver);
        portalPage.executeNegativeLogin(email, password);

        Assert.assertTrue(portalPage.verify419ErrorPageState(),
                "Known Defect: Login request results in HTTP 419 Page Expired error.");

        takeCheckpointScreenshot("Login_419_Error_Page");
    }

    @Test(priority = 3, dataProvider = "purchaseNegativeData", groups = { "negative" })
    public void verifyPurchaseNegative(String name, String address, String city, String state,
                                        String zip, String cardType, String cardNumber,
                                        String month, String year, String nameOnCard) {

        driver.get(ConfigReader.getUrl().replace("index.php", "") + "purchase.php");

        purchasePage = new PurchasePage(driver);

        purchasePage.fillPersonalAndPaymentDetails(name, address, city, state, zip,
                cardType, cardNumber, nameOnCard);
        purchasePage.clickPurchaseFlight();

        confirmationPage = new ConfirmationPage(driver);

        String confirmationMessage = confirmationPage.getConfirmationMessage();

        Assert.assertEquals(confirmationMessage, "Thank you for your purchase today!",
                "Critical Defect: System generated a booking confirmation despite invalid passenger and payment information.");

        takeCheckpointScreenshot("PurchaseNegative_BookingConfirmed_WithInvalidData");
    }
}