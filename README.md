# pot-summer-25
## Authors:
- Edgar Miloš
- Kasparas Murmokas
- Mykyta Tishkin
- Mark Andžejevski
- Agilė Astapovičiūtė  

<details><summary> API Draft</summary>

## Base URL
```
https://api.ibta.com/v1
```

### Company Management
```
GET    /companies                    # Get all companies / Search by name, country code, status, date
POST   /companies                    # Create new company profile
GET    /companies/{id}               # View company details
PUT    /companies/{id}               # Update company profile
DELETE /companies/{id}               # Deactivate company
GET    /companies/{id}/users         # View users for a specific company
GET    /companies/{id}/packages      # View packages for a specific company
GET    /companies/{id}/claims        # View claims for a specific company
GET    /companies/{id}/enrollments   # Get enrollments for company
```

### User Management
```
GET    /users                        # Get all users / Search by name, email, ssn,, company_id, status, date
POST   /users                        # Create new user (company-level or consumer-level)
GET    /users/{id}                   # View user details
PUT    /users/{id}                   # Update user profile
DELETE /users/{id}                   # Deactivate user
GET    /users/{id}/roles             # View user roles
PUT    /users/{id}/roles             # Update user roles
GET    /users/{id}/functions         # View user functions
PUT    /users/{id}/functions         # Update user functions
GET    /users/{id}/claims            # View claims for a specific user
GET    /users/{id}/enrollments       # Get enrollments for user
```

### Insurance Package Management 
```
GET    /packages                     # Get all packages / Search packages by company_id, dates, status
POST   /packages                     # Create new insurance package
GET    /packages/{id}                # View package details
PUT    /packages/{id}                # Update package (if not active)
DELETE /packages/{id}                # Deactivate package, here can be PATCH instead of DELETE
```

### Claims Management
```
GET    /claims                       # Get all claims / Search claims by claim number, user_id, company_id, status, date
POST   /claims                       # Create a claim
GET    /claims/{id}                  # View claim details
POST   /claims/{id}/approval         # Approve claim
POST   /claims/{id}/deniel           # Deny claim
PUT    /claims/{id}                  # Update claim information
```

### Enrollments
```
GET    /enrollments                  # Search enrollments by user_id, company_id, package_id, status, date
POST   /enrollments                  # Create enrollment
GET    /enrollments/{id}             # Get enrollment by ID
PUT    /enrollments/{id}             # Update enrollment
DELETE /enrollments/{id}             # Cancel enrollment
```

### Benefit Package Management
```
GET    /benefits                     # Get all benefits packages
POST   /benefits                     # Create benefit package
GET    /benefits/{id}                # View benefit package details
PUT    /benefits/{id}                # Update benefit package
DELETE /benefits/{id}                # Delete benefit package, here can be PATCH instead of DELETE
GET    /packages/{id}/benefits       # View benefits for a specific package
```

Common HTTP Status Codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `422` - Validation Error
- `500` - Internal Server Error
</details>

<details><summary>APIs DTOs</summary>

## Detailed Endpoint Specifications

### Companies Endpoints

#### GET /companies
**Query Parameters:**
- `name` (optional): Company name filter
- `country_code` (optional): Country code filter
- `status` (optional): Active/Inactive filter
- `page` (optional): Page number for pagination
- `size` (optional): Page size for pagination

**Response DTO:**
```json
{
  "companies": [
    {
      "id": "uuid",
      "name": "string",
      "country_code": "string",
      "address": "string",
      "phone": "string",
      "email": "string",
      "website": "string",
      "status": "ACTIVE|INACTIVE",
      "who_created": "uuid",
      "created_at": "datetime",
      "updated_at": "datetime"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### POST /companies
**Request DTO:**
```json
{
  "name": "string",
  "country_code": "string",
  "address": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "admin_user": {
    "first_name": "string",
    "last_name": "string",
    "username": "string",
    "email": "string",
    "phone": "string",
    "date_of_birth": "date",
    "ssn": "string"
  }
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "country_code": "string",
  "address": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "status": "ACTIVE",
  "who_created": "uuid",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /companies/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "country_code": "string",
  "address": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "status": "ACTIVE|INACTIVE",
  "who_created": "uuid",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### PUT /companies/{id}
**Request DTO:**
```json
{
  "name": "string",
  "country_code": "string",
  "address": "string",
  "phone": "string",
  "email": "string",
  "website": "string"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "country_code": "string",
  "address": "string",
  "phone": "string",
  "email": "string",
  "website": "string",
  "status": "ACTIVE|INACTIVE",
  "who_created": "uuid",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /companies/{id}/users
**Response DTO:**
```json
{
  "users": [
    {
      "id": "uuid",
      "first_name": "string",
      "last_name": "string",
      "username": "string",
      "email": "string",
      "phone": "string",
      "date_of_birth": "date",
      "ssn": "string",
      "status": "ACTIVE|INACTIVE",
      "company_id": "uuid",
      "who_created": "uuid",
      "roles": ["string"],
      "created_at": "datetime"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### GET /companies/{id}/packages
**Response DTO:**
```json
{
  "packages": [
    {
      "id": "uuid",
      "name": "string",
      "company_id": "uuid",
      "who_created": "uuid",
      "start_date": "date",
      "end_date": "date",
      "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
      "status": "INITIALIZED|ACTIVE|INACTIVE",
      "created_at": "datetime"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### GET /companies/{id}/claims
**Response DTO:**
```json
{
  "claims": [
    {
      "id": "uuid",
      "claim_number": "string",
      "user_id": "uuid",
      "type": "MEDICAL|PHARMACY|DENTAL|VISION",
      "amount": "decimal",
      "description": "string",
      "status": "PENDING|APPROVED|DENIED",
      "submitted_date": "datetime",
      "processed_date": "datetime",
      "company_id": "uuid",
      "who_created": "uuid"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

### Users Endpoints

#### GET /users
**Query Parameters:**
- `name` (optional): User name filter
- `email` (optional): Email filter
- `date_of_birth` (optional): Date of birth filter
- `status` (optional): Active/Inactive filter
- `ssn` (optional): SSN filter
- `function` (optional): Role/function filter
- `company_id` (optional): Company filter
- `page` (optional): Page number
- `size` (optional): Page size

**Response DTO:**
```json
{
  "users": [
    {
      "id": "uuid",
      "first_name": "string",
      "last_name": "string",
      "username": "string",
      "email": "string",
      "phone": "string",
      "date_of_birth": "date",
      "ssn": "string",
      "status": "ACTIVE|INACTIVE",
      "company_id": "uuid",
      "who_created": "uuid",
      "roles": ["string"],
      "created_at": "datetime",
      "updated_at": "datetime"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### POST /users
**Request DTO:**
```json
{
  "first_name": "string",
  "last_name": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "date_of_birth": "date",
  "ssn": "string",
  "company_id": "uuid",
  "roles": ["string"],
  "password": "string"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "first_name": "string",
  "last_name": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "date_of_birth": "date",
  "ssn": "string",
  "status": "ACTIVE",
  "company_id": "uuid",
  "who_created": "uuid",
  "roles": ["string"],
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /users/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "first_name": "string",
  "last_name": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "date_of_birth": "date",
  "ssn": "string",
  "status": "ACTIVE|INACTIVE",
  "company_id": "uuid",
  "roles": ["string"],
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### PUT /users/{id}
**Request DTO:**
```json
{
  "first_name": "string",
  "last_name": "string",
  "email": "string",
  "phone": "string",
  "date_of_birth": "date",
  "ssn": "string"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "first_name": "string",
  "last_name": "string",
  "username": "string",
  "email": "string",
  "phone": "string",
  "date_of_birth": "date",
  "ssn": "string",
  "status": "ACTIVE|INACTIVE",
  "company_id": "uuid",
  "roles": ["string"],
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /users/{id}/roles
**Response DTO:**
```json
{
  "roles": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string"
    }
  ]
}
```

#### PUT /users/{id}/roles
**Request DTO:**
```json
{
  "role_ids": ["uuid"]
}
```

**Response DTO:**
```json
{
  "roles": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string"
    }
  ]
}
```

#### GET /users/{id}/functions
**Response DTO:**
```json
{
  "functions": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string"
    }
  ]
}
```

#### PUT /users/{id}/functions
**Request DTO:**
```json
{
  "function_ids": ["uuid"]
}
```

**Response DTO:**
```json
{
  "functions": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string"
    }
  ]
}
```

### Insurance Packages Endpoints

#### GET /packages
**Query Parameters:**
- `name` (optional): Package name filter
- `start_date` (optional): Start date filter
- `end_date` (optional): End date filter
- `status` (optional): Status filter (INITIALIZED|ACTIVE|INACTIVE)
- `payroll_frequency` (optional): Payroll frequency filter
- `company_id` (optional): Company filter
- `page` (optional): Page number
- `size` (optional): Page size

**Response DTO:**
```json
{
  "packages": [
    {
      "id": "uuid",
      "name": "string",
      "company_id": "uuid",
      "who_created": "uuid",
      "start_date": "date",
      "end_date": "date",
      "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
      "status": "INITIALIZED|ACTIVE|INACTIVE",
      "created_at": "datetime",
      "updated_at": "datetime"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### POST /packages
**Request DTO:**
```json
{
  "name": "string",
  "company_id": "uuid",
  "start_date": "date",
  "end_date": "date",
  "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "company_id": "uuid",
  "who_created": "uuid",
  "start_date": "date",
  "end_date": "date",
  "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
  "status": "INITIALIZED",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /packages/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "company_id": "uuid",
  "who_created": "uuid",
  "start_date": "date",
  "end_date": "date",
  "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
  "status": "INITIALIZED|ACTIVE|INACTIVE",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### PUT /packages/{id}
**Request DTO:**
```json
{
  "name": "string",
  "start_date": "date",
  "end_date": "date",
  "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "company_id": "uuid",
  "who_created": "uuid",
  "start_date": "date",
  "end_date": "date",
  "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
  "status": "INITIALIZED|ACTIVE|INACTIVE",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

### Claims Endpoints

#### GET /claims
**Query Parameters:**
- `claim_number` (optional): Claim number filter
- `user_id` (optional): User filter
- `company_id` (optional): Company filter
- `status` (optional): Status filter (PENDING|APPROVED|DENIED)
- `date_from` (optional): Date range from
- `date_to` (optional): Date range to
- `page` (optional): Page number
- `size` (optional): Page size

**Response DTO:**
```json
{
  "claims": [
    {
      "id": "uuid",
      "claim_number": "string",
      "user_id": "uuid",
      "company_id": "uuid",
      "who_created": "uuid",
      "enrollment_id": "uuid",
      "type": "MEDICAL|PHARMACY|DENTAL|VISION",
      "amount": "decimal",
      "description": "string",
      "status": "PENDING|APPROVED|DENIED",
      "submitted_date": "datetime",
      "processed_date": "datetime",
      "approved_amount": "decimal",
      "denied_reason": "string",
      "notes": "string"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### POST /claims
**Request DTO:**
```json
{
  "user_id": "uuid",
  "enrollment_id": "uuid",
  "type": "MEDICAL|PHARMACY|DENTAL|VISION",
  "amount": "decimal",
  "description": "string",
  "attachments": ["string"]
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "claim_number": "string",
  "user_id": "uuid",
  "company_id": "uuid",
  "who_created": "uuid",
  "enrollment_id": "uuid",
  "type": "MEDICAL|PHARMACY|DENTAL|VISION",
  "amount": "decimal",
  "description": "string",
  "status": "PENDING",
  "submitted_date": "datetime",
  "created_at": "datetime"
}
```

#### GET /claims/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "claim_number": "string",
  "user_id": "uuid",
  "company_id": "uuid",
  "who_created": "uuid",
  "enrollment_id": "uuid",
  "type": "MEDICAL|PHARMACY|DENTAL|VISION",
  "amount": "decimal",
  "description": "string",
  "status": "PENDING|APPROVED|DENIED",
  "submitted_date": "datetime",
  "processed_date": "datetime",
  "approved_amount": "decimal",
  "denied_reason": "string",
  "notes": "string",
  "attachments": [
    {
      "id": "uuid",
      "file_name": "string",
      "file_size": "number",
      "mime_type": "string",
      "uploaded_at": "datetime"
    }
  ],
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### POST /claims/{id}/approval
**Request DTO:**
```json
{
  "notes": "string",
  "approved_amount": "decimal"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "claim_number": "string",
  "status": "APPROVED",
  "approved_amount": "decimal",
  "processed_date": "datetime",
  "notes": "string"
}
```

#### POST /claims/{id}/deniel
**Request DTO:**
```json
{
  "notes": "string",
  "reason": "string"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "claim_number": "string",
  "status": "DENIED",
  "denied_reason": "string",
  "processed_date": "datetime",
  "notes": "string"
}
```

#### PUT /claims/{id}
**Request DTO:**
```json
{
  "description": "string",
  "amount": "decimal",
  "notes": "string"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "claim_number": "string",
  "user_id": "uuid",
  "company_id": "uuid",
  "who_created": "uuid",
  "enrollment_id": "uuid",
  "type": "MEDICAL|PHARMACY|DENTAL|VISION",
  "amount": "decimal",
  "description": "string",
  "status": "PENDING|APPROVED|DENIED",
  "submitted_date": "datetime",
  "processed_date": "datetime",
  "approved_amount": "decimal",
  "denied_reason": "string",
  "notes": "string",
  "updated_at": "datetime"
}
```

#### GET /users/{id}/claims
**Response DTO:**
```json
{
  "claims": [
    {
      "id": "uuid",
      "claim_number": "string",
      "type": "MEDICAL|PHARMACY|DENTAL|VISION",
      "amount": "decimal",
      "description": "string",
      "status": "PENDING|APPROVED|DENIED",
      "submitted_date": "datetime",
      "processed_date": "datetime",
      "approved_amount": "decimal",
      "denied_reason": "string",
      "company_id": "uuid",
      "who_created": "uuid"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

### Benefit Packages Endpoints

#### GET /benefits
**Query Parameters:**
- `name` (optional): Benefit name filter
- `type` (optional): Benefit type filter
- `page` (optional): Page number
- `size` (optional): Page size

**Response DTO:**
```json
{
  "benefits": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string",
      "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
      "coverage_percentage": "decimal",
      "deductible_amount": "decimal",
      "max_benefit_amount": "decimal",
      "created_at": "datetime",
      "updated_at": "datetime",
      "company_id": "uuid",
      "who_created": "uuid"
    }
  ],
  "total": 100,
  "page": 1,
  "size": 20
}
```

#### POST /benefits
**Request DTO:**
```json
{
  "name": "string",
  "description": "string",
  "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
  "coverage_percentage": "decimal",
  "deductible_amount": "decimal",
  "max_benefit_amount": "decimal"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
  "coverage_percentage": "decimal",
  "deductible_amount": "decimal",
  "max_benefit_amount": "decimal",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /benefits/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
  "coverage_percentage": "decimal",
  "deductible_amount": "decimal",
  "max_benefit_amount": "decimal",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### PUT /benefits/{id}
**Request DTO:**
```json
{
  "name": "string",
  "description": "string",
  "coverage_percentage": "decimal",
  "deductible_amount": "decimal",
  "max_benefit_amount": "decimal"
}
```

**Response DTO:**
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
  "coverage_percentage": "decimal",
  "deductible_amount": "decimal",
  "max_benefit_amount": "decimal",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

#### GET /packages/{id}/benefits
**Response DTO:**
```json
{
  "benefits": [
    {
      "id": "uuid",
      "name": "string",
      "description": "string",
      "type": "MEDICAL|DENTAL|VISION|PHARMACY|LIFE|DISABILITY",
      "coverage_percentage": "decimal",
      "deductible_amount": "decimal",
      "max_benefit_amount": "decimal",
      "company_id": "uuid",
      "who_created": "uuid"
    }
  ]
}
```

### Enrollments Endpoints

#### GET /enrollments
**Query Parameters:**
- `user_id` (optional): User filter
- `company_id` (optional): Company filter
- `package_id` (optional): Package filter
- `status` (optional): Status filter
- `page` (optional): Page number
- `size` (optional): Page size

**Response DTO:**
```json
{
  "enrollments": [
    {
      "id": "uuid",
      "user_id": "uuid",
      "company_id": "uuid",
      "package_id": "uuid",
      "election_amount": "decimal",
      "contribution_amount": "decimal",
      "status": "ACTIVE|INACTIVE|PENDING",
      "effective_date": "date",
      "end_date": "date",
      "created_at": "datetime",
      "who_created": "uuid"
    }
  ],
  "total": 100,
  "size": 20
}
```

#### GET /enrollments/{id}
**Response DTO:**
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "company_id": "uuid",
  "package_id": "uuid",
  "election_amount": "decimal",
  "contribution_amount": "decimal",
  "status": "ACTIVE|INACTIVE|PENDING",
  "effective_date": "date",
  "end_date": "date",
  "created_at": "datetime",
  "updated_at": "datetime",
  "who_created": "uuid"
}
```

#### GET /users/{id}/enrollments
**Response DTO:**
```json
{
  "enrollments": [
    {
      "id": "uuid",
      "package_id": "uuid",
      "election_amount": "decimal",
      "contribution_amount": "decimal",
      "status": "ACTIVE|INACTIVE|PENDING",
      "effective_date": "date",
      "end_date": "date",
      "package": {
        "id": "uuid",
        "name": "string",
        "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
        "company_id": "uuid",
        "who_created": "uuid"
      }
    }
  ],
  "total": 100,
  "size": 20
}
```

#### GET /companies/{id}/enrollments
**Response DTO:**
```json
{
  "enrollments": [
    {
      "id": "uuid",
      "user_id": "uuid",
      "package_id": "uuid",
      "election_amount": "decimal",
      "contribution_amount": "decimal",
      "status": "ACTIVE|INACTIVE|PENDING",
      "effective_date": "date",
      "end_date": "date",
      "user": {
        "id": "uuid",
        "first_name": "string",
        "last_name": "string",
        "email": "string"
      },
      "package": {
        "id": "uuid",
        "name": "string",
        "payroll_frequency": "WEEKLY|BIWEEKLY|MONTHLY",
        "company_id": "uuid",
        "who_created": "uuid"
      }
    }
  ],
  "total": 100,
  "size": 20
}
```

## Error Responses

All endpoints return consistent error responses:

```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": "object"
  }
}
```
</details>

## Tech stack:
- Java 21
- PostgreSql 16.9
- Gradle 8+ (Groovy DSL)
- Auth0 
- JUnit 5
- GitHub Actions
- Swagger UI
- MapStruct 1.6.3

