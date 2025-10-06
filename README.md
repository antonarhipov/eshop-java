# Tea Shop E-commerce Prototype

A Spring Boot e-commerce application for browsing and purchasing tea products with admin management capabilities.

## Overview

The Tea Shop Prototype is a full-featured e-commerce application that enables users to:

- Browse a catalog of teas and teaware with faceted filtering
- Search products with synonym support
- View detailed product information with variant selection
- Manage shopping cart and complete mock checkout
- Admin interface for catalog and order management
- Inventory reservation and release workflow
- VAT and shipping calculations
- Basic analytics and audit logging

This is a prototype application with mock payment processing - no real payment transactions are processed.

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Java Version**: 21
- **Database**: PostgreSQL (with H2 for testing)
- **Build Tool**: Gradle 8.14.3
- **Template Engine**: Thymeleaf
- **Security**: Spring Security
- **Database Migration**: Flyway
- **Testing**: JUnit 5, Spring Boot Test
- **Containerization**: Docker Compose

### Key Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Mail
- Spring Boot Starter Validation
- Flyway Core
- Jackson Kotlin Module
- PostgreSQL Driver

## Requirements

- Java 21 or higher
- Docker and Docker Compose (for database)
- Gradle 8.14.3+ (or use included wrapper)

## Setup and Installation

### 1. Clone the Repository

```bash
git clone [repository-url]
cd eshop
```

### 2. Start the Database

The application uses Docker Compose to run PostgreSQL:

```bash
docker-compose up -d
```

This starts a PostgreSQL database with:
- Database: `mydatabase`
- Username: `myuser`
- Password: `secret`
- Port: `5432`

### 3. Run Database Migrations

```bash
./gradlew flywayMigrate
```

### 4. Start the Application

```bash
./gradlew bootRun
```

The application will be available at: http://localhost:8080

## Available Scripts

### Application Commands

- `./gradlew bootRun` - Run the Spring Boot application
- `./gradlew bootTestRun` - Run with test runtime classpath
- `./gradlew bootJar` - Create executable JAR file
- `./gradlew bootBuildImage` - Build Docker image

### Build Commands

- `./gradlew build` - Build and test the project
- `./gradlew clean` - Clean build directory
- `./gradlew assemble` - Assemble the project without running tests
- `./gradlew classes` - Compile main classes
- `./gradlew jar` - Create JAR archive

### Database Commands

- `./gradlew flywayMigrate` - Run database migrations
- `./gradlew flywayInfo` - Show migration status
- `./gradlew flywayClean` - Drop all database objects
- `./gradlew flywayValidate` - Validate applied migrations
- `./gradlew flywayRepair` - Repair schema history table

#### Repairing schema history (checksum mismatch)
If you see a startup error like "Migration checksum mismatch" or "Validate failed: Migrations have failed validation", run a Flyway REPAIR to update the schema history table.

Quick repair using the helper script:

```bash
./scripts/flyway_repair.sh
```

You can override connection settings via environment variables:

```bash
DB_HOST=localhost DB_PORT=5432 DB_NAME=mydatabase DB_USER=myuser DB_PASSWORD=secret ./scripts/flyway_repair.sh
```

Alternatively, run the Gradle task directly and pass connection details explicitly:

```bash
./gradlew flywayRepair \
  -Dflyway.url=jdbc:postgresql://localhost:5432/mydatabase \
  -Dflyway.user=myuser \
  -Dflyway.password=secret \
  -Dflyway.locations=filesystem:src/main/resources/db/migration
```

When to REPAIR vs RESET:
- Use REPAIR to fix checksums after a migration file was modified but already applied.
- Use the full reset script `./scripts/plan_b_reset_db.sh` only for local development when you can safely drop and recreate the database.

### Testing Commands

- `./gradlew test` - Run all tests
- `./gradlew check` - Run all verification tasks

## Environment Variables

### Development (Default)

The application runs with the `dev` profile by default and uses the Docker Compose database.

### Production Configuration

For production deployment, set these environment variables:

- `DATABASE_URL` - PostgreSQL connection URL (default: `jdbc:postgresql://localhost:5432/eshop_prod`)
- `DATABASE_USERNAME` - Database username (default: `eshop_user`)
- `DATABASE_PASSWORD` - Database password (default: `changeme`)
- `SPRING_PROFILES_ACTIVE` - Set to `prod` for production profile

### Application Configuration

Key configuration options in `application.yml`:

- `shop.vatRate` - VAT rate (default: 0.20 = 20%)
- `shop.shipping.zones` - Shipping cost configuration by zone
- `shop.search.synonyms` - Search synonym mappings

## Testing

The project includes comprehensive tests:

### Test Structure

- `EshopApplicationTests.kt` - Application context loading test
- `ProductServiceTest.kt` - Product service unit tests
- `ShippingCalculatorServiceTest.kt` - Shipping calculation tests
- `CheckoutServiceTest.kt` - Checkout process tests
- `VatCalculatorServiceTest.kt` - VAT calculation tests

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with detailed output
./gradlew test --info

# Run specific test class
./gradlew test --tests "org.example.eshop.service.ProductServiceTest"
```

Tests use H2 in-memory database and separate test configuration in `src/test/resources/application.yml`.

## Project Structure

```
eshop/
├── docs/                           # Project documentation
│   ├── requirements.md            # Detailed requirements
│   ├── tasks.md                   # Development tasks
│   ├── plan.md                    # Project plan
│   └── spec.md                    # Technical specifications
├── src/
│   ├── main/
│   │   ├── java/org/example/eshop/
│   │   │   ├── Application.kt     # Main application entry point
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── controller/        # REST and MVC controllers
│   │   │   ├── dto/               # Data transfer objects
│   │   │   ├── entity/            # JPA entities
│   │   │   ├── repository/        # Data repositories
│   │   │   └── service/           # Business logic services
│   │   └── resources/
│   │       ├── application.yml    # Application configuration
│   │       ├── db/migration/      # Flyway database migrations
│   │       ├── static/            # Static web assets
│   │       └── templates/         # Thymeleaf templates
│   └── test/                      # Test classes
├── build.gradle.kts               # Gradle build configuration
├── compose.yaml                   # Docker Compose configuration
└── README.md                      # This file
```

## API Endpoints

### Public Endpoints

- `GET /` - Home page
- `GET /catalog` - Product catalog with filtering
- `GET /products/{slug}` - Product detail page
- `GET /cart` - Shopping cart
- `POST /api/cart` - Create cart
- `PATCH /api/cart/{id}` - Update cart
- `POST /api/checkout/{cartId}/submit` - Submit order
- `GET /api/orders/{orderNumber}` - Order lookup

### Admin Endpoints (Authentication Required)

- `GET /admin` - Admin dashboard
- `GET /admin/login` - Admin login
- `POST /api/admin/products` - Create product
- `PATCH /api/admin/orders/{id}/mark-paid` - Mark order as paid
- `PATCH /api/admin/orders/{id}/ship` - Mark order as shipped
- `PATCH /api/admin/orders/{id}/cancel` - Cancel order

Default admin credentials:
- Username: `admin`
- Password: `admin123`

## Features

### Customer Features

- **Catalog Browsing**: Filter by type, region, harvest year, price range, stock status
- **Search**: Keyword search with configurable synonyms
- **Product Details**: Variant selection, pricing, stock status, brewing information
- **Shopping Cart**: Add/remove items, quantity management, real-time totals
- **Checkout**: Mock payment processing, order confirmation
- **Order Tracking**: Look up orders by order number

### Admin Features

- **Catalog Management**: Create, edit, delete products, variants, and lots
- **Order Management**: Mark orders as paid, shipped, or canceled
- **Inventory Control**: Stock and reservation management
- **Audit Logging**: Track all administrative actions

### System Features

- **Inventory Management**: Automatic reservation and release workflow
- **Pricing**: VAT-inclusive pricing with configurable rates
- **Shipping**: Zone-based shipping calculation by weight
- **Security**: CSRF protection, role-based access control
- **Analytics**: Basic event tracking for user actions
- **Mobile Ready**: Responsive design for mobile devices

## Development

### Database Schema

The application uses Flyway for database migrations. Schema files are located in `src/main/resources/db/migration/`:

- `V1__Create_initial_schema.sql` - Initial database schema
- `V1__Insert_seed_data.sql` - Sample data for development

### Logging

Application logs are written to:
- Console output (development)
- `logs/eshop.log` (file logging)

Log levels can be configured in `application.yml`.

## License

<!-- TODO: Add license information -->
License information not specified. Please add appropriate license file and update this section.

## Contributing

<!-- TODO: Add contributing guidelines -->
Contributing guidelines not specified. Please add CONTRIBUTING.md file with development workflow and coding standards.

## Support

<!-- TODO: Add support information -->
For support and questions, please refer to the project documentation in the `docs/` directory or contact the development team.

---

**Note**: This is a prototype application for demonstration purposes. Do not use in production without proper security review and real payment gateway integration.


## Schema Baseline Consolidation (2025-10-06)
As of 2025-10-06, all schema DDL has been consolidated into the baseline migration `V0__Baseline_schema.sql`.
Migrations `V1__Create_initial_schema.sql`, `V3__Add_order_contact_fields.sql`, and `V4__Reconcile_orders_contact_columns.sql` have been converted to no‑op placeholders to preserve version ordering.

Actions required on existing databases (any environment that previously applied V1/V3/V4):

1. Run a Flyway REPAIR once to accept new checksums:
   ```bash
   ./scripts/flyway_repair.sh
   ```
   Or via Gradle:
   ```bash
   ./gradlew flywayRepair
   ```
2. Then run migrations normally:
   ```bash
   ./gradlew flywayMigrate
   ```

Fresh installs:
- Simply run `./gradlew flywayMigrate` on an empty database. Flyway will apply the consolidated baseline (V0) and then proceed with data seeds (V2) and any future migrations.

Notes:
- The consolidated baseline uses idempotent `CREATE TABLE/INDEX IF NOT EXISTS` statements, so it is safe even if discovered out‑of‑order on existing databases.
- Application profiles and production validation behavior remain unchanged (prod still validates on migrate).