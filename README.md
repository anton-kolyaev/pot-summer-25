# pot-summer-25
## Authors:
- Edgar Miloš
- Kasparas Murmokas
- Mykyta Tishkin
- Mark Andžejevski
- Agilė Astapovičiūtė  

<details><summary> API Draft</summary>

# Admin Portal API List

## Authentication
```cs
POST   /auth/login          # Admin login
POST   /auth/logout         # Admin logout
POST   /auth/refresh        # Refresh JWT token
```

## Company Management
```cs
GET    /companies                    # Get all companies / Search by name, country code, status, etc.
POST   /companies                    # Create new company profile
GET    /companies/{id}               # View company details
PUT    /companies/{id}               # Update company profile
DELETE /companies/{id}               # Deactivate company, here can be PATCH instead of DELETE
GET    /companies/{id}/users         # View users for a specific company
```

## User Management
```cs
GET    /users                        # Get all users / Search by name, email, etc.
POST   /users                        # Create new user (company-level or consumer-level)
GET    /users/{id}                   # View user details
PUT    /users/{id}                   # Update user profile
DELETE /users/{id}                   # Deactivate user, here can be PATCH instead of DELETE
GET    /users/{id}/roles             # View user roles
PUT    /users/{id}/roles             # Update user roles/functions
```

## Insurance Package Management 
```cs
GET    /packages                     # Get all packages / Search packages by name, dates, status, etc.
POST   /packages                     # Create new insurance package
GET    /packages/{id}                # View package details
PUT    /packages/{id}                # Update package (if not active)
DELETE /packages/{id}                # Deactivate package, here can be PATCH instead of DELETE
GET    /companies/{id}/packages      # View packages for a specific company
```

## Claims Management
```cs
GET    /claims                       # Get all claims / Search claims by claim number, user, company, etc.
GET    /claims/{id}                  # View claim details
PUT    /claims/{id}/approve          # Approve claim
PUT    /claims/{id}/deny             # Deny claim
PUT    /claims/{id}                  # Update claim information
GET    /users/{id}/claims            # View claims for a specific user
GET    /companies/{id}/claims        # View claims for a specific company
```

## Benefit Package Management
```cs
GET    /benefits                     # Get all benefits / Search benefit packages
POST   /benefits                     # Create benefit package
GET    /benefits/{id}                # View benefit package details
PUT    /benefits/{id}                # Update benefit package
DELETE /benefits/{id}                # Delete benefit package, here can be PATCH instead of DELETE
GET    /packages/{id}/benefits       # View benefits for a specific package
```

## Enrollment Management
```cs
GET    /enrollments                  # Get all enrollments / Search enrollments (admin can view all)
GET    /enrollments/{id}             # View enrollment details
GET    /users/{id}/enrollments       # View enrollments for a specific user
GET    /companies/{id}/enrollments   # View enrollments for a specific company
```
</details>

## Tech stack:
- Java 21