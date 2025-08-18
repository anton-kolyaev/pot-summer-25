# pot-summer-25

# Setup guides

- [.env Setup](#env-settings)
- [SMTP Setup](#how-to-configure-it-in-your-auth0)
- [How to authorize with Auth0](#how-to-authorize-with-auth0)
- [How to start using role based permission management and access secured endpoints](#How-to-start-using-role-based-permission-management-and-access-secured-endpoints)


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
3.1 Here, you need to copy the `CURL` request
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

## How to Authorize with Auth0

**If you don`t have authorized users**, you should:
- in `SecurityConfig.java` change from `.anyRequest().authenticated())` to `.anyRequest().permitAll())`
- After that, build and app `./gradlew bootRun`
- Use Swagger or Curl to execute a request on `POST /v1/users/` endpoint
> With Curl (bash/zsh):
```bash
curl -X 'POST' \
  'http://localhost:8080/v1/users' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "firstName": "mykyta",
  "lastName": "tishkin",
  "username": "mkts26",
  "email": "nikita.tishkin.13+new26@gmail.com",
  "dateOfBirth": "2025-01-25",
  "ssn": "725-13-1666",
  "addressData": [
    {
      "country": "Lithuania",
      "city": "Vilnius",
      "state": "Vilniaus",
      "street": "Sauletekio",
      "building": "25",
      "room": "29"
    }
  ],
  "phoneData": [
    {
      "code": "370",
      "number": "66606733"
    }
  ],
  "functions": [
    "COMPANY_MANAGER"
  ],
  "companyId": "641e4c9d-e897-4c91-830e-c1daae443bee"
}'
```
- Check your email and set upa  password
- After that, check in your Auth0 Dashboard, is your user active?
- If **yes**,
- - rollback `SecurityConfig.java` changes, and relaunch app with `./gradlew bootRun`
- - Go to Swagger and press `Authorize`, enter your `client_id` and `client_secret`, you can take it from your `.env` file, press `Authorize`
- - Enter your user's email and password 
- - Try to execute any endpoint in Swagger
- If **no**
- - Probably, there are some errors in the previous steps

## How to start using role based permission management and access secured endpoints:

### How to make JWTs include claims with User's roles and functions:
In Auth0:
- Click `Actions` on the left ->
- Click `Triggers` ->
- Click `Post Login` ->
- Click `Add action` (On the right side) ->
- Click `Create Custom Action` -> 
- Fill all required fields: 

    `Name:` InjectCustomClaims;  
    `Trigger:` Login / Post Login;  
    `Runtime:` Node 22;

- Replace `AUTH_API_AUDIENCE` with your `Auth0 Management API identifier` and paste this code that creates custom claims with user's roles and functions in the token (between ===):  

============================================================

  exports.onExecutePostLogin = async (event, api) => {
  const namespace = "AUTH_API_AUDIENCE";

const systemRoles = event.user.app_metadata?.roles || [];
const functionalRoles = event.user.app_metadata?.functions || [];
const companyId = event.user.app_metadata?.company_id || null;

api.idToken.setCustomClaim(namespace + "roles", systemRoles);
api.idToken.setCustomClaim(namespace + "functions", functionalRoles);
api.idToken.setCustomClaim(namespace + "company_id", companyId);

api.accessToken.setCustomClaim(namespace + "roles", systemRoles);
api.accessToken.setCustomClaim(namespace + "functions", functionalRoles);
api.accessToken.setCustomClaim(namespace + "company_id", companyId);
};

============================================================


- Click `Deploy` (On the right side above code window)
- Click `Back to Triggers`
- Drag `InjectCustomClaims` from the right side to the place between `Start` and `Complete`
- Click `Apply`.
- Now JWTs include user's roles and functions.

### How to assign roles:
- Click `User Management`
- Click `Users`
- Select your user which you are authorizing with to the `Swagger` 
- Go to `Details`
- Scroll down and find `App Metadata` **(Not `User Metadata`)**
- In this window you can define roles and functions you want a user to have (See the examples below)
- Click `Save`

### Examples with defined roles:

**Application admin (can access every endpoint):**  
`{
    "roles": [  
    "APPLICATION_ADMIN"]  
}`

**Company admin:**  
`{  
    "company_id": "73673983-785e-4a9c-8776-0f9712a6f503",  
    "functions": [  
        "COMPANY_MANAGER",  
        "COMPANY_USER_MANAGER",  
        "COMPANY_PLAN_MANAGER",  
        "COMPANY_INSURANCE_PACKAGE_MANAGER",  
        "COMPANY_CLAIM_MANAGER",  
        "COMPANY_ENROLLMENT_MANAGER"  
        ],  
    "roles": [
    "COMPANY_ADMIN"
    ]  
}`

**Consumer:**  
`{ "user_id": "73673983-785e-4a9c-8776-0f9712a6f503",  
"functions": [ "CONSUMER_CLAIM_MANAGER" ],  
"roles": [ "CONSUMER" ] }
`



