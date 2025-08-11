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
2. Visit http://localhost:8080/swagger-ui/index.html
3. Click Authorize → Auth0 → Authorize.
4. Log in on Auth0’s page with a valid tenant user.
5. Back in Swagger UI, a green padlock appears.
6. Endpoints marked with a green padlock icon automatically include Authorization: Bearer <access_token> in the request.
7. Expand an endpoint, set parameters if needed, and press Execute - expect 200 OK if your user is allowed.

# Setup guides

## `.env` settings 
Okay, first of all, use the next command
```bash 
cp .env.example .env
``` 
This command will create `.env` file for you, based on the example. After that, you can remove all comments.
 
Now, register yourself on [Auth0](https://auth0.com/docs/secure/tokens/access-tokens/get-access-tokens)
## Follow the next steps:
1. Create an app
2. Grats perm for it
2.1 To grant perms, you need to search for `API`
2.2 Open a link
2.3 Press edit and choose your application
2.4 Choose `Machine to Machine`
2.5 Authorize your application
2.6 Grant access for `read:user`, `update:user`, `create:user`, `delete:user` (you can use this for search)

1. Go to the test tab
3.1 Here you need to copy the `CURL` request
```bash
curl --request POST \
  --url https://dev-urlink.us.auth0.com/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "client_id":"ur data",
    "client_secret":"ur data",
    "audience":"ur data",
    "grant_type":"ur data"
  }'
  ```
  1. Put all necessary data into the `.env` file in your project
  
  **NOTE!** If you are following this guide, before it was merged into main, do not forget to add in your `.env` file AUTH0_API_TOKEN 
  
## How to configure it in your Auth0 
### Create Google SMTP
1. Go to your Google Account Security Settings
2. Enable 2-Step Verification
3. Scroll to "App Passwords"
4. Generate a new app password for "Mail"
5. Use that 16-digit app password instead of your actual Gmail password in your app or code

Add these data into .env as the next fields
```json
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your mail
MAIL_PASSWORD=your pass
```

### Configure the same SMTP on Auth0
1. Open your Auth0 dashboard
2. Navigate to: Branding > Emails > Provider
3. Choose "SMTP" as the email provider
4. Fill out the form
5. ✅ Click Save
