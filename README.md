# BQRINES

BQRINES is a Spring Boot web application for managing vehicle and spare-part inventory, sales, users, dashboards, and reports.

The application uses server-side rendering with Thymeleaf, Spring Security for authentication and role-based access, Spring Data JPA for persistence, and PostgreSQL as the database.

## Tech stack

- **Java:** Spring Boot 3.3.0
- **Build tool:** Gradle Wrapper
- **Frontend rendering:** Thymeleaf
- **Security:** Spring Security
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA / Hibernate
- **Development database:** Docker Compose with PostgreSQL 16 Alpine

## Main features

- **Authentication:** Custom login page at `/login`
- **Dashboard:** Role-based dashboard after login at `/dashboard`
- **Inventory:** Vehicle and spare-part management
- **Sales:** Sale registration, stock validation, and voucher page
- **Reports:** Sales reporting for manager users
- **Users:** User management for manager users

## Requirements

Install the following before running the project:

- **Java 17 or newer**
- **Docker and Docker Compose**
- **Git**

You do not need to install Gradle globally because the project includes the Gradle Wrapper.

## Database setup

The project includes a PostgreSQL service in `docker-compose.yml`.

Start the database:

```bash
docker compose up -d
```

This creates a PostgreSQL container with:

```text
Database: bqrines
User: postgres
Password: postgres
Host port: 5433
Container port: 5432
```

The JDBC URL from the host machine is:

```text
jdbc:postgresql://localhost:5433/bqrines
```

## Environment variables

The base configuration in `src/main/resources/application.properties` reads the database connection from these variables:

```text
DB_URL
DB_USER
DB_PASSWORD
```

For the Docker Compose database, use:

```text
DB_URL=jdbc:postgresql://localhost:5433/bqrines
DB_USER=postgres
DB_PASSWORD=postgres
```

## Recommended local configuration

When running locally with Gradle, `build.gradle` activates the `dev` Spring profile automatically:

```text
--spring.profiles.active=dev
```

Create this file:

```text
src/main/resources/application-dev.properties
```

Add the local database values:

```properties
DB_URL=jdbc:postgresql://localhost:5433/bqrines
DB_USER=postgres
DB_PASSWORD=postgres
```

This file is intentionally ignored by Git, so local credentials are not committed.

## Alternative: export environment variables

If you do not want to create `application-dev.properties`, export the variables before running the application:

```bash
export DB_URL=jdbc:postgresql://localhost:5433/bqrines
export DB_USER=postgres
export DB_PASSWORD=postgres
```

Then run the application normally.

## Run the application

Start PostgreSQL:

```bash
docker compose up -d
```

Run the Spring Boot application:

```bash
./gradlew bootRun
```

Open the application:

```text
http://localhost:8080
```

You will be redirected to the login page if you are not authenticated.

## Initial login

On the first run, if there are no users in the database, the application creates an initial manager user:

```text
Email: admin@bqrines.com
Password: admin123
Role: GERENTE
```

Change this password after the first login.

## Build and test

Run tests:

```bash
./gradlew test
```

Build the project:

```bash
./gradlew build
```

Run the generated JAR:

```bash
java -jar build/libs/BQRINES-1.0-SNAPSHOT.jar
```

If you run the JAR directly, make sure the database variables are available in your shell:

```bash
export DB_URL=jdbc:postgresql://localhost:5433/bqrines
export DB_USER=postgres
export DB_PASSWORD=postgres
java -jar build/libs/BQRINES-1.0-SNAPSHOT.jar
```

## Useful commands

Stop the database container:

```bash
docker compose down
```

Stop the database and remove persisted data:

```bash
docker compose down -v
```

View database logs:

```bash
docker compose logs -f postgres
```

## Project structure

```text
src/main/java/org
├── BqrinesApplication.java
├── config
├── controllers
├── exception
├── models
├── repositories
└── services

src/main/resources
├── application.properties
├── static
│   ├── css
│   └── js
└── templates
    ├── auth
    ├── dashboard
    ├── error
    ├── fragments
    ├── inventory
    ├── reports
    ├── sells
    └── users
```

## Troubleshooting

### The application cannot connect to PostgreSQL

Check that the database container is running:

```bash
docker compose ps
```

Check that the JDBC URL uses port `5433` from the host machine:

```text
jdbc:postgresql://localhost:5433/bqrines
```

### `DB_URL` or another database variable is missing

Create `src/main/resources/application-dev.properties` with:

```properties
DB_URL=jdbc:postgresql://localhost:5433/bqrines
DB_USER=postgres
DB_PASSWORD=postgres
```

Or export the variables in your terminal before running the app.

### Login does not work with the default admin user

The default admin user is only created when the `users` table is empty. If the database already contains users, use an existing account or reset the database with:

```bash
docker compose down -v
docker compose up -d
./gradlew bootRun
```
