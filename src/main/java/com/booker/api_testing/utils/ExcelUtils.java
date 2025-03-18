package com.booker.api_testing.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import com.booker.api_testing.consonants.FileNameConstants;

public class ExcelUtils {
    
    @DataProvider(name = "testData")
    public static Iterator<Object[]> readTestDataFromExcel(Method testMethod) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(FileNameConstants.EXCEL_TEST_DATA));
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheet("Testcases");
        Iterator<Row> rowIterator = sheet.iterator();

        // Skip header row
        rowIterator.next();

        List<Object[]> testData = new ArrayList<>();
        String testName = testMethod.getName();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String methodName = row.getCell(0).getStringCellValue();

            // Match test method name to the Excel row's method name
            if (testName.equals(methodName)) {
                String endpoint = row.getCell(1).getStringCellValue();
                String acceptHeader = row.getCell(2).getStringCellValue();
                int expectedStatusCode = (int) row.getCell(3).getNumericCellValue();
                String expectedStatusLine = row.getCell(4).getStringCellValue();

                testData.add(new Object[]{endpoint, acceptHeader, expectedStatusCode, expectedStatusLine});
            }
        }

        workbook.close();
        fileInputStream.close();
        return testData.iterator();
    }

}
