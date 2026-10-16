
Feature: Verify super admin able to change employee manager

  Scenario Outline: <userType> can log in successfully
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And clicks on the administration
    And clicks on the userManagement
    And the user searches "<searchWith>" for the created employee
    And the user clicks on Action
    And the user clicks on edit
    And the user selects reporting manager "<reportingManager>"
    And the user clicks on Save Changes
    And the user clicks on Action
    And the user clicks on edit
    Then the user verifies reporting manager as "<expectedMngr>"
    And the user clicks on Save Changes
    And the user clicks on Action
    And the user clicks on edit
    And the user selects reporting manager "<reportingManager1>"
    And the user clicks on Save Changes
    And clicks on logout icon

    Examples:
      | userType   | dashboard              | searchWith | reportingManager               |               expectedMngr   | reportingManager1   |
      | Super Admin | Super Admin Dashboard | srinivas   |  Ramesh Adepu (rose@email.com) | Ramesh Adepu (rose@email.com) | Suresh Kumar Hanumalla (hradmin@nforceone.com) |