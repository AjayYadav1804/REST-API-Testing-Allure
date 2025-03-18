package com.booker.api_testing.testcases;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.booker.api_testing.api.RequestBuilder;

import org.json.JSONObject;

import java.util.UUID;
import java.util.Random;

public class BookingPOSTApiTestlog {
	
	   private static final Logger logger = LogManager.getLogger(BookingPOSTApiTestlog.class);
	   private static final String BASE_URI = "https://restful-booker.herokuapp.com/booking";
	   String firstname;
	   String lastname;
	   int totalprice;

	    @BeforeClass
	    public void setup() {
	        logger.info("Setup completed. Base URI: {}", BASE_URI);
	    }
    
	    @Test
	    public void createBookingTest() {
	        String logDetails = "";
	        try {
	            logDetails += createBooking().toString();
	            validateResponse();
	        } finally {
	            attachLog(logDetails);
	        }
	    }
	    
	    @Step("Create a new booking")
	    public Response createBooking() {
	        // Generate unique values
	        firstname = "Jim" + UUID.randomUUID().toString().substring(0, 5);
	        lastname = "Brown" + UUID.randomUUID().toString().substring(0, 5);
	        totalprice = new Random().nextInt(1000) + 1;

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

	        logger.info("Request Body: {}", requestBody.toString());

	        // Create request
	        RequestBuilder requestBuilder = new RequestBuilder()
	                .setBaseUri(BASE_URI)
	                .setHeader("Accept", "application/json")
	                .setHeader("Content-Type", "application/json");

	        // Send POST request
	        Response response = requestBuilder.build()
	                .body(requestBody.toString())
	                .post();

	        logger.info("Response Status Code: {}", response.getStatusCode());
	        logger.info("Response Body: {}", response.getBody().asString());

	        return response;
	    }

	    @Step("Validate booking response")
	    public void validateResponse() {
	        Response response = createBooking();

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
	        String responseBodyp = response.getBody().asString();
	        System.out.println("Response Body:");
	        System.out.println(responseBodyp);
	    }

	    @Attachment(value = "Logs", type = "text/plain")
	    public String attachLog(String logContent) {
	        return logContent;
	    }

}
