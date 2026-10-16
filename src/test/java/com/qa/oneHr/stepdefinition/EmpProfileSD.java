package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.pageobjectfiles.EmpProfile;
import com.qa.oneHr.pageobjectfiles.HomePage;
import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;

public class EmpProfileSD {

    TestContextSetup testcontext;

    public EmpProfileSD(TestContextSetup testcontext) {
        this.testcontext = testcontext;
    }

    @And("the user clicks on My Profile")
    public void clickOnMyProfile() throws InterruptedException {
        HomePage homePage = testcontext.getPageObjectManager().getHomePage();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        // Employee dashboard shows a "policies need your acknowledgment" modal that blocks the header
        if (reusableMethods.isElementDisplayedOnScreen(homePage.remindMeLaterBtn)) {
            reusableMethods.clickOnButton(homePage.remindMeLaterBtn);
            reusableMethods.waitForElementToInvisible(homePage.remindMeLaterBtn);
        }

        // My Profile lives inside the profile menu, so open the menu first
        reusableMethods.waitForElementVisible(homePage.profile);
        reusableMethods.clickOnButton(homePage.profile);
        homePage.myProfileBtn();
    }

    @Then("the user clicks on each profile tab and verifies it is displayed")
    public void clickEachProfileTab() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        By[] tabs = {empProfile.aboutTab, empProfile.profileTab, empProfile.documentsTab,
                empProfile.jobTab, empProfile.assetsTab};

        for (By tab : tabs) {
            reusableMethods.waitForElementVisible(tab);
            reusableMethods.clickOnButton(tab);
            testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(tab),
                    "Profile tab is not displayed: " + tab);
        }
    }

    @And("the user clicks on the Profile tab")
    public void clickOnProfileTab() throws InterruptedException {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();
        reusableMethods.waitForElementVisible(empProfile.profileTab);
        reusableMethods.clickOnButton(empProfile.profileTab);
    }

    @Then("the Primary Details section fields should be displayed")
    public void verifyPrimaryDetailsSection() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        By[] fields = {empProfile.primaryDetailsSec, empProfile.firstName, empProfile.middleName,
                empProfile.lastName, empProfile.dateofBirth, empProfile.gender, empProfile.maritalStatus,
                empProfile.emergncyContactName, empProfile.emrgncyCntctRltnShip, empProfile.emrgncyCntctPhone};

        for (By field : fields) {
            reusableMethods.ScrollParticularElement(field);
            testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(field),
                    "Primary Details element is not displayed: " + field);
        }
    }

    @Then("the Contact Details section fields should be displayed")
    public void verifyContactDetailsSection() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        By[] fields = {empProfile.contactDetailsSection, empProfile.workEmail, empProfile.personalEmail,
                empProfile.mobileNumber};

        for (By field : fields) {
            reusableMethods.ScrollParticularElement(field);
            testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(field),
                    "Contact Details element is not displayed: " + field);
        }
    }

    @Then("the Addresses section fields should be displayed")
    public void verifyAddressesSection() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        By[] fields = {empProfile.addressesSection, empProfile.currentAddress, empProfile.permanentAddress};

        for (By field : fields) {
            reusableMethods.ScrollParticularElement(field);
            testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(field),
                    "Addresses element is not displayed: " + field);
        }
    }

    @Then("the Education section should be displayed")
    public void verifyEducationSection() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        reusableMethods.ScrollParticularElement(empProfile.educationSctn);
        testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(empProfile.educationSctn),
                "Education section is not displayed: " + empProfile.educationSctn);
    }

    @Then("the Identity & Statutory section fields should be displayed")
    public void verifyIdentityStatutorySection() throws Exception {
        EmpProfile empProfile = testcontext.getPageObjectManager().getEmpProfile();
        ReusableMethods reusableMethods = testcontext.getReusableMethods();

        By[] fields = {empProfile.identityStatutorySctn, empProfile.nationalID, empProfile.passportNum,
                empProfile.passportExpiry, empProfile.bankAccNum, empProfile.BankNameIFSC};

        for (By field : fields) {
            reusableMethods.ScrollParticularElement(field);
            testcontext.softAssert.assertTrue(reusableMethods.VerifyElementIsPresentorNot(field),
                    "Identity & Statutory element is not displayed: " + field);
        }
    }
}
