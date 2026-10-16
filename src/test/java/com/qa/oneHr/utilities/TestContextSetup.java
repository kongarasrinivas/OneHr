package com.qa.oneHr.utilities;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.qa.oneHr.pageobjectfiles.POManager;
import com.qa.oneHr.testbase.TestBase;



public class TestContextSetup {
    private WebDriver driver;
    public TestBase testbase;
    private POManager pageObjectManager;
    private ReusableMethods reusableMethods;

    //Example test data
    public String ProductNameWithCurrentDate;
    public String SegmentNameWithDate;
    public String PromotionNameWithCurrentDate;

    // Shared across step definition classes within one scenario (PicoContainer
    // gives everyone the same TestContextSetup instance per scenario), so the
    // employee created by AddUserSD can be searched for later in the same scenario.
    public String lastGeneratedEmail;
    public String lastGeneratedFullName;
    public String lastGeneratedTempPassword;

    public TestContextSetup() {
        testbase = new TestBase();
    }

    //Creating soft assert to pass accross all the stepdefintions
    public SoftAssert softAssert = new SoftAssert();

    //Here we are fetching the driver information and assigning it to driver variable.
    // Get driver, initialize browser if not done already
    public WebDriver getDriver() throws InterruptedException {
        if (driver == null) {
            testbase.openBrowser();
            driver = testbase.getDriver();
        }
        return driver;
    }

    //Here we created one customized method to pass the driver information to the PageObjectManager
    // Get Page Object Manager (lazy initialization)
    public POManager getPageObjectManager() throws InterruptedException {
        if (pageObjectManager == null) {
            pageObjectManager = new POManager(getDriver(), getReusableMethods(), this); //This represents current object that we are passing POM manager
        }
        return pageObjectManager;
    }


    public ReusableMethods getReusableMethods() {
        if (reusableMethods == null) {
            reusableMethods = new ReusableMethods();
        }
        return reusableMethods;
    }


}
