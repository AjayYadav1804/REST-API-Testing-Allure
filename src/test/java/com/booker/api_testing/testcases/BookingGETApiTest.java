package com.booker.api_testing.testcases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import com.booker.api_testing.api.RequestBuilder;
import com.booker.api_testing.consonants.FileNameConstants;
import com.booker.api_testing.testcases.commons.BaseTest;
import com.booker.api_testing.utils.ExcelUtils;

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

public class BookingGETApiTest extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(BookingGETApiTest.class);
    private Response response;
    
    @Test(dataProvider = "testData", dataProviderClass = ExcelUtils.class)
    @Description("Validate booking API based on provided Excel data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Story-1: API Validation with Excel Data")
    public void validateBookingAPITest(String endpoint, String acceptHeader, int expectedStatusCode, String expectedStatusLine) {
        logger.info("Testing endpoint: {}, Accept Header: {}", endpoint, acceptHeader);

        RequestBuilder requestBuilder = new RequestBuilder()
                .setBaseUri(BASE_URI)                // Set Base URI
                .setHeader("Accept", acceptHeader);  // Set Accept Header
        
        // Build the RequestSpecification
        RequestSpecification requestSpec = requestBuilder.build();

        response = RestAssured.given()
        		.spec(requestSpec)					   //Use the built request specification
        		.filter(new AllureRestAssured())       // Add Allure filter
                .filter(new RequestLoggingFilter())    // Log the request details
                .filter(new ResponseLoggingFilter())   // Log the response details
        		.get("/" + endpoint);				   // Get request

        Assert.assertEquals(response.getStatusCode(), expectedStatusCode, "Unexpected status code");
        Assert.assertEquals(response.getStatusLine(), expectedStatusLine, "Unexpected status line");
        logger.info("Test passed with expected status code: {} and status line: {}", expectedStatusCode, expectedStatusLine);
    }

    
    @Test(dataProvider = "testData", dataProviderClass = ExcelUtils.class)
    @Description("Verify successful retrieval of specific booking details")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Story-2: Get Booking Details")
    public void getBookingDetailsSuccessTest(String endpoint, String acceptHeader, int expectedStatusCode, String expectedStatusLine) {
        //String bookingId = "1748";  // Valid booking ID
        logger.info("Testing valid booking ID: {}", endpoint);

        RequestBuilder requestBuilder = new RequestBuilder()
                .setBaseUri(BASE_URI)
                .setHeader("Accept", "application/json");
        
     // Build the RequestSpecification
        RequestSpecification requestSpec = requestBuilder.build();

        response =  RestAssured.given()
        		.spec(requestSpec)					   //Use the built request specification
        		.filter(new AllureRestAssured())       // Add Allure filter
                .filter(new RequestLoggingFilter())    // Log the request details
                .filter(new ResponseLoggingFilter())   // Log the response details
        		.get("/" + endpoint);  				   // Get method
        
       
       File schema = new File(FileNameConstants.Get_JSON_SCHEMA);
       response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
        

        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP Response Code: 200");

        // Positive assertions (actual values may vary, adjust accordingly)
        Assert.assertEquals(response.jsonPath().getString("firstname"), "John", "Firstname mismatch");
        Assert.assertEquals(response.jsonPath().getString("lastname"), "Smith", "Lastname mismatch");
        Assert.assertEquals((int) response.jsonPath().getInt("totalprice"), 111, "Total price mismatch");
        Assert.assertTrue(response.jsonPath().getBoolean("depositpaid"), "Deposit paid mismatch");

        String checkinDate = response.jsonPath().getString("bookingdates.checkin");
        String checkoutDate = response.jsonPath().getString("bookingdates.checkout");
        Assert.assertEquals(checkinDate, "2018-01-01", "Checkin date mismatch");
        Assert.assertEquals(checkoutDate, "2019-01-01", "Checkout date mismatch");

        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), "super bowls", "Additional needs mismatch");
        logger.info("Positive test passed for booking ID: {}", endpoint);
    }
    

}
