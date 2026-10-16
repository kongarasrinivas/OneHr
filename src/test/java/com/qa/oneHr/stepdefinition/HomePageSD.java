package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.utilities.TestContextSetup;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class HomePageSD {
	TestContextSetup testcontext;

	public HomePageSD(TestContextSetup testcontext) {
		this.testcontext = testcontext;
	}

	@And("clicks on logout icon")
	public void clicks_on_logout_icon() throws InterruptedException {
		testcontext.getPageObjectManager().getHomePage().logoutAction();
	}

	@Then("the user should be land to {string} dashboard page")
    public void verifyTitle(String expTitle) throws InterruptedException {
		String actualTitle = testcontext.getPageObjectManager().getHomePage().verifyTitle(expTitle);
		testcontext.softAssert.assertEquals(actualTitle,expTitle,"Title missmatch: Actual is "+actualTitle +"Expectedtittle is: "+expTitle);
	}

	@And("User click on people")
	public void clickPeople() throws InterruptedException {
testcontext.getPageObjectManager().getHomePage().clickPeople();
	}

	@And("User click on organization")
	public void clickOrg() throws InterruptedException {
		testcontext.getPageObjectManager().getHomePage().clickOrgnization();
	}

	@And("User click on oganization hierarchy")
	public void clickOrgHierarchy() throws InterruptedException {
		testcontext.getPageObjectManager().getHomePage().clickOrgHierarchy();
	}

}
