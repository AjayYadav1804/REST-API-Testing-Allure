# REST API Testing Framework with REST Assured and Allure

## Overview
This project is an automated testing framework for RESTful APIs, specifically focused on booking-related endpoints. It utilizes REST Assured for API testing and Allure for reporting, providing a robust solution for validating API functionality, performance, and reliability.

## Tech Stack
- **Java**: Core programming language
- **REST Assured**: Library for testing and validating REST services
- **TestNG**: Testing framework for test organization and execution
- **Allure**: Framework for generating interactive and comprehensive test reports
- **Maven**: Dependency management and build automation
- **Excel (Apache POI)**: Data-driven testing support

## What's Tested
This framework tests a booking API with the following operations:
1. **GET** operations to retrieve booking information
2. **POST** operations to create new bookings
3. **PUT** operations to update existing bookings
4. **DELETE** operations to remove bookings
5. **POST with logging** to validate request/response logging functionality

### API Operations
1. **GET Requests**
   - Basic GET operations
   - Authentication handling
   - Cookie-based requests
   - Parameter validation

2. **POST Requests**
   - Using POJO classes
   - Using JSON files
   - Using JSON Objects
   - Dynamic payload generation
   - UUID-based requests

3. **PUT/PATCH Requests**
   - Update operations
   - Partial updates

4. **File Operations**
   - File upload functionality
   - File handling

5. **End-to-End Flows**
   - Complete API workflows
   - Chained API requests

### Data-Driven Testing
- CSV file-based testing
- Excel file-based testing
- JSON file-based testing
- Dynamic data generation

## Project Structure

```
REST-API-Testing-Allure/
├── src/
│   ├── main/java/
│   │   ├── com.booker.api_testing.consonants/
│   │   │   └── FileNameConstants.java           # Constants for file paths and names
│   │   ├── com.booker.api_testing.reusables/
│   │   │   └── CommonToAllTests.java            # Common functionality shared across tests
│   │   └── com.booker.api_testing.utils/
│   │       └── ExcelUtils.java                  # Utilities for Excel operations
│   ├── test/java/
│   │   ├── com.booker.api_testing.api/
│   │   │   └── RequestBuilder.java              # Builder for API requests
│   │   └── com.booker.api_testing.testcases/
│   │       ├── AllureReportGenerationAllTests.java  # Allure report configuration
│   │       ├── BookingDELETEApiTest.java        # Tests for DELETE operations
│   │       ├── BookingGETApiTest.java           # Tests for GET operations
│   │       ├── BookingPOSTApiTest.java          # Tests for POST operations
│   │       ├── BookingPOSTApiTestlog.java       # Tests for POST with logging
│   │       ├── BookingPUTApiTest.java           # Tests for PUT operations
│   │       └── commons/
│   │           └── BaseTest.java                # Base test class for all test cases
│   └── resources/
│       ├── Apitestdata.xlsx                     # Test data in Excel format
│       ├── get_response.json                    # Sample GET response
│       └── tokenapirequestbody.txt              # Token request payload
├── allure-report/                               # Generated Allure reports directory
├── allure-results/                              # Allure test results
├── test-output/                                 # TestNG output
│   ├── Allurereport2EAPlests.xml                # Allure report configuration
│   ├── pom.xml                                  # Maven configuration
│   ├── testngDelete.xml                         # TestNG configuration for DELETE tests
│   ├── testngGet.xml                            # TestNG configuration for GET tests
│   ├── testngPost.xml                           # TestNG configuration for POST tests
│   └── testngPut.xml                            # TestNG configuration for PUT tests
└── README.md                                    # Project documentation
```

## Framework Components Explained

### 1. Core Components

#### FileNameConstants.java
Contains constants for file paths and names used throughout the project, ensuring consistency and easy maintenance.

#### CommonToAllTests.java
Provides common functionality that's shared across all tests, such as setup and teardown operations, common assertions, and utility methods.

#### ExcelUtils.java
Utilities for reading test data from Excel files, enabling data-driven testing approaches.

### 2. API Interaction

#### RequestBuilder.java
Builder pattern implementation for constructing API requests with various parameters, headers, and payloads.

### 3. Test Cases

#### BaseTest.java
Base class that all test classes extend, providing common setup, configuration, and utility methods.

#### BookingGETApiTest.java
Tests for validating GET operations on the booking API, ensuring proper retrieval of booking information.

#### BookingPOSTApiTest.java
Tests for validating POST operations, verifying that new bookings can be created successfully.

#### BookingPUTApiTest.java
Tests for validating PUT operations, ensuring that existing bookings can be updated correctly.

#### BookingDELETEApiTest.java
Tests for validating DELETE operations, confirming that bookings can be removed successfully.

#### BookingPOSTApiTestlog.java
Specialized tests for POST operations with logging enabled, to verify request/response logging functionality.

### 4. Reporting

#### AllureReportGenerationAllTests.java
Configuration for Allure reporting, ensuring that test results are properly formatted and displayed.

### 5. Resources

#### Apitestdata.xlsx
Excel file containing test data for various test scenarios.

#### get_response.json
Sample GET response for reference or validation.

#### tokenapirequestbody.txt
Template for token request payloads.

## How to Run the Project

### Prerequisites
- Java JDK 8 or higher
- Maven 3.6 or higher
- Git (for cloning the repository)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone [repository-url]
   cd REST-API-Testing-Allure
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run tests**

   To run all tests:
   ```bash
   mvn test
   ```

   To run specific test suites:
   ```bash
   # For GET tests
   mvn test -DsuiteXmlFile=testngGet.xml

   # For POST tests
   mvn test -DsuiteXmlFile=testngPost.xml

   # For PUT tests
   mvn test -DsuiteXmlFile=testngPut.xml

   # For DELETE tests
   mvn test -DsuiteXmlFile=testngDelete.xml
   
   ### Running Data-Driven Tests
```bash
mvn test -DsuiteXmlFile=suites/Datadriventestingusingcsv.xml
mvn test -DsuiteXmlFile=suites/Datadriventestingusingexcel.xml
mvn test -DsuiteXmlFile=suites/Datadriventestingusingjson.xml
   ```

4. **Generate Allure reports**
   ```bash
   # First run the tests to generate result files
   mvn test

   # Then generate the report
   mvn allure:report
   ```

5. **View Allure reports**
   ```bash
   mvn allure:serve
   ```
   This will automatically open the report in your default browser.
   
   **Test Execution Report:**
![Image](https://github.com/user-attachments/assets/a318de28-c341-4142-b36b-12ed3ec16f9e)
![Image](https://github.com/user-attachments/assets/c3887ddd-d4a7-4178-bd7f-acd8184940ee)

## Best Practices Implemented

1. **Data-Driven Testing**: Using Excel files to manage test data
2. **Modular Design**: Separation of concerns with utilities, constants, and request builders
3. **Reusable Components**: Common functionality extracted into reusable classes
4. **Comprehensive Reporting**: Detailed test reports with Allure
5. **Maintainable Structure**: Organized package structure for easy navigation
6. **Consistent Naming**: Clear and consistent naming conventions throughout the codebase

