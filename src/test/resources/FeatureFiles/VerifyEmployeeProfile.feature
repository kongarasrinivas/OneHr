
Feature: Verify my profile for employee

@VerifyEmpProfile
Scenario Outline: <userType> can log in successfully
When the "<userType>" user enters valid credentials
And clicks on the login button
Then the user should be land to "<dashboard>" dashboard page
And the user clicks on My Profile
Then the user clicks on each profile tab and verifies it is displayed
And the user clicks on the Profile tab
Then the Primary Details section fields should be displayed
And the Contact Details section fields should be displayed
And the Addresses section fields should be displayed
And the Education section should be displayed
And the Identity & Statutory section fields should be displayed

Examples:
| userType   | dashboard              |
| Employee   | Employee Dashboard     |
