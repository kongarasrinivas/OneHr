package com.qa.oneHr.pageobjectfiles;
import org.openqa.selenium.WebDriver;
import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;


public class POManager {
	
	public WebDriver driver;
	public ReusableMethods reusableMethods;
	public LoginPage loginpage;
	public HomePage homepage;
	public AddUser addUser;
	public UserManagement userManagement;
	public EmpProfile empProfile;
	public TestContextSetup testcontext;
	private OrganizationHierarchy OrganizationHierarchy;


	public POManager(WebDriver driver, ReusableMethods reusableMethods, TestContextSetup testcontext) {
		this.driver = driver;
		this.reusableMethods=reusableMethods;
		this.testcontext=testcontext;
	}

	public LoginPage getLoginPage()
	 {
	  if(loginpage ==null)
	  {
	   loginpage=new LoginPage(driver, reusableMethods,testcontext);
	  }
	  return loginpage;
	 }

	 public OrganizationHierarchy getOrganizationHierarchy() {
	  if (OrganizationHierarchy == null)
		  OrganizationHierarchy = new OrganizationHierarchy(driver, reusableMethods,testcontext);
	  return OrganizationHierarchy;
	 }

	public HomePage getHomePage() {
		if (homepage == null)
			homepage = new HomePage(driver, reusableMethods,testcontext);
		return homepage;
	}

	public AddUser getAddUser() {
		if (addUser == null)
			addUser = new AddUser(driver, reusableMethods,testcontext);
		return addUser;
	}

	public UserManagement getUserManagement() {
		if (userManagement == null)
			userManagement = new UserManagement(driver, reusableMethods, testcontext);
		return userManagement;
	}

	public EmpProfile getEmpProfile() {
		if (empProfile == null)
			empProfile = new EmpProfile(driver, reusableMethods, testcontext);
		return empProfile;
	}

}
