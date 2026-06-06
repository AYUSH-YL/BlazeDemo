package utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

public class ScreenshotUtils {

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        
        File directory = new File("screenshots");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destinationPath = "screenshots/" + fileName;
        File destination = new File(destinationPath);
        try {
            FileHandler.copy(source, destination);
            System.out.println("Screenshot captured successfully: " + destinationPath);
        } catch (IOException e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
        }
        return destinationPath;
    }
}