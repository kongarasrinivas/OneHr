package com.qa.oneHr.testrunner;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(

		glue = "technology.stepdefinition", // Replace with actual path to your step definitions
		features = "src/test/resources/features"

)

public class FailedRunner {
}
