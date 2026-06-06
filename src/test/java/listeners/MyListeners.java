package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class MyListeners implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("▶️ Starting Test Execution: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ TEST PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ TEST FAILED: " + result.getName() + " -> Taking automated screenshot.");
        // This is where your screenshot utility will hook in automatically later!
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test Skipped: " + result.getName());
    }
}