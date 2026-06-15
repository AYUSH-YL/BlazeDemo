package testcases;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BlazeDemoHomePage;
import pages.PortalGatewayPage;

public class LoginTest extends BaseTest {

    private BlazeDemoHomePage homePage;
    private PortalGatewayPage portalPage;

    @Test(priority = 1, groups = { "end-to-end" })
    public void validateBrokenPortalGateways() {
        log.info("Starting Portal Security Regression Sweep (Once).");
        
        String appUrl = utilities.ConfigReader.getUrl();
        log.info("Navigating directly to application index: " + appUrl);
        driver.get(appUrl);
        
        homePage = new BlazeDemoHomePage(driver);
        portalPage = new PortalGatewayPage(driver);
        
        homePage.clickHomeButtonLink();
        portalPage.clickRegisterLink();
        
        log.info("Injecting hardcoded registration placeholders.");
        portalPage.executeNegativeRegistration("DemoUser", "Wipro", "test@wipro.com", "Password123");
        
        takeCheckpointScreenshot("Registration_419_Error_Page");
        Assert.assertTrue(portalPage.verify419ErrorPageState(), "Error: Registration failed to trigger a 419 Page state.");
        log.info("Known error : Registration 419 Page.");
        
        portalPage.navigateBackTwoSteps();
        
        log.info("Injecting hardcoded login placeholders.");
        portalPage.executeNegativeLogin("login@wipro.com", "SecurePass99");
        
        takeCheckpointScreenshot("Login_419_Error_Page");
        Assert.assertTrue(portalPage.verify419ErrorPageState(), "Error: Login failed to trigger a 419 Page state.");
        log.info("Known error : 419 Page.");
        
        portalPage.navigateBackTwoSteps();
        log.info("Portal Sweep Complete.");
    }
}