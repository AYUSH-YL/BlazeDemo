package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utilities.ScreenshotUtils;

public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        log.info("Initializing browser automation setup.");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
    
        if (System.getenv("RUNNING_IN_DOCKER") != null) {
            log.info("System environment variable detected: Docker Pipeline. Forcing headless configuration flags.");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.setBinary("/usr/bin/google-chrome");
        } else {
            log.info("System environment variable missing: Local Eclipse Sandbox. Launching visible GUI browser panel.");
        }
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        log.info("Chrome Browser instance initialized successfully.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            log.info("Browser instance terminated cleanly.");
        }
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public void takeCheckpointScreenshot(String stepName) {
        log.info("Triggering manual checkpoint screenshot: " + stepName);
        ScreenshotUtils.captureScreenshot(driver, stepName);
    }
}