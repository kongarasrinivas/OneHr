
Feature: Super admin changes user role

  Scenario Outline: <userType> can log in successfully
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And clicks on the administration
    And clicks on the userManagement
    And the user searches "<searchWith>" for the created employee
    And the user clicks on Action
    And the user clicks on edit
    And the user selects role "<role>"
    And the user clicks on Save Changes
    And the user clicks on Action
    And the user clicks on edit
    Then the user verifies role as "<expectedRole>"
    And the user clicks on Save Changes
    And the user clicks on Action
    And the user clicks on edit
    And the user selects role "<role1>"
    And the user clicks on Save Changes
    And clicks on logout icon


    Examples:
      | userType   | dashboard              |  searchWith | role | expectedRole | role1 |
      | Super Admin | Super Admin Dashboard   | Srinivas    | Manager | Manager   | Employee |