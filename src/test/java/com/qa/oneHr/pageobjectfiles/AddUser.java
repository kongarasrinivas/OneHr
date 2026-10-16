package com.qa.oneHr.pageobjectfiles;

import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class AddUser {


        WebDriver driver;
        ReusableMethods reusableMethods;
        TestContextSetup testContext;
        public AddUser(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {

            this.driver=driver;
            this.reusableMethods=reusablemethods;
            this.testContext=testcontext;
        }
    public final By fullName = By.xpath("//input[@placeholder='Jane Smith']");
    public final By companyEmail = By.xpath("//input[@placeholder='jane@nforceone.com']");
    public final By roleDrpdwn = By.xpath("//*[normalize-space()='Role *']/following-sibling::select");
    public final By createUserButton = By.xpath("//button[normalize-space()='Create User']");
    public final By employementDrpdwn = By.xpath("//*[normalize-space()='Employment Type']/following-sibling::select");
    public final By businessUnitDrpdwn = By.xpath("//*[normalize-space()='Business Unit']/following-sibling::select");
    public final By departmentDrpdwn = By.xpath("//*[normalize-space()='Department *']/following-sibling::select");
    public final By designationDrpdwn = By.xpath("//*[normalize-space()='Designation *']/following-sibling::select");
    public final By location = By.xpath("//input[@placeholder='Select a location…']");
    public final By reportingManagerDrpDwn = By.xpath("//*[normalize-space()='Reporting Manager *']/following-sibling::select");
    public final By shift = By.xpath("//input[@placeholder='Select a shift…']");
    public final By joiningDate = By.xpath("//input[@type='date']");
    public final By userCreated = By.xpath("//div[text()='User Created']");
    public final By doneButton = By.xpath("//button[normalize-space()='Done']");
    public final By defaultshift = By.xpath("//div[text()='Default Shift — 3:30 PM–12:30 AM']");
    public final By workModeDrpDwn = By.xpath("//*[normalize-space()='Work Mode']/following-sibling::select");
//div[contains(text(),'Temp password — share once')]/following-sibling::code to get code
public final By tempasswordText = By.xpath("//div[contains(text(),'Temp password — share once')]/following-sibling::code");




    public void enterFullName(String name) {

        reusableMethods.waitForElementVisible(fullName);
        reusableMethods.enterText(fullName, name);
    }


    public void enterCompanyEmail(String email) {

        reusableMethods.waitForElementVisible(companyEmail);
        reusableMethods.enterText(companyEmail, email);
    }



    public void selectRole(String role) {

        reusableMethods.waitForElementVisible(roleDrpdwn);
        reusableMethods.selectTextOnDropDown(roleDrpdwn, role);
    }

public void verifyTempPwdDisplayed()
{
    reusableMethods.waitForElementVisible(tempasswordText);
}

public String getTempPwd()
{
    reusableMethods.waitForElementVisible(tempasswordText);
    String tempPwd = reusableMethods.getText(tempasswordText);
    return tempPwd;
}

public String getManager()
{
    reusableMethods.waitForElementVisible(reportingManagerDrpDwn);
    Select selc = new Select(driver.findElement( reportingManagerDrpDwn));
    return selc.getFirstSelectedOption().getText().trim();
}

public String getRole()
{
    reusableMethods.waitForElementVisible(roleDrpdwn);
    Select selc = new Select(driver.findElement(roleDrpdwn));
    return selc.getFirstSelectedOption().getText().trim();
}
    public void enterJoiningDate(String date) {

        reusableMethods.waitForElementVisible(joiningDate);
        reusableMethods.enterText(joiningDate,date);

    }


    public void selectEmploymentType(String employmentType) {

        reusableMethods.waitForElementVisible(employementDrpdwn);
        reusableMethods.selectTextOnDropDown(
                employementDrpdwn,
                employmentType
        );
    }

    public void selectBusinessUnit(String businessUnit) {

        reusableMethods.waitForElementVisible(businessUnitDrpdwn);
        reusableMethods.selectTextOnDropDown(businessUnitDrpdwn, businessUnit);
    }

    public void selectWorkMode(String workModeType)
    {
        reusableMethods.waitForElementVisible(workModeDrpDwn);
        reusableMethods.selectTextOnDropDown(workModeDrpDwn,workModeType);
    }


    public void selectDepartment(String department) {

        reusableMethods.waitForElementVisible(departmentDrpdwn);
        reusableMethods.selectTextOnDropDown(departmentDrpdwn, department);
    }



    public void selectDesignation(String designation) {

        reusableMethods.waitForElementVisible(designationDrpdwn);
        reusableMethods.selectTextOnDropDown(designationDrpdwn, designation);
    }


    public void verifyDesignation(String expectedDesignation) {

        reusableMethods.waitForElementVisible(designationDrpdwn);
        reusableMethods.validateSelectedDropdownText(designationDrpdwn, expectedDesignation);
    }

    public void selectReportingMngr(String reportingManager)
    {
        reusableMethods.waitForElementVisible(reportingManagerDrpDwn);
        reusableMethods.selectTextOnDropDown(reportingManagerDrpDwn,reportingManager);
    }


    public void selectLocation(String locationName) {

        reusableMethods.waitForElementVisible(location);
        reusableMethods.enterText(location, locationName);

        By suggestion = By.xpath("//div[normalize-space()='" + locationName + "']");
        reusableMethods.waitForElementVisible(suggestion);
        reusableMethods.clickOnButton(suggestion);

        String selectedValue = reusableMethods.getInputFieldValue(location);
        System.out.println("Location field value after selection: '" + selectedValue + "'");
    }



    public void selectReportingManager(String managerName) {

        reusableMethods.waitForElementVisible(reportingManagerDrpDwn);

        reusableMethods.selectTextOnDropDown(reportingManagerDrpDwn,managerName);
    }




    public void clickShift() {

        reusableMethods.waitForElementVisible(shift);
        reusableMethods.clickOnButton(shift);
    }


    public void clickDefaultShift() {

        reusableMethods.waitForElementVisible(defaultshift);
        reusableMethods.clickOnButton(defaultshift);
    }


    public void clickCreateUser() {

        reusableMethods.waitForElementVisible(createUserButton);
        reusableMethods.clickOnButton(createUserButton);
    }


    public void waitForUserCreatedConfirmation() {

        reusableMethods.waitForElementVisible(userCreated);
    }


    public void clickDone() {

        reusableMethods.waitForElementVisible(doneButton);
        reusableMethods.clickOnButton(doneButton);
    }

    }



