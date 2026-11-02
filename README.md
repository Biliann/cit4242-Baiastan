
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

Use these commands in Bash or macOS Terminal. 
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
