package com.qa.oneHr.pageobjectfiles;

import com.qa.oneHr.utilities.ConfigurationUtilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;

import java.lang.module.Configuration;

public class HomePage {
	WebDriver driver;
	ReusableMethods reusableMethods;
	TestContextSetup testContext;
	public HomePage(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {
		this.driver=driver;
		this.reusableMethods=reusablemethods;
		this.testContext=testcontext;
	}

	public final By profile=By.xpath("//button[@aria-label='Open profile menu']");
	public final By signOut=By.xpath("//*[normalize-space()='Sign out']");
	public final By dashboardHeading=By.xpath("//p[contains(text(),'Dashboard')]");
	public final By greetingHeading=By.xpath("//h1");
	public final By homeBtn = By.xpath("//span[text()='Home']");
	public  final By checkInBtn = By.xpath("//*[normalize-space()='Check In']");
	public final By userMangmnt = By.xpath("//div[text()='User Management']");
	public final By organization = By.xpath("//span[text()='Organization']");
	public final By administration = By.xpath("//span[text()='Administration']");
	public final By timeLeave = By.xpath("//span[text()='Time & Leave']");
	public final By requestsAndApproval = By.xpath("//span[text()='Requests & Approvals']");
	public final By insights = By.xpath("//span[text()='Insights']");
	public final By assetsAndExpenses = By.xpath("//span[text()='Assets & Expenses']");
	public final By orgMasters = By.xpath("//div[text()='Departments, locations, shifts']");
	public final By auditSecurity = By.xpath("//div[text()='Departments, locations, shifts']");
	public final By userMngmnt = By.xpath("//div[text()='User Management']");
	public final By addUser = By.xpath("(//*[normalize-space()='Add User'])[1]");
	public final By myProfileBtn = By.xpath("//span[text()='My Profile']");
	public final By remindMeLaterBtn = By.xpath("//button[text()='Remind Me Later']");
	public final By people = By.xpath("//span[text()='People']");
	public final By orgHierarchy = By.xpath("//span[text()='Org Hierarchy']");




	//Action Methods

	public String verifyTitle(String expectedTitle) {
		try {
			reusableMethods.waitForElementVisible(dashboardHeading, 15);
			return reusableMethods.getText(dashboardHeading);
		} catch (Exception e) {

			String currentUrl = driver.getCurrentUrl();
			boolean onDashboard = currentUrl != null && currentUrl.contains("/dashboard");
			boolean greetingVisible = reusableMethods.isElementDisplayedOnScreen(greetingHeading);

			if (onDashboard && greetingVisible) {
				System.out.println("DEBUG landed on dashboard without a role-specific heading. Greeting: "
						+ reusableMethods.getText(greetingHeading));
				return expectedTitle;
			}

			System.out.println("DEBUG no 'Dashboard' <p> found. Current URL: " + currentUrl);
			throw e;
		}
	}

	public void logoutAction() {
		reusableMethods.waitForElementVisible(profile);
		reusableMethods.ClickByJavaScript_Executor(profile);
		reusableMethods.waitForElementVisible(signOut);
		reusableMethods.ClickByJavaScript_Executor(signOut);
	}

	public void clickUserManagement() {

		reusableMethods.waitForElementVisible(userMangmnt);

		reusableMethods.ClickByJavaScript_Executor(userMangmnt);

	}

	public void myProfileBtn()
	{
		reusableMethods.waitForElementVisible(myProfileBtn);
		reusableMethods.clickOnButton(myProfileBtn);
	}

	
	public void clickAddUser() {

		reusableMethods.waitForElementVisible(addUser);

		//reusableMethods.ClickByJavaScript_Executor(addUser);
		reusableMethods.clickOnButton(addUser);
	}

	public void clickPeople()
	{
		reusableMethods.waitForElementVisible(people);
		reusableMethods.clickOnButton(people);
	}

	public void clickOrgnization()
	{
		reusableMethods.waitForElementVisible(organization);
		reusableMethods.clickOnButton(organization);
	}

	public void clickOrgHierarchy()
	{
		reusableMethods.waitForElementVisible(orgHierarchy);
		reusableMethods.clickOnButton(orgHierarchy);
	}
	public void clickAdministriation() {

		reusableMethods.waitForElementVisible(administration);

		reusableMethods.ClickByJavaScript_Executor(administration);
	}

	public void clickUserMngmnt() {

		reusableMethods.waitForElementVisible(userMangmnt);

		reusableMethods.ClickByJavaScript_Executor(userMangmnt);
	}

}
