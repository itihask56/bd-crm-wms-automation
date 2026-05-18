# Framework Notes

This document stores project context that is useful for future work. Treat it as the framework memory: decisions, conventions, current limitations, and next improvements.

## Current Purpose

The framework automates the Emoha BD CRM admin UI. The current main scenario is the login flow:

```text
open admin login page -> email/password login -> mobile OTP -> verify admin header
```

The goal is to keep the framework small, readable, and stable while it grows into broader CRM coverage.

## Branch Context

Recent work merged into `main` includes:

- Stable login and OTP automation flow.
- Admin header logo verification after login.
- Maven Surefire configuration for `testng.xml`.
- Improved configuration lookup.
- Thread-local WebDriver lifecycle.
- Local config override support through `config.local.properties`.

## Configuration Strategy

`ConfigReader` loads values from multiple places so local development, CI, and shared defaults can coexist.

Lookup priority:

1. JVM system property: `-Dkey=value`
2. Environment variable: uppercase snake-case version of the key
3. Classpath `config.properties`
4. Optional local file `src/test/resources/config.local.properties`

Examples:

```text
baseUrl      -> BASE_URL
mobileNumber -> MOBILE_NUMBER
testOtp      -> TEST_OTP
```

Sensitive values should be supplied through environment variables, JVM properties, or the ignored local file. Do not commit real credentials or OTP data.

## Driver Strategy

`DriverFactory` owns browser creation and cleanup.

Current behavior:

- Supports Chrome only.
- Uses WebDriverManager to set up ChromeDriver.
- Uses `ThreadLocal<WebDriver>` to avoid shared static driver issues.
- Maximizes the browser in headed mode.
- Uses `--headless=new` and `--window-size=1920,1080` in headless mode.
- Adds Chrome arguments to auto-allow media permissions and disable notifications.

If parallel execution is enabled later, keep driver access through `DriverFactory` or the test instance. Avoid reintroducing a shared static `WebDriver`.

## Page Object Conventions

The framework uses Page Object Model classes under `src/test/java/com/emoha/crm/pages`.

Preferred pattern:

- Keep locators inside page classes.
- Expose user-level actions, such as `login()` or `completeOtpFlow()`.
- Use explicit waits before interacting with dynamic elements.
- Keep assertions mostly in test classes unless a page method is purely a state query.

Current page objects:

- `LoginPage`: email/password login actions.
- `OtpModal`: mobile number, send OTP, enter OTP, submit, then wait for admin header.
- `AdminHeader`: post-login header state and logo details.

## Wait Strategy

Use `WaitUtils` for explicit waits. Avoid `Thread.sleep()` because it slows tests and still allows flaky timing.

Preferred waits:

- Wait for visibility before reading/display assertions.
- Wait for clickability before clicking or typing into dynamic fields.
- Wait for a meaningful post-action state, such as the admin header logo after OTP submit.

## Test Runner

Maven Surefire is configured to run:

```text
src/test/resources/testng.xml
```

Primary command:

```bash
mvn test
```

Useful overrides:

```bash
mvn test -Dheadless=true
mvn test -Dbrowser=chrome
mvn test -DbaseUrl=https://staging.admin.emoha.com/log-in
```

## Current Limitations

- Only one UI test exists.
- Only Chrome is supported.
- No screenshot-on-failure listener yet.
- No custom HTML report beyond Surefire/TestNG output.
- No CI workflow yet.
- API dependencies exist, but API helpers are not implemented yet.
- Login test depends on valid staging credentials and OTP behavior.

## Recommended Next Improvements

1. Add a TestNG listener for screenshots on failure.
2. Add structured logging for key test steps.
3. Add GitHub Actions for headless smoke runs.
4. Move all sensitive data to environment variables or `config.local.properties`.
5. Add a `BasePage` for common interactions if page objects start repeating code.
6. Add TestNG groups such as `smoke`, `login`, and `regression`.
7. Add API helpers only when they remove real UI setup cost or make data setup more reliable.

## When Adding New Tests

Use this flow:

1. Create or extend a page object for the screen.
2. Add explicit waits for dynamic UI behavior.
3. Keep test methods readable at the business-flow level.
4. Put environment-specific values in config, not hardcoded in tests.
5. Run `mvn test` locally before merging.

## Useful Files

- `pom.xml`: dependencies and Surefire configuration.
- `src/test/resources/testng.xml`: TestNG suite entry point.
- `src/test/resources/config.example.properties`: shared config template.
- `src/test/resources/config.local.properties`: ignored local overrides.
- `src/test/java/com/emoha/crm/base/DriverFactory.java`: browser setup.
- `src/test/java/com/emoha/crm/utils/ConfigReader.java`: config loading rules.
