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
import org.testng.Assert;
import org.testng.annotations.Test;

import com.booker.api_testing.api.RequestBuilder;
import com.booker.api_testing.testcases.commons.BaseTest;

import org.json.JSONObject;

import java.util.UUID;
import java.util.Random;

public class BookingPOSTApiTest extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(BookingPOSTApiTest.class);
    private static final String BASE_URI = "https://restful-booker.herokuapp.com/booking";

 
    @Test
    @Description("Validate Creation of Booking Test")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Story 3 - Create Booking")
    public void createBookingTest() {
    	logger.info("Post API test execution started...");
        // Generate unique values
        String firstname = "Jim" + UUID.randomUUID().toString().substring(0, 5);
        String lastname = "Brown" + UUID.randomUUID().toString().substring(0, 5);
        int totalprice = new Random().nextInt(1000) + 1;

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
                .setHeader("Content-Type", "application/json");  // Set Content-Type Header
        
     // Build the RequestSpecification
        RequestSpecification requestSpec = requestBuilder.build();

        // Send POST request
        Response response = RestAssured.given()
        		.spec(requestSpec) 					   //Use the built request specification
        		.filter(new AllureRestAssured())       // Add Allure filter
                .filter(new RequestLoggingFilter())    // Log the request details
                .filter(new ResponseLoggingFilter())   // Log the response details
                .body(requestBody.toString())          // Send POST request
                .post();

        // Verify the response
        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP Response Code: 200");

        // Extract the response body
        JSONObject responseBody = new JSONObject(response.getBody().asString());

        // Assertions to verify the response
        Assert.assertNotNull(responseBody.getInt("bookingid"), "Booking ID should not be null");

        JSONObject booking = responseBody.getJSONObject("booking");
        Assert.assertEquals(booking.getString("firstname"), firstname, "Firstname mismatch");
        Assert.assertEquals(booking.getString("lastname"), lastname, "Lastname mismatch");
        Assert.assertEquals(booking.getInt("totalprice"), totalprice, "Total price mismatch");
        Assert.assertTrue(booking.getBoolean("depositpaid"), "Deposit paid mismatch");

        JSONObject responseBookingDates = booking.getJSONObject("bookingdates");
        Assert.assertEquals(responseBookingDates.getString("checkin"), "2018-01-01", "Checkin date mismatch");
        Assert.assertEquals(responseBookingDates.getString("checkout"), "2019-01-01", "Checkout date mismatch");

        Assert.assertEquals(booking.getString("additionalneeds"), "Breakfast", "Additional needs mismatch");
        
        logger.info("POST request successful, booking ID: {}", responseBody.getInt("bookingid"));
        
        // Print the entire response body
        System.out.println("Response Body: " + response.getBody().asString());
    }
	

}
