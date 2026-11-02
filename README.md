
## Run

```sh
docker compose up --build
```

API: `http://localhost:8080/api/products`. The first build needs internet.
Stop with `Ctrl+C`, then `docker compose down`. Products remain in the database volume.
Ports 8080 and 5432 must be free.

Alternatively, with Java 17+ and Maven 3.6.3+ installed:

```sh
docker compose up -d db
mvn clean verify
mvn spring-boot:run
```

Default database: `jdbc:postgresql://localhost:5432/online_store`, user `store`,
password `store`. Override with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` if using
an existing PostgreSQL database. Hibernate creates/updates the table.
These credentials are for local coursework only.

Tests use the configured PostgreSQL database and clear its products. Use a separate
test database if you already have data you need to keep.

## Endpoints and examples

Use these commands in Bash or macOS Terminal. On Windows use Git Bash.
The examples assume a new database, where the first product has ID 1.

Create:

```sh
curl -i -X POST http://localhost:8080/api/products -H 'Content-Type: application/json' -d '{"name":"Test Product","price":29.99,"category":"Test"}'
```

Response: `201 Created`, `Location: http://localhost:8080/api/products/1` and:

```json
{"id":1,"name":"Test Product","description":null,"price":29.99,"category":"Test","inStock":true,"createdAt":"2026-11-02T10:00:00","updatedAt":"2026-11-02T10:00:00"}
```

Actual IDs and timestamps are generated. The object above is called `P` below.

```sh
curl -i http://localhost:8080/api/products
curl -i http://localhost:8080/api/products/1
curl -i http://localhost:8080/api/products/category/Test
curl -i 'http://localhost:8080/api/products/search?name=product'
```

Each returns `200 OK`. GET all, category and search return `[P]` (an array
containing the example object); GET by ID returns `P` itself. An empty list or
unmatched filter returns `[]` with 200.

Replace:

```sh
curl -i -X PUT http://localhost:8080/api/products/1 -H 'Content-Type: application/json' -d '{"name":"Updated Product","price":39.99,"category":"Test","inStock":false}'
```

Response: `200 OK`:

```json
{"id":1,"name":"Updated Product","description":null,"price":39.99,"category":"Test","inStock":false,"createdAt":"2026-11-02T10:00:00","updatedAt":"2026-11-02T10:01:00"}
```

Delete:

```sh
curl -i -X DELETE http://localhost:8080/api/products/1
```

Response: `204 No Content`, no body.

Failure examples:

```sh
curl -i http://localhost:8080/api/products/999999
curl -i -X POST http://localhost:8080/api/products -H 'Content-Type: application/json' -d '{"name":"","price":-10}'
```

The first returns 404 with `{"status":404,"message":"Product 999999 not found","timestamp":"2026-11-02T10:00:00","errors":{}}`.
The second returns 400 with the same four fields, message `Validation failed`,
and `errors` naming `name`, `price` and `category`. PUT validates the same rules.

Verified: 7 integration tests passed against PostgreSQL 16, plus live HTTP checks
for all seven endpoints. Docker configuration is included; Docker itself was not
available in the verification environment.

## Structure and assumptions

`controller` handles HTTP; `service` performs operations and DTO mapping;
`repository` accesses PostgreSQL; `entity` maps the eight database columns;
`dto` contains the three request/response records; `exception` handles errors.
All are under `com.khalel.onlinestore`.

- Create accepts name, optional description, price and category. New products are in stock.
- Update also accepts inStock. PUT replaces the values; omitted description becomes null and omitted/null inStock becomes true.
- Category is an exact match ignoring case; name search is a substring ignoring case. Empty search returns all products. Results are ordered by ID.
- Price supports up to 15 integer and 4 decimal digits; extra digits are rejected to prevent silent database rounding. No currency conversion is performed.
- IDs/timestamps are server-managed; unknown JSON fields are rejected. Timestamps use server local time.
- No authentication, pagination or additional features were requested.

AI assistance was used to prepare this solution. Review and understand the code
before submitting, and follow the course acknowledgement requirements.

Submit by creating a GitHub repository, pushing this folder's contents, and
pasting the repository URL into Moodle.
