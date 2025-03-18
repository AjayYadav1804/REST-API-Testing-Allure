package com.booker.api_testing.testcases;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.booker.api_testing.api.RequestBuilder;
import com.booker.api_testing.reusables.CommonToAllTests;
import com.booker.api_testing.testcases.commons.BaseTest;
import com.booker.api_testing.utils.ExcelUtils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

public class BookingDELETEApiTest extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(BookingDELETEApiTest.class);
    private Response response;

    @Test(dataProvider = "testData", dataProviderClass = ExcelUtils.class)
    @Description("Validate Delete booking API based on provided Excel data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Story-5: Delete API Validation with Excel Data")
    public void deleteBookingAPITest(String endpoint, String acceptHeader, int expectedStatusCode, String expectedStatusLine) throws IOException {
        logger.info("Testing endpoint: {}, Accept Header: {}", endpoint, acceptHeader);
       
        String token =CommonToAllTests.tokenGeneration();
        RequestBuilder requestBuilder = new RequestBuilder()
                .setBaseUri(BASE_URI)                // Set Base URI
                .setHeader("Accept", acceptHeader)  // Set Accept Header
                .setHeader("Content-Type", "application/json") // Set Content-Type Header
                .setHeader("Cookie", "token=" + token);
        
        // Build the RequestSpecification
        RequestSpecification requestSpec = requestBuilder.build();

        response = RestAssured.given()
        		.spec(requestSpec)					   //Use the built request specification
        		.filter(new AllureRestAssured())       // Add Allure filter
                .filter(new RequestLoggingFilter())    // Log the request details
                .filter(new ResponseLoggingFilter())   // Log the response details
        		.delete("/" + endpoint);				//Delete request
        Assert.assertEquals(response.getStatusCode(), 201, "Expected HTTP Response Code: 201");
        logger.info("Test passed with expected status code: {} and status line: {}", expectedStatusCode, expectedStatusLine);
    }
    
    

}
