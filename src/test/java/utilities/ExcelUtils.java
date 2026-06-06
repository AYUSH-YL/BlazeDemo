package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    public static Object[][] getTestData(String filePath, String sheetName) {
        XSSFWorkbook workbook = null;
        FileInputStream fileInputStream = null;
        Object[][] data = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheet(sheetName);

            int totalRows = sheet.getLastRowNum(); 
            XSSFRow headerRow = sheet.getRow(0);
            int totalColumns = headerRow.getLastCellNum();

            data = new Object[totalRows][totalColumns];
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= totalRows; i++) {
                XSSFRow row = sheet.getRow(i);
                for (int j = 0; j < totalColumns; j++) {
                    if (row == null) {
                        data[i - 1][j] = "";
                    } else {
                        XSSFCell cell = row.getCell(j);
                        data[i - 1][j] = formatter.formatCellValue(cell);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Critical Exception: Failed to read from Excel data matrix at path " + filePath);
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                System.out.println("Exception encountered while closing Excel streams: " + e.getMessage());
            }
        }
        return data;
    }
}