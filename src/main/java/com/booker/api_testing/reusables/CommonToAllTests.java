package com.booker.api_testing.reusables;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.booker.api_testing.consonants.FileNameConstants;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CommonToAllTests {

	public static String tokenGeneration() throws IOException {
		String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),
				"UTF-8");

		// token generation
		Response tokenAPIResponse = RestAssured.given().filter(new AllureRestAssured())
				.filter(new RequestLoggingFilter()).filter(new ResponseLoggingFilter()).contentType(ContentType.JSON)
				.body(tokenAPIRequestBody).baseUri("https://restful-booker.herokuapp.com/auth").when().post().then()
				.assertThat().statusCode(200).extract().response();
		String token = tokenAPIResponse.jsonPath().getString("token");
		System.out.println("Token Id : " + token);
		return token;
	}

}
