package com.booker.api_testing.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestBuilder {
	
	private static final Logger logger = LogManager.getLogger(RequestBuilder.class);

    private final RequestSpecification requestSpecification;

    public RequestBuilder() {
        this.requestSpecification = RestAssured.given();
        logger.info("Initialized RequestBuilder");
    }

    public RequestBuilder setBaseUri(String baseUri) {
        requestSpecification.baseUri(baseUri);
        logger.info("Set Base Uri: {}", baseUri);
        return this;
    }

    public RequestBuilder setHeader(String key, String value) {
        requestSpecification.header(key, value);
        logger.info("Set Header: {} = {}", key, value);
        return this;
    }

    public RequestBuilder setQueryParam(String key, String value) {
        requestSpecification.queryParam(key, value);
        logger.info("Set Query Param: {} = {}", key, value);
        return this;
    }

    public RequestBuilder setPathParam(String key, String value) {
        requestSpecification.pathParam(key, value);
        logger.info("Set Path Param: {} = {}", key, value);
        return this;
    }

    public RequestBuilder setBasicAuth(String username, String password) {
        requestSpecification.auth().basic(username, password);
        logger.info("Set Basic Auth for user: {}", username);
        return this;
    }

    public RequestBuilder setDigestAuth(String username, String password) {
        requestSpecification.auth().digest(username, password);
        logger.info("Set Digest Auth for user: {}", username);
        return this;
    }

    public RequestBuilder setApiKey(String key, String value) {
        requestSpecification.header(key, value);
        logger.info("Set API Key: {} = {}", key, value);
        return this;
    }

    public RequestBuilder setCookie(String name, String value) {
        requestSpecification.cookie(name, value);
        logger.info("Set Cookie: {} = {}", name, value);
        return this;
    }

    public RequestBuilder setOAuth2(String token) {
        requestSpecification.auth().oauth2(token);
        logger.info("Set OAuth2 token");
        return this;
    }

    public RequestSpecification build() {
        return requestSpecification;
    }

}
