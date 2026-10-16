package com.qa.oneHr.testrunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/resources/FeatureFiles",

		glue = {
		"com.qa.oneHr.stepdefinition","com.qa.oneHr.hooks"},
		tags="@Sanity",
		dryRun = false,
		monochrome = true,
		plugin = { "pretty",
		        "html:target/cucumber-reports.html",  
		        "json:target/cucumber.json",
		        "com.qa.oneHr.utilities.StepListener",
		        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
})

public class TestRunner extends AbstractTestNGCucumberTests {

//	///Parallel Run
	/*
	 * @Override
	 *
	 * @DataProvider(parallel=true) public Object[][] scenarios() { return
	 * super.scenarios();
	 *
	 * }
	 */

}
