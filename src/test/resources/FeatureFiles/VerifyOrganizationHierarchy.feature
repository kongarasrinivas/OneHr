
Feature: User should be able to view the organization hierarchy

  Scenario Outline: <userType> can log in successfully
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And User click on people
    And User click on organization
    And User click on oganization hierarchy
    Then user verify organization hierarchy page is displayed
    And clicks on logout icon

    Examples:
      | userType   | dashboard              |
      | Employee   | Employee Dashboard     |