# Upgrade guide

This guide contains information for upgrading to some versions when there are major changes.

## Upgrade to 0.44.x

This version bumps the underlying testcontainers-java to v2 ([release note](https://github.com/testcontainers/testcontainers-java/releases/tag/2.0.0)).

### Breaking changes

- Dynalite module is removed (`testcontainers-scala-dynalite`), following its removal in testcontainers-java. No replacement.
- Kafka module:
  - `KafkaContainer` now targets the Apache Kafka image (`apache/kafka:latest`) instead of the Confluent one (`confluentinc/cp-kafka`) (as does testcontainers-java). Use `KafkaConfluentContainer` to keep using the Confluent one.
  - `ConfluentKafkaContainer` defaults to tag `8.1.0` (was `7.6.1`).

### Image changes

- `CockroackContainer`: now uses `cockroachdb/cockroach` by default (was `v19.2.11`).
- `Db2Container`: now uses `icr.io/db2_community/db2:latest` by default (was `ibmcom/db2:11.5.0.0a`).
- `MariaDBContainer`: now uses `mariadb:latest` by default (was `mariadb:10.3.6`).
- `MSSQLServerContainer`: now uses `mcr.microsoft.com/mssql/server:latest` by default (was `mcr.microsoft.com/mssql/server:2017-CU12`).
- `MySQLContainer`: now uses `mysql:latest` by default (was `mysql:5.7.34`).

### New

- Add support for specifying Docker image in `DockerComposeContainer` and `ComposeContainer`

### Others

Removed several deprecated (or removed from the underlying testcontainers-java library) methods/attributes.

- `CassandraContainer`: `jmxReporting` is removed.
- `OrientDBContainer`: `testQueryString`, `orientDB`, and `session` are removed.
- `RabbitMQContainer`: `queues`, `exchanges`, `bindings`, `users`, `vhosts`, `vhostsLimits`, `operatorPolicies`, `policies`, `parameters`, `permissions`, `pluginsEnabled`, `ssl` are removed.
- `ToxiproxyContainer`: `proxy` is removed.
- `DockerComposeContainer`: `localCompose` is removed.
- `Container` and `MultipleContainers`: `finished`, `succeeded`, `starting` and `failed` are removed in favour of `stop`, `start` or optionally `afterTest`.
- Transitive dependency to JUnit 4 removed in Scalatest, munit and specs2 modules.
