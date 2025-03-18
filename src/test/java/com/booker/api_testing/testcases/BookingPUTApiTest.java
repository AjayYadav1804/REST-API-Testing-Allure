package com.booker.api_testing.testcases;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.booker.api_testing.api.RequestBuilder;
import com.booker.api_testing.reusables.CommonToAllTests;
import com.booker.api_testing.testcases.commons.BaseTest;
import com.booker.api_testing.utils.ExcelUtils;

import org.json.JSONObject;
import java.util.UUID;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
public class BookingPUTApiTest extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(BookingPUTApiTest.class);
    
   
    @Test(dataProvider = "testData", dataProviderClass = ExcelUtils.class)
    @Description("Validate Updation of Booking Test")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Story 4 - Update Booking")
    public void updateBookingTest(String endpoint, String acceptHeader, int expectedStatusCode, String expectedStatusLine) throws IOException {
    	logger.info("Put API test execution started...");
        // Generate unique values
        String firstname = "Jim" + UUID.randomUUID().toString().substring(0, 5);
        String lastname = "Brown" + UUID.randomUUID().toString().substring(0, 5);
        int totalprice = new Random().nextInt(1000) + 1;
        
        String token =CommonToAllTests.tokenGeneration();

        // Request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("firstname", firstname);
        requestBody.put("lastname", lastname);
        requestBody.put("totalprice", totalprice);
        requestBody.put("depositpaid", true);

        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkin", "2018-01-01");
        bookingDates.put("checkout", "2019-01-01");

        requestBody.put("bookingdates", bookingDates);
        requestBody.put("additionalneeds", "Breakfast");

        // Create request
        RequestBuilder requestBuilder = new RequestBuilder()
                .setBaseUri(BASE_URI)						// Set Base URI
                .setHeader("Accept", "application/json")    // Set Accept Header
                .setHeader("Content-Type", "application/json") // Set Content-Type Header
                .setHeader("Cookie", "token=" + token);
        
     // Build the RequestSpecification
        RequestSpecification requestSpec = requestBuilder.build();

        // Send POST request
        Response response = RestAssured.given()
        		.spec(requestSpec) 					   //Use the built request specification
        		.filter(new AllureRestAssured())       // Add Allure filter
                .filter(new RequestLoggingFilter())    // Log the request details
                .filter(new ResponseLoggingFilter())   // Log the response details
                .body(requestBody.toString())          // Send POST request
                .put("/" + endpoint);

        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP Response Code: 200");

        // Extract the response body
        JSONObject responseBody = new JSONObject(response.getBody().asString());

        // Assertions to verify the response

        Assert.assertEquals(response.jsonPath().getString("firstname"), firstname, "Firstname mismatch");
        Assert.assertEquals(response.jsonPath().getString("lastname"), lastname, "Lastname mismatch");
        Assert.assertEquals((int) response.jsonPath().getInt("totalprice"), totalprice, "Total price mismatch");
        Assert.assertTrue(response.jsonPath().getBoolean("depositpaid"), "Deposit paid mismatch");

        String checkinDate = response.jsonPath().getString("bookingdates.checkin");
        String checkoutDate = response.jsonPath().getString("bookingdates.checkout");
        Assert.assertEquals(checkinDate, "2018-01-01", "Checkin date mismatch");
        Assert.assertEquals(checkoutDate, "2019-01-01", "Checkout date mismatch");

        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), "Breakfast", "Additional needs mismatch");
        
        logger.info("PUT request successful, booking ID: {}", endpoint);
        
        // Print the entire response body
        System.out.println("Response Body: " + response.getBody().asString());
    }
    
    @DataProvider(name = "testData")
    public Iterator<Object[]> readTestDataFromExcel(Method testMethod) throws IOException {
    	 String excelFilePath = "src/test/java/resources/Apitestdata.xlsx";
        FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheet("Testcases");
        Iterator<Row> rowIterator = sheet.iterator();

        // Skip header row
        rowIterator.next();

        // Use a List to gather test data inputs for the specific test method
        List<Object[]> testData = new ArrayList<>();
        String testName = testMethod.getName();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String methodName = row.getCell(0).getStringCellValue();
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
        logger.info("Test data loaded for method: {}", testName);
        return testData.iterator();
    }


}
