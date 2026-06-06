package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import base.BaseTest;

public class MyListeners implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/ExtentReport.html");
        sparkReporter.config().setDocumentTitle("BlazeDemo Automation Report");
        sparkReporter.config().setReportName("End To End Regression Results");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Environment", "QA Pipeline");
        extent.setSystemInfo("Author", "Ayush");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting Test Execution: " + result.getName());
        ExtentTest test = extent.createTest(result.getName());
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("TEST PASSED: " + result.getName());
        testThread.get().log(Status.PASS, "Test Case Passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TEST FAILED: " + result.getName() + " -> Taking automated screenshot.");
        testThread.get().log(Status.FAIL, "Test Case Failed: " + result.getName());
        testThread.get().log(Status.FAIL, result.getThrowable());

        try {
            Object testClass = result.getInstance();
            org.openqa.selenium.WebDriver driver = ((BaseTest) testClass).getDriver();
            
            if (driver != null) {
                String screenshotPath = utilities.ScreenshotUtils.captureScreenshot(driver, result.getName());
                testThread.get().addScreenCaptureFromPath("../" + screenshotPath);
            }
        } catch (Exception e) {
            System.out.println("Exception thrown while attaching screenshot to Extent Report: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test Skipped: " + result.getName());
        testThread.get().log(Status.SKIP, "Test Case Skipped: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }
    public static ExtentTest getTestLog() {
        return testThread.get();
    }
}