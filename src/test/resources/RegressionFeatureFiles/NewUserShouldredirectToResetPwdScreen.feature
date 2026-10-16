
Feature: Verify set you password screen displays after newly created user login in One HR Application


  @AddEmployee
  Scenario Outline: <userType> creates a new employee
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And clicks on the administration
    And clicks on the userManagement
    And clicks on the addUser
    When the user enters full name "AUTO"
    And the user enters company email "AUTO@nforceone.com"
    And the user selects role "<role>"
    And the user enters joining date "<joiningDate>"
    And the user selects employment type "<employmentType>"
    And the user selects business unit "<businessUnit>"
    And the user selects department "<department>"
    And the user selects designation "<designation>"
    And the user selects reporting manager "<reportingManager>"
    And the user selects location "<location>"
    And the user clicks on shift
    And the user clicks on default shift
    And the user clicks on Create User
    And the user waits until the user created message is displayed
    Then the user fetch temporary password
    And the user clicks on Done
    And clicks on logout icon
    When the user logs in with the newly created employee's email and temporary password
    And clicks on the login button
    Then the set your password screen should be displayed


    Examples:
      | userType   | dashboard             |     role     | joiningDate | employmentType | businessUnit | department  | designation       | reportingManager                    | location  |
      | SuperAdmin | Super Admin Dashboard |     Employee | 11-09-2026  | FULL TIME      | Operations   | Engineering | Software Engineer | Ramesh Adepu (rose@email.com)        | Hyderabad |