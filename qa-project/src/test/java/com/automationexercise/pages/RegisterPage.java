package com.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * RegisterPage.java
 *
 * PURPOSE:
 *   Represents the Account Registration form (/signup).
 *   This page appears after clicking "Signup" from the login page.
 *   Two sections:
 *     1. Account Information (title, name, password, DOB, checkboxes)
 *     2. Address Information (name, address, country, city, etc.)
 *
 * URL: https://automationexercise.com/signup
 */
public class RegisterPage extends BasePage {

    // ─── Section 1: Account Information Locators ─────────────────────────────
    private By titleMr              = By.id("id_gender1");
    private By titleMrs             = By.id("id_gender2");
    private By passwordInput        = By.cssSelector("input[data-qa='password']");
    private By dobDayDropdown       = By.cssSelector("select[data-qa='days']");
    private By dobMonthDropdown     = By.cssSelector("select[data-qa='months']");
    private By dobYearDropdown      = By.cssSelector("select[data-qa='years']");
    private By newsletterCheckbox   = By.id("newsletter");
    private By offersCheckbox       = By.id("optin");

    // ─── Section 2: Address Information Locators ─────────────────────────────
    private By firstNameInput       = By.cssSelector("input[data-qa='first_name']");
    private By lastNameInput        = By.cssSelector("input[data-qa='last_name']");
    private By companyInput         = By.cssSelector("input[data-qa='company']");
    private By address1Input        = By.cssSelector("input[data-qa='address']");
    private By address2Input        = By.cssSelector("input[data-qa='address2']");
    private By countryDropdown      = By.cssSelector("select[data-qa='country']");
    private By stateInput           = By.cssSelector("input[data-qa='state']");
    private By cityInput            = By.cssSelector("input[data-qa='city']");
    private By zipcodeInput         = By.cssSelector("input[data-qa='zipcode']");
    private By mobileInput          = By.cssSelector("input[data-qa='mobile_number']");

    // ─── Action Buttons & Confirmation ───────────────────────────────────────
    private By createAccountBtn     = By.cssSelector("button[data-qa='create-account']");
    private By accountCreatedHeader = By.xpath("//b[text()='Account Created!']");
    private By continueBtn          = By.cssSelector("a[data-qa='continue-button']");
    private By accountDeletedHeader = By.xpath("//b[text()='Account Deleted!']");

    // Page heading to confirm we're on the right form
    private By enterAccountInfoHeading = By.xpath("//h2[text()='Enter Account Information']");

    /**
     * Constructor: passes driver up to BasePage.
     */
    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS — Section 1: Account Information
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies the "Enter Account Information" form is displayed.
     * Call this after clicking Signup to confirm page loaded correctly.
     */
    public boolean isAccountFormVisible() {
        return isDisplayed(enterAccountInfoHeading);
    }

    /**
     * Selects the "Mr" radio button for title.
     */
    public void selectTitleMr() {
        click(titleMr);
    }

    /**
     * Selects the "Mrs" radio button for title.
     */
    public void selectTitleMrs() {
        click(titleMrs);
    }

    /**
     * Types the account password.
     * @param password  the password to set
     */
    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    /**
     * Selects the day of birth from the dropdown.
     * @param day  e.g., "15"
     */
    public void selectDobDay(String day) {
        selectByVisibleText(dobDayDropdown, day);
    }

    /**
     * Selects the month of birth from the dropdown.
     * @param month  e.g., "January"
     */
    public void selectDobMonth(String month) {
        selectByVisibleText(dobMonthDropdown, month);
    }

    /**
     * Selects the year of birth from the dropdown.
     * @param year  e.g., "1995"
     */
    public void selectDobYear(String year) {
        selectByVisibleText(dobYearDropdown, year);
    }

    /**
     * Checks the newsletter subscription checkbox.
     */
    public void checkNewsletter() {
        if (!driver.findElement(newsletterCheckbox).isSelected()) {
            click(newsletterCheckbox);
        }
    }

    /**
     * Checks the special offers checkbox.
     */
    public void checkSpecialOffers() {
        if (!driver.findElement(offersCheckbox).isSelected()) {
            click(offersCheckbox);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS — Section 2: Address Information
    // ─────────────────────────────────────────────────────────────────────────

    public void enterFirstName(String firstName) { type(firstNameInput, firstName); }
    public void enterLastName(String lastName)   { type(lastNameInput,  lastName);  }
    public void enterCompany(String company)      { type(companyInput,   company);   }
    public void enterAddress1(String address)     { type(address1Input,  address);   }
    public void enterAddress2(String address)     { type(address2Input,  address);   }
    public void selectCountry(String country)     { selectByVisibleText(countryDropdown, country); }
    public void enterState(String state)          { type(stateInput,     state);     }
    public void enterCity(String city)            { type(cityInput,      city);      }
    public void enterZipcode(String zip)          { type(zipcodeInput,   zip);       }
    public void enterMobile(String mobile)        { type(mobileInput,    mobile);    }

    // ─────────────────────────────────────────────────────────────────────────
    //  METHODS — Buttons & Confirmations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clicks "Create Account" button to submit the registration form.
     */
    public void clickCreateAccount() {
        click(createAccountBtn);
    }

    /**
     * Checks if "Account Created!" confirmation heading is visible.
     * @return  true if account was created successfully
     */
    public boolean isAccountCreated() {
        return isDisplayed(accountCreatedHeader);
    }

    /**
     * Clicks "Continue" after account creation confirmation.
     * Returns to the home page (logged in).
     */
    public void clickContinue() {
        click(continueBtn);
    }

    /**
     * Checks if "Account Deleted!" confirmation heading is visible.
     * @return  true if account was deleted successfully
     */
    public boolean isAccountDeleted() {
        return isDisplayed(accountDeletedHeader);
    }

    /**
     * CONVENIENCE METHOD: Fills the complete registration form in one call.
     * Calls all the individual field methods in the correct order.
     *
     * @param title      "Mr" or "Mrs"
     * @param password   account password
     * @param day        birth day e.g. "15"
     * @param month      birth month e.g. "June"
     * @param year       birth year e.g. "1995"
     * @param firstName  first name
     * @param lastName   last name
     * @param address    street address
     * @param country    country name (must match dropdown option exactly)
     * @param state      state/province
     * @param city       city
     * @param zip        zip/postal code
     * @param mobile     mobile phone number
     */
    public void fillCompleteRegistrationForm(
            String title, String password,
            String day, String month, String year,
            String firstName, String lastName, String address,
            String country, String state, String city, String zip, String mobile) {

        // Section 1: Account Info
        if (title.equalsIgnoreCase("Mr"))  selectTitleMr();
        else                               selectTitleMrs();

        enterPassword(password);
        selectDobDay(day);
        selectDobMonth(month);
        selectDobYear(year);
        checkNewsletter();
        checkSpecialOffers();

        // Section 2: Address Info
        enterFirstName(firstName);
        enterLastName(lastName);
        enterAddress1(address);
        selectCountry(country);
        enterState(state);
        enterCity(city);
        enterZipcode(zip);
        enterMobile(mobile);

        // Submit
        clickCreateAccount();
    }
}
