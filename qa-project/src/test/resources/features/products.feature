# products.feature
#
# Tests for the Products page — browsing, searching, adding to cart

Feature: Product Catalogue and Search
  As a shopper
  I want to browse and search for products
  So that I can find what I want to buy

  Background:
    Given user is on the home page

  @smoke @regression
  Scenario: View all products on Products page
    When user clicks on Products link in navigation
    Then user should be on All Products page
    And product list should be visible with multiple products

  @smoke @regression
  Scenario: Search for a product by keyword
    When user clicks on Products link in navigation
    And user searches for product "top"
    Then "SEARCHED PRODUCTS" section should appear
    And search results should not be empty

  @regression
  Scenario: Add a product to cart and verify
    When user clicks on Products link in navigation
    And user hovers over first product and clicks Add to Cart
    And user clicks Continue Shopping in the popup
    And user hovers over second product and clicks Add to Cart
    And user clicks View Cart in the popup
    Then cart page should have 2 products

  @regression
  Scenario Outline: Search for different product keywords
    When user clicks on Products link in navigation
    And user searches for product "<keyword>"
    Then "SEARCHED PRODUCTS" section should appear

    Examples:
      | keyword |
      | top     |
      | tshirt  |
      | jeans   |
