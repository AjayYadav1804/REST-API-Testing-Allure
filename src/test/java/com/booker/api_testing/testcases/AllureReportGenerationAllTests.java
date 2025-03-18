package com.booker.api_testing.testcases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.booker.api_testing.api.RequestBuilder;
import com.booker.api_testing.consonants.FileNameConstants;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Epic("Epic-01")
@Feature("Create Get Update Delete Booking")
public class AllureReportGenerationAllTests {

	private static final Logger logger = LogManager.getLogger(BookingDELETEApiTest.class);
	private Response response;
	private static final String BASE_URI = "https://restful-booker.herokuapp.com/booking";
	String tokenAPIRequestBody;
	String firstname;
	String lastname;
	int totalprice;

	@Test(description = "end to end api testing", priority = 1)
	@Story("Story EP1")
	@Description("end to end testing")
	@Severity(SeverityLevel.CRITICAL)
	public void e2eAPIRequest() throws IOException {

		logger.info("e2eAPIRequest test execution started...");
		tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY), "UTF-8");
		logger.info("e2eAPIRequest test execution ended...");
		// POST API call
		int bookingId = createBookingTest();
		System.out.println("Booking Id : " + bookingId);

		// Get API Call
		getBookingDetailsSuccessTest(bookingId);

		// PUT API Call
		updateBookingTest(bookingId);

		// Delete API Call
		deleteBookingAPITest(bookingId);

	}

	@Step("POST:: create Booking Test")
	public int createBookingTest() {
		logger.info("Post API test execution started...");
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

		// Create request
		RequestBuilder requestBuilder = new RequestBuilder().setBaseUri(BASE_URI) // Set Base URI
				.setHeader("Accept", "application/json") // Set Accept Header
				.setHeader("Content-Type", "application/json"); // Set Content-Type Header

		// Build the RequestSpecification
		RequestSpecification requestSpec = requestBuilder.build();

		// Send POST request
		Response response = RestAssured.given().spec(requestSpec) // Use the built request specification
				.filter(new AllureRestAssured()) // Add Allure filter
				.filter(new RequestLoggingFilter()) // Log the request details
				.filter(new ResponseLoggingFilter()) // Log the response details
				.body(requestBody.toString()) // Send POST request
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

		String bookingIdstr = response.jsonPath().getString("bookingid");
		int bookingId = Integer.parseInt(bookingIdstr);
		System.out.println("Booking Id : " + bookingId);

		return bookingId;
	}

	@Step("GET:: Get Booking details")
	public void getBookingDetailsSuccessTest(int endpoint) throws FileNotFoundException {
		// String bookingId = "1748"; // Valid booking ID
		logger.info("Testing valid booking ID: {}", endpoint);

		RequestBuilder requestBuilder = new RequestBuilder().setBaseUri(BASE_URI).setHeader("Accept",
				"application/json");

		// Build the RequestSpecification
		RequestSpecification requestSpec = requestBuilder.build();

		response = RestAssured.given().spec(requestSpec) // Use the built request specification
				.filter(new AllureRestAssured()) // Add Allure filter
				.filter(new RequestLoggingFilter()) // Log the request details
				.filter(new ResponseLoggingFilter()) // Log the response details
				.get("/" + endpoint); // Get method

		String filePath = "src/test/java/resources/get_response.json";
		File schema = new File(filePath);
		response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));

		Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP Response Code: 200");

		// Positive assertions (actual values may vary, adjust accordingly)
		Assert.assertEquals(response.jsonPath().getString("firstname"), firstname, "Firstname mismatch");
		Assert.assertEquals(response.jsonPath().getString("lastname"), lastname, "Lastname mismatch");
		Assert.assertEquals((int) response.jsonPath().getInt("totalprice"), totalprice, "Total price mismatch");
		Assert.assertTrue(response.jsonPath().getBoolean("depositpaid"), "Deposit paid mismatch");

		String checkinDate = response.jsonPath().getString("bookingdates.checkin");
		String checkoutDate = response.jsonPath().getString("bookingdates.checkout");
		Assert.assertEquals(checkinDate, "2018-01-01", "Checkin date mismatch");
		Assert.assertEquals(checkoutDate, "2019-01-01", "Checkout date mismatch");

		Assert.assertEquals(response.jsonPath().getString("additionalneeds"), "Breakfast", "Additional needs mismatch");
		logger.info("GET API test passed for booking ID: {}", endpoint);
	}

	@Step("PUT:: Update Booking details")
	public void updateBookingTest(int endpoint) throws IOException {
		logger.info("Put API test execution started...");
		tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY), "UTF-8");
		// Generate unique values
		firstname = "Jim" + UUID.randomUUID().toString().substring(0, 5);
		lastname = "Brown" + UUID.randomUUID().toString().substring(0, 5);
		totalprice = new Random().nextInt(1000) + 1;

		String token = tokenGeneration();

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
		RequestBuilder requestBuilder = new RequestBuilder().setBaseUri(BASE_URI) // Set Base URI
				.setHeader("Accept", "application/json") // Set Accept Header
				.setHeader("Content-Type", "application/json") // Set Content-Type Header
				.setHeader("Cookie", "token=" + token);

		// Build the RequestSpecification
		RequestSpecification requestSpec = requestBuilder.build();

		// Send POST request
		Response response = RestAssured.given().spec(requestSpec) // Use the built request specification
				.filter(new AllureRestAssured()) // Add Allure filter
				.filter(new RequestLoggingFilter()) // Log the request details
				.filter(new ResponseLoggingFilter()) // Log the response details
				.body(requestBody.toString()) // Send POST request
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

	@Step("DELETE:: Delete the booking")
	public void deleteBookingAPITest(int endpoint) throws IOException {
		logger.info("Delete API test execution started...");
		tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY), "UTF-8");
		String token = tokenGeneration();
		RequestBuilder requestBuilder = new RequestBuilder().setBaseUri(BASE_URI) // Set Base URI
				.setHeader("Accept", "application/json") // Set Accept Header
				.setHeader("Content-Type", "application/json") // Set Content-Type Header
				.setHeader("Cookie", "token=" + token);

		// Build the RequestSpecification
		RequestSpecification requestSpec = requestBuilder.build();

		response = RestAssured.given().spec(requestSpec) // Use the built request specification
				.filter(new AllureRestAssured()) // Add Allure filter
				.filter(new RequestLoggingFilter()) // Log the request details
				.filter(new ResponseLoggingFilter()) // Log the response details
				.delete("/" + endpoint); // Delete request
		Assert.assertEquals(response.getStatusCode(), 201, "Expected HTTP Response Code: 201");
		logger.info("Delete API test execution Completed...");
	}

	@Step("GET:: Get Token")
	public String tokenGeneration() {
		// token generation
		Response tokenAPIResponse = RestAssured.given().filter(new AllureRestAssured())
				.filter(new RequestLoggingFilter()).filter(new ResponseLoggingFilter()).contentType(ContentType.JSON)
				.body(tokenAPIRequestBody).baseUri("https://restful-booker.herokuapp.com/auth").when().post().then()
				.assertThat().statusCode(200).extract().response();
		String token1 = tokenAPIResponse.jsonPath().getString("token");
		System.out.println("Token Id : " + token1);
		return token1;
	}
}
