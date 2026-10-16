package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.utilities.EnhancedPdfReportManager;
import com.qa.oneHr.utilities.TestContextSetup;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class AddUserSD {

    TestContextSetup testcontext;
    private String randomSuffix;

    public AddUserSD(TestContextSetup testcontext) {
        this.testcontext = testcontext;
    }


    @And("clicks on the administration")
    public void clickAdministration() throws InterruptedException {
        testcontext.getPageObjectManager().getHomePage().clickAdministriation();
    }

    @And("clicks on the userManagement")
    public void clickUserMngmnt() throws InterruptedException {
        testcontext.getPageObjectManager().getHomePage().clickUserMngmnt();
    }

    @And("clicks on the addUser")
    public void clickAddUser() throws InterruptedException {
        testcontext.getPageObjectManager().getHomePage().clickAddUser();
    }

@When("the user enters full name {string}")
public void enterFullName(String name) throws InterruptedException {

    randomSuffix = testcontext.getReusableMethods().generateRandomNumber(4);
    String fullNameValue = "Test";
    System.out.println("Generated full name: " + fullNameValue);
    testcontext.lastGeneratedFullName = fullNameValue;
    EnhancedPdfReportManager.setNextStepTextOverride("the user enters full name \"" + fullNameValue + "\"");
    testcontext.getPageObjectManager().getAddUser().enterFullName(fullNameValue);
}
@And("the user enters company email {string}")
public void enterCompanyEmail(String email) throws InterruptedException {

    String randomEmail = "Test" + randomSuffix + "@nforceone.com";
    System.out.println("Generated random email: " + randomEmail);
    testcontext.lastGeneratedEmail = randomEmail;
    EnhancedPdfReportManager.setNextStepTextOverride("the user enters company email \"" + randomEmail + "\"");
    testcontext.getPageObjectManager().getAddUser().enterCompanyEmail(randomEmail);
}


@And("the user selects role {string}")
public void selectRole(String role) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectRole(role);
}



@And("the user enters joining date {string}")
public void enterJoiningDate(String date) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().enterJoiningDate(date);
}


@And("the user selects employment type {string}")
public void selectEmploymentType(String employmentType) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectEmploymentType(employmentType);
}

//step not using as of now
//@And("the user selects work mode {string}")
//public void selectWorkMode(String workMode) throws InterruptedException {
//
//    testcontext.getPageObjectManager().getAddUser().selectWorkMode(workMode);
//}

@And("the user selects workMode type {string}")
public void selectWorkMode(String workModeType) throws InterruptedException {
    testcontext.getPageObjectManager().getAddUser().selectWorkMode(workModeType);
}


@And("the user selects business unit {string}")
public void selectBusinessUnit(String businessUnit) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectBusinessUnit(businessUnit);
}


@And("the user selects department {string}")
public void selectDepartment(String department) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectDepartment(department);
}


@And("the user selects designation {string}")
public void selectDesignation(String designation) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectDesignation(designation);
}


@And("the user selects location {string}")
public void selectLocation(String location) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectLocation(location);
}


@And("the user selects reporting manager {string}")
public void selectReportingManager(String manager) throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().selectReportingManager(manager);
}

@And("the user clicks on shift")
public void clickShift() throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().clickShift();
}

@And("the user clicks on default shift")
public void clickDefaultShift() throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().clickDefaultShift();
}

@Then("the user verify temporary password is returned")
public void verifyTempPwd() throws InterruptedException {
    testcontext.getPageObjectManager().getAddUser().verifyTempPwdDisplayed();
}

@Then("the user fetch temporary password")
public void getpwd() throws InterruptedException {

    String tempPassword = testcontext.getPageObjectManager().getAddUser().getTempPwd();
    testcontext.lastGeneratedTempPassword = tempPassword;
    System.out.println("Captured temp password: " + tempPassword + " for email: " + testcontext.lastGeneratedEmail);
    EnhancedPdfReportManager.setNextStepTextOverride("the user fetch temporary password \"" + tempPassword + "\"");
}


@And("the user clicks on Create User")
public void clickCreateUser() throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().clickCreateUser();
}

@And("the user waits until the user created message is displayed")
public void waitForUserCreatedConfirmation() throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().waitForUserCreatedConfirmation();
}

@And("the user clicks on Done")
public void clickDone() throws InterruptedException {

    testcontext.getPageObjectManager().getAddUser().clickDone();
}



    @Then("the user verifies reporting manager as {string}")
    public void verifyReportingMngr(String expectedMngr) throws InterruptedException {

        String actualManager = testcontext.getPageObjectManager().getAddUser().getManager();
        Assert.assertEquals(actualManager,expectedMngr,"Reporting Manager value is not matching.");

    }

    @Then("the user verifies role as {string}")
    public void verifyUserRole(String expectedRole) throws InterruptedException {
        String actualRole = testcontext.getPageObjectManager().getAddUser().getRole();
        Assert.assertEquals(actualRole,expectedRole,"Role is changed sucessfully.");
    }

}

