package com.qa.oneHr.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


import com.aventstack.extentreports.Status;
import com.qa.oneHr.hooks.Hooks;
import com.qa.oneHr.testbase.TestBase;
import com.qa.oneHr.utilities.EnhancedPdfReportManager;
import com.qa.oneHr.utilities.OneHrHtmlReportGenerator;

public class TestNGListeners extends TestBase implements ITestListener{
	@Override
	public void onStart(ITestContext context) {
		initializeExtentReports();
		System.out.println("Test execution started: " + context.getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String scenarioName = Hooks.getScenarioName();
		TestPass("✅ Test passed successfully: "  + scenarioName);
		System.out.println("✅ Test passed: " + scenarioName);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String scenarioName = Hooks.getScenarioName();
		TestFail("❌ Test failed: " + scenarioName);
		System.out.println(" ❌ Test failed: " + scenarioName);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String scenarioName = Hooks.getScenarioName();
		getTest().log(Status.SKIP, "Test Skipped: " + scenarioName);
		TestInfo("⏩ Test skipped: " + scenarioName);
		System.out.println("⏩ Test skipped: " + scenarioName);
	}

	@Override
	public void onFinish(ITestContext context) {
		finalizeReport(); // sets suiteEndTime, flushes the Extent HTML reports
		OneHrHtmlReportGenerator.generate(dt, executionId, suiteStartTime, suiteEndTime, chromeBrowserVersion);
		EnhancedPdfReportManager.finalizePdfReport(dt);
		System.out.println("Test execution finished: " + context.getName());
	}
}
