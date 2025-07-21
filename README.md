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
`build/reports/jacoco/test/html/index.html`
