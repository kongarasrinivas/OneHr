
Feature: Login feature in One Hr Application


  Scenario Outline: <userType> can log in successfully
    When the "<userType>" user enters valid credentials
    And clicks on the login button
    Then the user should be land to "<dashboard>" dashboard page
    And clicks on logout icon

    Examples:
      | userType   | dashboard              |
      | Employee   | Employee Dashboard     |
