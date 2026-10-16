package com.qa.oneHr.pageobjectfiles;

import com.qa.oneHr.utilities.ReusableMethods;
import com.qa.oneHr.utilities.TestContextSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrganizationHierarchy {

    WebDriver driver;
    ReusableMethods reusableMethods;
    TestContextSetup testContext;
    public OrganizationHierarchy(WebDriver driver, ReusableMethods reusablemethods, TestContextSetup testcontext) {

        this.driver=driver;
        this.reusableMethods=reusablemethods;
        this.testContext=testcontext;
}
    public final By orgnHierarchyHeading=By.xpath("//h1[text()='Organization Hierarchy']");


    public boolean validateOrgHierachDisplayed()
    {
        return reusableMethods.isElementDisplayed(orgnHierarchyHeading);
    }
}
