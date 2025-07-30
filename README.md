# pot-summer-25

## Authors:

- Edgar Miloš
- Kasparas Murmokas
- Mykyta Tishkin
- Mark Andžejevski
- Agilė Astapovičiūtė

## Tech stack:

- Java 21
- PostgreSql 16.9
- Gradle 8+ (Groovy DSL)
- Auth0
- JUnit 5
- GitHub Actions
- Swagger UI
- MapStruct 1.6.3

## Development:

### Code Coverage:

We use the [JaCoCo Gradle plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html) to measure test coverage and enforce a minimum threshold of **80%**.

#### Generate the report:

```bash
./gradlew test
```

#### Locate the report:

After running the tests, you’ll find the complete HTML coverage report here:
```text
build/reports/jacoco/test/html/index.html
```

## API Documentation:

Once the app is running, visit:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Using Swagger UI with Auth0:

Swagger UI is wired for the **OAuth2.0 Authorization Code + PKCE** flow, so you can test secured endpoints right from the browser.

1. Start the app.
2. Visit http://localhost:8080/swagger-ui.html
3. Click Authorize → Auth0 → Authorize.
4. Log in on Auth0’s page with a valid tenant user.
5. Back in Swagger UI, a green padlock appears.
6. Endpoints marked with a green padlock icon automatically include Authorization: Bearer <access_token> in the request.
7. Expand an endpoint, set parameters if needed, and press Execute - expect 200 OK if your user is allowed.

### **`.env`** setup

An **`.env.example`** template is available in project root. 
- Create a file called **.env** in the project root (or **.env.test** for test‑specific overrides).
- Populate every placeholder with your real Auth0 values before starting the app.
- These variables are referenced by **application.yml** and **application-test.yml**.
