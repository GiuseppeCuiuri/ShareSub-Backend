# ShareSub Backend

REST API for **ShareSub**, a platform for sharing subscriptions: a user *hosts* a subscription to a product, other users buy a *slot* in it, and the people sharing a subscription can talk in a per-subscription chat.

Built with Spring Boot, Spring Data JPA and PostgreSQL. A Keycloak realm is configured as the token issuer, but JWT validation is not enabled in the security filter chain: see [Known limitations](#known-limitations).

The Angular frontend lives in [marcoleto01/ShareSubDesign](https://github.com/marcoleto01/ShareSubDesign).

## How it works

| Entity | What it represents |
| --- | --- |
| `User` | A person, identified by email, with a money `balance` |
| `Product` | A shareable service: name, description, type, price, `pricePerUser`, `maxUsers`, `renewRate`, `urlImage` |
| `ProductHosted` | A product that a user (`hostedBy`) is hosting: start and last renewal date, number of `subUsers`, whether a slot is still available (`subAvailable`), whether to `continueToHost` |
| `ProductHostedInfo` | The login information of a hosted product |
| `Subscription` | A user's participation in a hosted product, with an auto-`renew` flag |
| `SubPurchase` | The purchase of a product by a buyer: price and time |
| `Chat`, `Message` | The chat of a hosted product and its messages |

Business rules are enforced in the service layer and surfaced as HTTP 400 through dedicated exceptions: no slot available, maximum number of users exceeded, user already subscribed, amount not available, product already existing, and so on.

## API overview

All routes are served on port `8181`.

| Prefix | Endpoints |
| --- | --- |
| `/check` | `GET /status`: health check |
| `/users` | `GET /getAll`, `GET /findByEmail`, `GET /getBalance`, `POST /createUser`, `POST /registerUserByString`, `POST /redeemBalance` |
| `/products` | Catalogue: `POST crea_product`, `PUT /update_product`, `PUT /delete_product`, `PUT /delete_product_by_id`, `GET /getAll`, `GET /getProductPricePerUser`, `POST /updateProductPricePerUser`, and searches under `/search/` (by id, name, type, maximum price and their combinations) |
| `/products` | Hosting: `POST crea_productHosted`, `GET /getAllProductHosted`, `GET /getAllProductHostedInfo`, `PUT /productHostedInfo/changeLoginInfo`, `PUT /changeSubAvailable`, `PUT /productHosted/changeDate`, `PUT /productHosted/changeContinueToHost`, `GET /productHostedInfo/getByUser`, `GET /productHosted/getByProductAssociated`, `PUT /productManaged/` |
| `/products/subscription` | `GET /get`, `GET /getAll`, `GET /subscriptionAlreadyExists`, `POST /addSubscription`, `PUT /renewSubscription`, `PUT /removeSubscription`, `POST /updateAll` |
| `/products/message` | `POST /createMessage`, `POST /deleteMessage`, `GET /getMessagesByProductHosted`, `GET /getMessagesByProductHostedInfo` (plus `GET /products/getAllChats`) |
| `/purchasing` | `POST /create`, `GET /by_buyer`, `GET /getSubPurchase` |

Successful responses are wrapped in a small `ResponseMessage` object (`message` plus `object`), except `/check/status` (a plain string) and `/users/findByEmail` (the bare result).

## Getting started

### Requirements

- JDK 17: `pom.xml` declares `java.version` 14, and the build has been verified with JDK 17.
- PostgreSQL, with an empty database named `ShareSub` on `localhost:5432`
- A Keycloak server on `localhost:8080` with the realm `ProgettoNegozioScarpe`

### Configuration

Non-secret defaults are in `src/main/resources/application.properties`. The database username and password have no default, so set them in the environment. To use a different database, also set `SPRING_DATASOURCE_URL`.

```bash
export SPRING_DATASOURCE_USERNAME=<your user>
export SPRING_DATASOURCE_PASSWORD=<your password>
```

The project sets `spring.jpa.hibernate.ddl-auto=none` and does not ship a schema script. All entities live in the PostgreSQL schema `orders`, which Hibernate does not create, so on a fresh database create it once:

```bash
psql -U <your user> -d ShareSub -c "CREATE SCHEMA orders;"
```

Then, to create the tables from the entities, run the first time with:

```bash
export SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Run

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Check that it is up: `curl http://localhost:8181/check/status`

For the PostgreSQL installation, see [HELP.md](HELP.md).

## Security

- Sessions are stateless and CSRF protection is disabled.
- The Keycloak realm's issuer and JWK set URIs are set in `application.properties`.
- CORS allows the frontend at `http://localhost:4200`.

### Known limitations

This is a student project and not production-ready:

- JWTs are not validated: `SecurityConfiguration` does not enable `oauth2ResourceServer()`, so the Keycloak settings have no effect on requests.
- `/check`, `/users`, `/products` and `/purchasing` are open (`permitAll`); the role checks (`@PreAuthorize`) are commented out.
- The login information of hosted products is stored as plain text.
- The only test is a Spring context-loading test.

## Tech stack

Java 14 · Spring Boot 2.2 · Spring Web · Spring Data JPA · Spring Security · PostgreSQL · Keycloak (configured, not enforced) · Bean Validation · Lombok · Maven

## License

Apache License 2.0: see [LICENSE](LICENSE).
