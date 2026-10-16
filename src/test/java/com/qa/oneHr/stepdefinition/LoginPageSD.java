package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.utilities.TestContextSetup;
import com.qa.oneHr.utilities.UserType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginPageSD {
	TestContextSetup testcontext;
	public LoginPageSD(TestContextSetup testcontext) {
		this.testcontext=testcontext;
	}

	@When("the {string} user enters valid credentials")
	public void the_user_enters_valid_credentials(String userTypeLabel) throws InterruptedException, Exception {
		UserType userType = UserType.fromLabel(userTypeLabel);
		testcontext.getPageObjectManager().getLoginPage().login(userType);
	}

	@And("clicks on the login button")
	public void clicks_on_the_login_button() throws InterruptedException {
		testcontext.getPageObjectManager().getLoginPage().clickOnLoginButton();
	}

	@When("the user logs in with the newly created employee's email and temporary password")
	public void the_user_logs_in_with_temp_password() throws InterruptedException {
		testcontext.getPageObjectManager().getLoginPage()
				.login(testcontext.lastGeneratedEmail, testcontext.lastGeneratedTempPassword);
	}

	@Then("the set your password screen should be displayed")
	public void the_set_your_password_screen_should_be_displayed() throws InterruptedException {
		boolean displayed = testcontext.getPageObjectManager().getLoginPage().isSetYourPasswordScreenDisplayed();
		testcontext.softAssert.assertTrue(displayed,
				"'Set your password' screen was not displayed after logging in with the temporary password");
	}



}
