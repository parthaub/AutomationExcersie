package com.automationexercise.models;

/**
 * TestDataModel.java
 *
 * PURPOSE:
 *   A POJO (Plain Old Java Object) that holds user registration test data.
 *   Instead of passing 13 separate parameters to registerUser(), we bundle
 *   all the data into one object and pass that.
 *
 * WHAT IS A POJO?
 *   A simple Java class with:
 *   - Private fields (data)
 *   - Public getters (read the data)
 *   - Public setters (write the data)
 *   - A constructor (create the object with all data at once)
 *   No complex logic — just data storage.
 *
 * BUILDER PATTERN (inner static class):
 *   Instead of:   new UserData("John", "john@test.com", "pass123", ...)  ← hard to read
 *   We use:       UserData.builder().name("John").email("john@test.com").build()
 *   This is cleaner when there are many optional fields.
 */
public class TestDataModel {

    // ─── User Registration Fields ─────────────────────────────────────────────
    private String name;
    private String email;
    private String password;
    private String title;         // "Mr" or "Mrs"
    private String dobDay;        // e.g., "15"
    private String dobMonth;      // e.g., "June"
    private String dobYear;       // e.g., "1995"
    private String firstName;
    private String lastName;
    private String address;
    private String country;
    private String state;
    private String city;
    private String zipcode;
    private String mobileNumber;

    // ─── Private Constructor (used by Builder only) ───────────────────────────
    private TestDataModel() {}

    // ─── Getters (read-only access to fields) ────────────────────────────────
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPassword()     { return password; }
    public String getTitle()        { return title; }
    public String getDobDay()       { return dobDay; }
    public String getDobMonth()     { return dobMonth; }
    public String getDobYear()      { return dobYear; }
    public String getFirstName()    { return firstName; }
    public String getLastName()     { return lastName; }
    public String getAddress()      { return address; }
    public String getCountry()      { return country; }
    public String getState()        { return state; }
    public String getCity()         { return city; }
    public String getZipcode()      { return zipcode; }
    public String getMobileNumber() { return mobileNumber; }

    /**
     * Returns a String representation — useful for logging.
     * We intentionally EXCLUDE the password for security.
     */
    @Override
    public String toString() {
        return "UserData{name='" + name + "', email='" + email +
               "', city='" + city + "', country='" + country + "'}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  STATIC FACTORY: pre-built default test user
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a default test user with a unique email (using timestamp).
     * Use this when you need a quick valid user without configuring all fields.
     *
     * @return  a fully populated TestDataModel ready for registration
     */
    public static TestDataModel defaultTestUser() {
        // Generate a unique email using current timestamp
        // This prevents "email already exists" errors across test runs
        String uniqueEmail = "testuser_" + System.currentTimeMillis() + "@test.com";

        return new Builder()
            .name("Test User")
            .email(uniqueEmail)
            .password("Test@1234")
            .title("Mr")
            .dobDay("15")
            .dobMonth("June")
            .dobYear("1995")
            .firstName("Test")
            .lastName("User")
            .address("123 Test Street")
            .country("United States")
            .state("New York")
            .city("New York City")
            .zipcode("10001")
            .mobileNumber("1234567890")
            .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BUILDER CLASS
    //  Lets you create a TestDataModel step by step, setting only what you need.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * USAGE:
     *   TestDataModel user = new TestDataModel.Builder()
     *       .name("Alice")
     *       .email("alice@test.com")
     *       .password("Pass@123")
     *       .city("London")
     *       .country("United Kingdom")
     *       .build();
     */
    public static class Builder {
        // Create an empty TestDataModel — Builder will fill in the fields
        private final TestDataModel data = new TestDataModel();

        // Each setter returns "this" so you can chain calls: .name().email().build()
        public Builder name(String v)         { data.name         = v; return this; }
        public Builder email(String v)        { data.email        = v; return this; }
        public Builder password(String v)     { data.password     = v; return this; }
        public Builder title(String v)        { data.title        = v; return this; }
        public Builder dobDay(String v)       { data.dobDay       = v; return this; }
        public Builder dobMonth(String v)     { data.dobMonth     = v; return this; }
        public Builder dobYear(String v)      { data.dobYear      = v; return this; }
        public Builder firstName(String v)    { data.firstName    = v; return this; }
        public Builder lastName(String v)     { data.lastName     = v; return this; }
        public Builder address(String v)      { data.address      = v; return this; }
        public Builder country(String v)      { data.country      = v; return this; }
        public Builder state(String v)        { data.state        = v; return this; }
        public Builder city(String v)         { data.city         = v; return this; }
        public Builder zipcode(String v)      { data.zipcode      = v; return this; }
        public Builder mobileNumber(String v) { data.mobileNumber = v; return this; }

        /**
         * Finalizes and returns the built TestDataModel object.
         * Called LAST in the chain.
         */
        public TestDataModel build() {
            return data;
        }
    }
}
