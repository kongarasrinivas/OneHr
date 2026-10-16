@Sanity
Feature: Add HrAdmin functionality in One HR Application

  @AddEmployee
  Scenario Outline: <userType> creates a new HrAdmin
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    When clicks on the administration
    And clicks on the userManagement
    And clicks on the addUser
    When the user enters full name "AUTO"
    And the user enters company email "AUTO@nforceone.com"
    And the user selects role "<role>"
    And the user enters joining date "<joiningDate>"
    And the user selects employment type "<employmentType>"
    And the user selects workMode type "<workModetype>"
    And the user selects business unit "<businessUnit>"
    And the user selects department "<department>"
    And the user selects designation "<designation>"
    And the user selects reporting manager "<reportingManager>"
    And the user selects location "<location>"
    And the user clicks on shift
    And the user clicks on default shift
    And the user clicks on Create User
    And the user waits until the user created message is displayed
    Examples:
      | userType   | dashboard             |     role     | joiningDate | employmentType | businessUnit | department  | designation       | reportingManager                    | location  | workModetype |
      | Super Admin | Super Admin Dashboard |     HR Admin | 11-09-2026  | FULL TIME      | Modozo   | Human Resources | HR Executive   | Mohan K (superadmintwo@nforceone.com)     | New York |   Remote    |
