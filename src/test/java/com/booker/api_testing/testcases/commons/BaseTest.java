package com.booker.api_testing.testcases.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public class BaseTest {
	private static final Logger logger = LogManager.getLogger(BaseTest.class);
	protected static final String BASE_URI = "https://restful-booker.herokuapp.com/booking";
	@BeforeClass
    public void setup() {
        logger.info("Setup completed. Base URI: {}", BASE_URI);
    }
	
	@AfterMethod
	public void afterMethod(ITestResult result) {
		if(result.getStatus() == ITestResult.FAILURE) {
			Throwable t = result.getThrowable();
			StringWriter error = new StringWriter();
			t.printStackTrace(new PrintWriter(error));
			logger.info(error.toString());
		}
	}	

}
