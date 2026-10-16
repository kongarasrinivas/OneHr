package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.utilities.TestContextSetup;
import io.cucumber.java.en.Then;

public class OrganizationHierarchySD {

    TestContextSetup testcontext;
    public OrganizationHierarchySD (TestContextSetup testcontext) {
        this.testcontext=testcontext;
    }
@Then("user verify organization hierarchy page is displayed")
    public void verifyOrgHierarchypage() throws InterruptedException {
        boolean result = testcontext.getPageObjectManager().getOrganizationHierarchy().validateOrgHierachDisplayed();
        testcontext.softAssert.assertTrue(result,"Organization Hierarchy page is failed to display");

    }
}

