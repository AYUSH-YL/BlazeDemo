package base;

import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utilities.ConfigReader;
import utilities.ScreenshotUtils;

public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        log.info("Initializing browser automation setup.");
        
        String browser = ConfigReader.getProperty("browser");
        if (browser == null) {
            log.warn("Browser property missing from config. Defaulting to Chrome.");
            browser = "chrome";
        }
        
        log.info("Target browser selected from configuration: " + browser);

        switch (browser.toLowerCase().trim()) {
            
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                
                if (System.getenv("RUNNING_IN_DOCKER") != null) {
                    log.info("System environment variable detected: Docker Pipeline. Forcing headless configuration flags.");
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.setBinary("/usr/bin/google-chrome");
                } else {
                    log.info("System environment variable missing: Local Eclipse Sandbox. Launching visible GUI Chrome browser.");
                }
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                log.info("Launching visible Firefox browser panel locally.");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                log.info("Launching visible Edge browser panel locally.");
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
                break;

            default:
                log.error("CRITICAL error: Unsupported Browser variant requested: " + browser);
                throw new RuntimeException("Unsupported Browser framework assignment: " + browser);
        }
        
        driver.manage().window().maximize();
        
        String implicitWaitStr = ConfigReader.getProperty("implicitWait");
        int waitSeconds = (implicitWaitStr != null) ? Integer.parseInt(implicitWaitStr) : 10;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitSeconds));
        
        log.info("Browser instance initialized successfully with implicit wait of " + waitSeconds + " seconds.");
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