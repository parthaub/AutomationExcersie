# login.feature
#
# PURPOSE:
#   BDD (Behavior-Driven Development) feature file for Login functionality.
#   Written in GHERKIN language — plain English structured as:
#     Feature  → what we're testing
#     Scenario → one specific test case
#     Given    → setup / precondition
#     When     → the action the user takes
#     Then     → the expected result (assertion)
#
# HOW CUCUMBER WORKS:
#   1. You write .feature files like this (plain English)
#   2. Cucumber reads each step (Given/When/Then line)
#   3. It matches each step to a @Given/@When/@Then method in Java (StepDefinitions)
#   4. Those Java methods call Page Object methods to control the browser
#
# TAGS:
#   @smoke     = critical tests, run on every build
#   @regression = all tests, run nightly or before release
#   @negative   = tests for error conditions

Feature: User Login and Authentication
  As a registered user
  I want to be able to log in with my credentials
  So that I can access my account and place orders

  # Background runs BEFORE every Scenario in this Feature
  # Like @BeforeMethod in TestNG but only for scenarios in this file
  Background:
    Given user is on the home page
    When user clicks on Signup Login link

  @smoke @regression
  Scenario: Login with valid email and password
    # This is the "happy path" — everything goes right
    When user enters email "valid_user@test.com" and password "Test@1234"
    And user clicks Login button
    Then user should be logged in successfully
    And "Logged in as" text should be visible in header

  @regression @negative
  Scenario: Login with invalid password shows error message
    # Negative test: wrong password should show error
    When user enters email "valid_user@test.com" and password "WrongPassword123"
    And user clicks Login button
    Then error message "Your email or password is incorrect!" should be displayed

  @regression @negative
  Scenario: Login with empty fields shows error
    # Edge case: submitting empty form
    When user enters email "" and password ""
    And user clicks Login button
    Then login page should still be displayed

  @regression
  Scenario: Logout after successful login
    When user enters email "valid_user@test.com" and password "Test@1234"
    And user clicks Login button
    Then user should be logged in successfully
    When user clicks Logout
    Then user should be redirected to login page

  # Scenario Outline = data-driven test
  # The same scenario runs ONCE FOR EACH ROW in the Examples table
  # <email> and <password> are replaced with actual values from Examples
  @regression
  Scenario Outline: Login with multiple sets of credentials
    When user enters email "<email>" and password "<password>"
    And user clicks Login button
    Then login result should be "<expectedResult>"

    # Examples table: first row = column headers (must match <placeholders> above)
    # Each subsequent row = one test run
    Examples:
      | email                  | password     | expectedResult |
      | valid_user@test.com    | Test@1234    | success        |
      | wrong@email.com        | Test@1234    | failure        |
      | valid_user@test.com    | wrongpass    | failure        |
      | notregistered@test.com | anypassword  | failure        |
