## Integration Testing with Testcontainers

This project uses [Testcontainers](https://www.testcontainers.org/) to spin up a real PostgreSQL instance during integration tests.

### How it works

- `PostgresTestContainer` (under `src/test/java/.../containers/`) sets up a fresh PostgreSQL container.
- Tests that require the database should extend `PostgresTestContainer`.
- The container is started automatically before the tests and stopped afterward.
- This ensures all tests run in isolation against a clean PostgreSQL environment.

### Example usage

To run integration tests against a real PostgreSQL instance managed by Testcontainers, extend the
shared `PostgresTestContainer` base class in your test:
```java
@SpringBootTest
class YourTestClass extends PostgresTestContainer {
    // Write your test methods here
}
```
### Note:
By extending PostgresTestContainer, your test class automatically starts a fresh
PostgreSQL container and wires Spring Boot’s datasource properties to use it.

