package com.qa.oneHr.pageobjectfiles;

import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmpProfile {

    WebDriver driver;
    ReusableMethods reusableMethods;
    TestContextSetup testContext;
    public EmpProfile(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {

        this.driver=driver;
        this.reusableMethods=reusablemethods;
        this.testContext=testcontext;
    }

    public final By aboutTab = By.xpath("//button[text()='About']");
    public final By profileTab = By.xpath("//button[text()='Profile']");
    public final By documentsTab = By.xpath("//button[text()='Documents']");
    public final By jobTab = By.xpath("//button[text()='Job']");
    public final By assetsTab = By.xpath("//button[text()='Assets']");
    public final By primaryDetailsSec = By.xpath("//h2[text()='Primary Details']");
    public final By firstName = By.xpath("//div[text()='First Name']");
    public final By middleName = By.xpath("//div[text()='Middle Name']");

    public final By lastName = By.xpath("//div[text()='Last Name']");
    public final By dateofBirth = By.xpath("//div[text()='Date of Birth']");
    public final By gender = By.xpath("//div[text()='Gender']");



    public final By maritalStatus = By.xpath("//div[text()='Marital Status']");
    public final By emergncyContactName = By.xpath("//div[text()='Emergency Contact Name']");
    public final By emrgncyCntctRltnShip = By.xpath("//div[text()='Emergency Contact Relationship']");


    public final By  emrgncyCntctPhone = By.xpath("//div[text()='Emergency Contact Phone']");
    public final By emrgncyContactName = By.xpath("//div[text()='Emergency Contact Name']");
    public final By contactDetailsSection = By.xpath("//h2[text()='Contact Details']");

    public final By  workEmail= By.xpath("//div[text()='Work Email']");
    public final By personalEmail = By.xpath("//div[text()='Personal Email']");
    public final By mobileNumber = By.xpath("//div[text()='Mobile Number']");

    public final By  addressesSection = By.xpath("//h2[text()='Addresses']");
    public final By currentAddress = By.xpath("//div[text()='Current Address']");
    public final By permanentAddress = By.xpath("//div[text()='Permanent Address']");

    public final By  educationSctn = By.xpath("//h2[text()='Education']");
    public final By identityStatutorySctn = By.xpath("//h2[text()='Identity & Statutory']");
    public final By nationalID = By.xpath("//div[text()='National ID']");

    public final By  passportNum = By.xpath("//div[text()='Passport Number']");
    public final By passportExpiry = By.xpath("//div[text()='Passport Expiry']");
    public final By bankAccNum = By.xpath("//div[text()='Bank Account Number']");

    public final By BankNameIFSC = By.xpath("//div[text()='Bank Name & IFSC']");

    public boolean verifyAboutTab()
    {
        reusableMethods.waitForElementVisible(aboutTab);

return reusableMethods.isElementDisplayed(aboutTab);

    }
}
