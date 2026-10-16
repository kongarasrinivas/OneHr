package com.qa.oneHr.utilities;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ElementActions {

	public static void highlightElement(WebDriver driver, By element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(
				"arguments[0].setAttribute('style', 'border: 1px solid yellow !important; box-shadow: 0 0 5px yellow !important;');",
				element);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void click(WebDriver driver, By element, String stepName, String scenarioName)
			throws IOException {
		highlightElement(driver, element);
		captureScreenshot(driver, scenarioName, stepName);
		driver.findElement(element).click();
	}

	public static void sendKeys(WebDriver driver, By element, String text, String stepName, String scenarioName)
			throws IOException {
		highlightElement(driver, element);
		captureScreenshot(driver, scenarioName, stepName);
		driver.findElement(element).sendKeys(text);
	}

	public static String getText(WebDriver driver, By element, String stepName, String scenarioName)
			throws IOException {
		highlightElement(driver, element);
		captureScreenshot(driver, scenarioName, stepName);
		return driver.findElement(element).getText();
	}

	public static void captureScreenshot(WebDriver driver, String scenarioName, String stepName) throws IOException {
		String sanitizedStep = stepName.replaceAll("[^a-zA-Z0-9]", "_");
		String folderPath = System.getProperty("user.dir") + "/test-output/screenshots/" + scenarioName;
		new File(folderPath).mkdirs();

		int stepNumber = StepCounterUtil.getAndIncrementStepCounter();
		String timestamp = StepCounterUtil.getCurrentTimestamp();

		String fileName = String.format("%02d_%s_%s.png", stepNumber, sanitizedStep, timestamp);

		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(src, new File(folderPath + "/" + fileName));
	}
}
