package com.qa.oneHr.stepdefinition;

import com.qa.oneHr.pageobjectfiles.UserManagement;
import com.qa.oneHr.utilities.TestContextSetup;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class UserManagementSD {

    TestContextSetup testcontext;

    public UserManagementSD(TestContextSetup testcontext) {
        this.testcontext = testcontext;
    }

    @And("the user searches for the created employee")
    public void searchForCreatedEmployee() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().searchtxtBox(testcontext.lastGeneratedEmail);
    }


    @And("the user searches {string} for the created employee")
    public void searchForCreatedEmployee(String searchText) throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().searchtxtBox(searchText);
    }


    @And("the user searches the created employee by full name")
    public void searchCreatedEmployeeByFullName() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().searchtxtBox(testcontext.lastGeneratedFullName);
    }


    @And("the user clicks on 3 dots for the created employee")
    public void openActionsForCreatedEmployee() throws InterruptedException {

        UserManagement userManagement = testcontext.getPageObjectManager().getUserManagement();
        String expectedEmail = testcontext.lastGeneratedEmail;

        int rowCount = userManagement.getRowCount();
        System.out.println("User Management table row count after search: " + rowCount);

        for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
            String actualEmail = userManagement.getRowEmail(rowIndex);
            System.out.println("Row " + rowIndex + " of " + rowCount + " -> Email: " + actualEmail);

            if (actualEmail != null && actualEmail.trim().equalsIgnoreCase(expectedEmail.trim())) {
                userManagement.clickRowActions(rowIndex);
                return;
            }
        }

        throw new IllegalStateException("No row found with email \"" + expectedEmail
                + "\" after searching full name \"" + testcontext.lastGeneratedFullName + "\".");
    }

    @And("the user deletes the employee with full name {string}")
    public void deleteEmployeeWithFullName(String fullName) throws InterruptedException {

        UserManagement userManagement = testcontext.getPageObjectManager().getUserManagement();

        int rowIndex = 1;
        int rowCount = userManagement.getRowCount();
        System.out.println("User Management table row count after search: " + rowCount);

        int deletedCount = 0;
        while (rowIndex <= rowCount) {


            String actualName = userManagement.getRowName(rowIndex);
            System.out.println("Row " + rowIndex + " of " + rowCount + " -> Name: " + actualName);


            if (actualName != null && actualName.trim().equalsIgnoreCase(fullName.trim())) {


                String actualEmail = userManagement.getRowEmail(rowIndex);
                System.out.println("Row " + rowIndex + " matched \"" + fullName + "\" -> Email: " + actualEmail);


                userManagement.clickRowActions(rowIndex);
                userManagement.clickDelete();
                userManagement.enterCnfrmEmialDlt(actualEmail);
                userManagement.clickDeletePermanently();
                deletedCount++;


                rowCount = userManagement.getRowCount();
            } else {
                // 5. Not equal - go to the next row.
                rowIndex++;
            }
        }

        System.out.println("Deleted " + deletedCount + " row(s) matching full name \"" + fullName + "\".");
    }


    @And("the user deletes all employees found in the search")
    public void deleteAllEmployeesFoundInSearch() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().deleteAllRowsInTable();
    }

    @And("the user opens actions for the employee")
    public void openActionsForEmployee() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().clickActions();
    }

    @And("the user clicks on edit")
    public void clickEdit() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().clickEdit();
    }

    @Then("the user clicks on deactivate")
    public void clickDelete() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().clickDeactivate();
    }

    @And("the user confirms deactivate in the popup")
    public void confirmDeactivate() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().clickConfirmDeactivate();
    }

    @Then("the created employee should be deactivated")
    public void verifyCreatedEmployeeDeactivated() throws InterruptedException {
        UserManagement userManagement = testcontext.getPageObjectManager().getUserManagement();
        String email = testcontext.lastGeneratedEmail;


        String rowText = null;
        for (int attempt = 1; attempt <= 10; attempt++) {
            Thread.sleep(1000);
            rowText = userManagement.getRowTextByEmail(email);
            System.out.println("Status check " + attempt + " for " + email + " -> Row: " + rowText);
            if (rowText == null || rowText.toLowerCase().matches("(?s).*\\b(inactive|deactivated)\\b.*")) {
                break;
            }
        }

        if (rowText != null && !rowText.toLowerCase().matches("(?s).*\\b(inactive|deactivated)\\b.*")) {
            throw new AssertionError("Employee " + email + " is not deactivated. Row text: " + rowText);
        }
        System.out.println("Employee " + email + " is deactivated"
                + (rowText == null ? " (no longer listed in the active users table)." : ". Row: " + rowText));
    }

    @And("the user clicks on Save Changes")
    public void clickSaveChanges() throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().clickSaveChanges();
    }

    @Then("the designation should be updated to {string}")
    public void verifyDesignationUpdated(String expectedDesignation) throws InterruptedException {
        testcontext.getPageObjectManager().getAddUser().verifyDesignation(expectedDesignation);
    }

    @Then("the user enters email for delete{string}")
    public void enterEmailToDlt(String email) throws InterruptedException {
        testcontext.getPageObjectManager().getUserManagement().enterCnfrmEmialDlt(email);
    }


    @And("the user clicks on Action")
    public void clickon3Dots() throws InterruptedException {
       testcontext.getPageObjectManager().getUserManagement().clickActions();
    }

}
