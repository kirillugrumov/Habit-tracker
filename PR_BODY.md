## Summary
- Add async business operation API: start returns `taskId`, status endpoint returns execution state/result.
- Add thread-safe counters (`synchronized`, `AtomicLong`) plus an unsafe counter for comparison.
- Add race-condition demo using `ExecutorService` (50+ threads) and show the fix.
- Add JMeter test plan + README to run load tests (async start/poll and race demo).

## Endpoints
- `POST /api/async-operations`
- `GET /api/async-operations/{taskId}`
- `GET /api/concurrency/race-demo?threads=50&tasks=200000&trials=5`

## Test plan
- [ ] Run unit tests: `mvn test`
- [ ] Start app and smoke-test endpoints via Swagger UI (`/swagger-ui.html`)
- [ ] (Optional) Run JMeter non-GUI:
  - `jmeter -n -t .\jmeter\habittracker-async-and-race.jmx -l .\jmeter\results.jtl -e -o .\jmeter\report`

## Notes
- JMeter is not bundled; see `jmeter/README.md` for Windows setup and commands.

