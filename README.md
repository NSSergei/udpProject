# Study Group UDP Service

A Java client/server application for managing study groups. The client sends XML requests over UDP, the server validates commands and persists group and user data in PostgreSQL.

## Contents

- [Architecture](#architecture)
- [Requirements](#requirements)
- [Run the application](#run-the-application)
- [Configuration](#configuration)
- [Commands](#commands)
- [Tests](#tests)
- [Metrics and monitoring](#metrics-and-monitoring)
- [Load test](#load-test)
- [Measured results](#measured-results)
- [Known limitations and optimization plan](#known-limitations-and-optimization-plan)

## Architecture

The Maven reactor contains three modules:

| Module | Responsibility |
| --- | --- |
| `common` | XML DTOs, domain models, enums, and validation |
| `client` | Interactive terminal client and UDP request handling |
| `server` | UDP listener, command dispatch, authentication, and PostgreSQL repositories |

Requests and responses are XML encoded as UTF-8 datagrams. PostgreSQL stores users, groups, administrators, coordinates, and locations. Server work is dispatched to a fixed worker pool with a bounded queue. When the queue is full, the receiver applies backpressure by processing the request itself.

## Requirements

- Java 21 or newer
- Maven 3.9 or newer
- Docker with the Compose plugin, or a PostgreSQL 16 instance
- Python 3.9 or newer for the UDP load-test script

The project was built with Java 24 using Java 21 release compatibility. The load-test environment was Windows 10, an Intel Core i7-11700K (16 logical processors), 32 GiB RAM, and PostgreSQL 16 in Docker.

## Run the application

Start PostgreSQL:

```powershell
docker compose up -d pg
```

Build and run the server:

```powershell
mvn clean package
java -jar server/target/server-1.0-SNAPSHOT.jar
```

Run the client in another terminal:

```powershell
java -jar client/target/client-1.0-SNAPSHOT.jar
```

Both executable JARs include their runtime dependencies.

The default UDP port is `12345`; the default database is `studs` on `localhost:5432`. The server creates the required tables and indexes at startup. To use a different local database port, set `DB_PORT` before starting the server. The `docker-compose.yml` host port can be changed with the same variable.

## Configuration

All database and UDP settings can be overridden with environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `12345` | UDP server port |
| `DB_URL` | Constructed from the following database settings | Full JDBC URL override |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `studs` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |

For example, in PowerShell:

```powershell
$env:DB_PORT = "55432"
$env:DB_PASSWORD = "replace-this-for-a-shared-environment"
$env:SERVER_PORT = "12346"
java -jar server/target/server-1.0-SNAPSHOT.jar
```

The default credentials are intended for local development. Set a non-default password for any shared environment.

## Commands

The terminal client supports these commands:

| Command | Description |
| --- | --- |
| `register` | Create a user account |
| `login` | Sign in and select the current user |
| `logout` | Clear the current client login |
| `help` | Print command help |
| `info` | Show storage type, initialization date, and the current user's group count |
| `show` | List the current user's groups |
| `add` | Create a group |
| `update <id>` | Update a group owned by the current user |
| `remove_by_id <id>` | Delete a group by ID |
| `remove_first` | Delete the current user's group with the smallest ID |
| `remove_greater <id>` | Delete the current user's groups whose IDs exceed the given ID |
| `clear` | Confirm and delete all groups owned by the current user |
| `head` | Show the first group by ID |
| `average_of_average_mark` | Show the mean group average mark |
| `filter_contains_name <text>` | Find group names containing text |
| `filter_greater_than_average_mark <value>` | Find groups above the given mark |
| `execute_script <path>` | Read commands and group fields from a file |
| `exit` | Exit the client |

Registration requires a login of at least six characters and a password of at least eight characters. Group input is validated by the server before it is written.

## Tests

Run all module tests from the repository root:

```powershell
mvn test
```

The current suite contains 11 tests covering client input parsing, group validation boundaries, authentication command behavior, duplicate-registration handling at the command layer, salted password hashing, legacy password verification, and rejection of malformed requests. Tests do not require a running PostgreSQL instance.

## Metrics and monitoring

Every ten seconds the server writes a metrics log entry with:

- Total UDP packets received
- Completed and failed requests, including counts for the last reporting interval
- Mean request processing time
- Active worker count and bounded queue depth

The stress-test script records request throughput, success and error counts, error rate, mean latency, p50, p95, p99, and maximum latency. JSON results are stored in `reports/` so later runs can be compared.

For JVM profiling, start the server with Java Flight Recorder enabled:

```powershell
java -XX:StartFlightRecording=filename=server.jfr,duration=60s,settings=profile -jar server/target/server-1.0-SNAPSHOT.jar
```

Inspect the recording with Java Mission Control. The application currently reports request and queue metrics through logs; it does not expose a Prometheus or HTTP metrics endpoint.

## Load test

With PostgreSQL and the server running, use a valid user ID and run:

```powershell
python scripts/stress_test.py --host 127.0.0.1 --port 12345 --user-id 1 --requests 5000 --concurrency 128 --output reports/local-run.json
```

The script sends UDP `info` requests and fails the run if any request times out or returns a failure response. Its default socket timeout is three seconds; `--timeout` can change it. This is a local protocol and database smoke-load test, not a capacity guarantee for a production network or a large dataset.

## Measured results

Both measurements ran against a fresh local PostgreSQL 16 Docker container and the `info` command, which performs a user-scoped count query. The server and load generator ran on the same machine. No packet loss or failed responses were observed.

| Requests | Concurrent clients | Throughput | Mean latency | p50 | p95 | p99 | Max | Errors |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1,000 | 32 | 1,466 req/s | 21.40 ms | 17.17 ms | 29.47 ms | 145.14 ms | 159.91 ms | 0 |
| 5,000 | 128 | 1,852 req/s | 67.94 ms | 65.68 ms | 93.44 ms | 111.95 ms | 128.30 ms | 0 |

During the 5,000-request run, the Java process used 3.03 CPU seconds (6.63% average across 16 logical processors); its sampled peak working set was 381.38 MiB. These are short-run Windows process samples, not sustained or container memory limits. See [reports/resource-metrics.json](reports/resource-metrics.json) for the resource sample and the full request results in [reports/stress-test-1000.json](reports/stress-test-1000.json) and [reports/stress-test-5000.json](reports/stress-test-5000.json).

The server log reported a mean processing time of 9.12 ms across the same 5,000 completed requests. Client latency includes the full local UDP round trip and response parsing, so it is higher than the server's processing time.

The measurements describe this workstation, runtime, database placement, and read-only command. They do not measure registration, group writes, cross-machine network latency, or database behavior with a large collection.

## Known limitations and optimization plan

The following items remain as concrete follow-up work:

1. **Replace client-supplied user IDs with server-verified sessions.** Collection commands currently carry a user ID in the UDP request. The server validates that it is present, but a client can substitute another user's ID. Add expiring session tokens or authenticated request signatures before using this protocol outside a trusted local environment.
2. **Use a JDBC connection pool.** The server currently shares one database connection and serializes command execution to keep JDBC operations safe. This limits database concurrency. A bounded connection pool with transaction-scoped connections can improve throughput while preserving consistency.
3. **Finish transaction and cleanup coverage.** Group creation and update are transactional. Delete operations currently remove the group row but can leave unreferenced administrator, coordinate, and location rows. Add transactional cleanup or database cascades after validating existing data.
4. **Add PostgreSQL integration tests.** The current suite is database-independent. Add container-backed tests for schema initialization, CRUD ownership, transaction rollback, nullable fields, and legacy-password migration.
5. **Expand monitoring.** Add latency histograms by command, database timing, JVM heap and garbage-collection metrics, and an export endpoint if the service needs external dashboards or alerting.
6. **Exercise write-heavy and adversarial load.** The recorded stress tests use the read-only `info` operation. Measure add/update traffic, larger result payloads, burst traffic, malformed XML, and packet loss before setting production capacity targets.
7. **Move credentials out of defaults.** Configure unique database credentials through a deployment secret store and enforce network access restrictions for non-local deployments.

Treat these measurements as a baseline for future comparisons. Re-run the same request counts, concurrency, runtime, and database setup after each performance change.
