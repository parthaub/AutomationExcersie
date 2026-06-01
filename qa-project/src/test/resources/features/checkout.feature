# checkout.feature
#
# End-to-end checkout flow tests

Feature: End-to-End Order Placement
  As a registered user
  I want to add products to my cart and place an order
  So that I can purchase items from the store

  @smoke @regression @e2e
  Scenario: Place order as a logged-in user
    Given user is on the home page
    When user clicks on Products link in navigation
    And user hovers over first product and clicks Add to Cart
    And user clicks View Cart in the popup
    And user clicks Proceed to Checkout
    And user clicks Register Login from checkout modal
    And user logs in with email "checkout_test@test.com" and password "Test@1234"
    And user returns to cart page
    And user clicks Proceed to Checkout again
    Then checkout page should display address details
    When user enters order comment "Automated test order"
    And user clicks Place Order
    And user fills payment with card name "QA Test" number "4111111111111111" cvc "123" expiry month "12" year "2028"
    And user confirms payment
    Then order success message should be displayed

  @regression
  Scenario: Cart persists after login
    Given user is on the home page
    When user clicks on Products link in navigation
    And user hovers over first product and clicks Add to Cart
    And user clicks Continue Shopping in the popup
    And user clicks on Signup Login link
    And user logs in with email "checkout_test@test.com" and password "Test@1234"
    When user clicks on Cart link in navigation
    Then cart should not be empty
