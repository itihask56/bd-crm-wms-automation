# BD CRM Automation Framework

Selenium TestNG automation framework for the Emoha BD CRM admin flows. The current coverage focuses on the admin login journey: email/password login, OTP verification, and post-login header validation.

## Tech Stack

- Java 17
- Maven
- Selenium WebDriver
- TestNG
- WebDriverManager
- Rest Assured and JSON dependencies are available for future API/data setup work

## Project Structure

```text
src/test/java/com/emoha/crm
  base/
    BaseTest.java          Test setup and teardown
    DriverFactory.java     WebDriver creation and lifecycle
  pages/
    LoginPage.java         Login form page object
    OtpModal.java          OTP modal page object
    AdminHeader.java       Post-login header page object
  tests/ui/
    LoginTest.java         Login flow test
  utils/
    ConfigReader.java      Config lookup and overrides
    WaitUtils.java         Explicit wait helpers

src/test/resources
  config.properties          Default shared config
  config.example.properties  Example config values
  testng.xml                 TestNG suite used by Maven Surefire
```

## Running Tests

Run the TestNG suite through Maven:

```bash
mvn test
```

Run in headless mode:

```bash
mvn test -Dheadless=true
```

Override any config value from the command line:

```bash
mvn test -DbaseUrl=https://staging.admin.emoha.com/log-in -Demail=user@example.com
```

## Configuration

Configuration is resolved in this order:

1. JVM system property, for example `-Demail=value`
2. Environment variable, for example `EMAIL=value`
3. `src/test/resources/config.properties`
4. Ignored local override file: `src/test/resources/config.local.properties`

Camel-case config keys become uppercase snake-case environment variables:

```text
mobileNumber -> MOBILE_NUMBER
testOtp      -> TEST_OTP
```

Use `config.local.properties` for personal or secret values. It is ignored by Git.

## Current Test Flow

`LoginTest.verifyLoginFlow()` performs these steps:

1. Opens `baseUrl`.
2. Enters email and password through `LoginPage`.
3. Completes mobile OTP verification through `OtpModal`.
4. Waits for the admin header logo.
5. Verifies the logo is displayed, has alt text `Emoha Admin`, and uses an SVG source.

## Important Notes

- Only Chrome is currently supported.
- Headless mode uses a fixed `1920x1080` window size.
- `DriverFactory` uses `ThreadLocal<WebDriver>`, which is safer for future parallel execution.
- Avoid adding sleeps. Prefer `WaitUtils` or page-specific waits for stable UI behavior.
- Keep credentials, OTP values, and personal test data out of committed files.

More implementation context is documented in [docs/framework-notes.md](docs/framework-notes.md).
