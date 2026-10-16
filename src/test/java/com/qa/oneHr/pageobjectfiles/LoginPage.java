package com.qa.oneHr.pageobjectfiles;

import java.awt.AWTException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.qa.oneHr.utilities.EncryptionUtils;
import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import com.qa.oneHr.utilities.UserType;

public class LoginPage {
	WebDriver driver;
	ReusableMethods reusableMethods;
	TestContextSetup testContext;
	String secretKey = "MySecretKey12345"; // 16 bytes

	public final By email=By.xpath("//input[@inputmode='email']");
	public final By password=By.xpath("//input[@autocomplete='current-password']");
	public final By login=By.xpath("//button[@type='submit' and normalize-space()='Sign in']");
	public final By setYourPasswordHeading=By.xpath("//*[contains(text(),'Set your password') or contains(text(),'Set Your Password')]");

	String decryptedPassword="";

	public LoginPage(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {
		this.driver=driver;
		this.reusableMethods=reusablemethods;
		this.testContext=testcontext;
	}



	public void login(UserType userType)
	{
		UserType.Credentials credentials = userType.getCredentials();

		reusableMethods.enterText(email, credentials.username());
		reusableMethods.enterText(password, credentials.password());
	}


	public void login(String username, String userPassword)
	{
		reusableMethods.waitForElementVisible(email);
		reusableMethods.enterText(email, username);
		reusableMethods.enterText(password, userPassword);
	}


	public boolean isSetYourPasswordScreenDisplayed()
	{
		reusableMethods.waitForElementVisible(setYourPasswordHeading, 15);
		return reusableMethods.isElementDisplayedOnScreen(setYourPasswordHeading);
	}

	 public void launchBrowser() throws InterruptedException
	 {
		 if(testContext.getDriver()==null)
		 {
			 testContext.testbase.openBrowser();
		 }

	 }

	 public void openURL() throws InterruptedException, AWTException
	 {
		 testContext.testbase.openURL();
	 }

	 public void clickOnLoginButton()
	 {
		 //reusableMethods.clickOnButton(login);

			 //reusableMethods.enterText(password, decryptedPassword);
			 reusableMethods.clickOnButton(login);
			 System.out.println("User has been navigated to One Hr Home page");
		 }
	 }

