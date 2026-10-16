
Feature: Delete Employee functionality in One HR Application

  Scenario Outline: <userType> deletes an existing "Test" employee
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And clicks on the administration
    And clicks on the userManagement
    And the user searches "Test" for the created employee
    And the user deletes the employee with full name "Test"

    Examples:
      | userType   | dashboard             |
      | Super Admin | Super Admin Dashboard |
