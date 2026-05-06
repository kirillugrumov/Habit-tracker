# JMeter load testing

This project includes a ready-to-run JMeter test plan for:

- Async business operation:
  - `POST /api/async-operations` (start task, returns `taskId`)
  - `GET /api/async-operations/{taskId}` (poll status until `SUCCESS`/`FAILED`)
- Concurrency demo endpoint:
  - `GET /api/concurrency/race-demo`

## 1) Prerequisites (Windows)

- Java 17+
- Apache JMeter (recommended 5.6.x)

Install options:

- Download ZIP from official site and unpack.
- Add `JMETER_HOME\\bin` to `PATH` or call `jmeter.bat` directly.

## 2) Run application

Start Spring Boot app, default port is `8080`.

## 3) Run load test (non-GUI)

From repository root:

```powershell
# If jmeter is in PATH
jmeter -n -t .\jmeter\habittracker-async-and-race.jmx -l .\jmeter\results.jtl -e -o .\jmeter\report

# Or explicit jmeter.bat path (example)
& "C:\path\to\apache-jmeter-5.6.3\bin\jmeter.bat" -n -t .\jmeter\habittracker-async-and-race.jmx -l .\jmeter\results.jtl -e -o .\jmeter\report
```

Artifacts:

- `jmeter/results.jtl` – raw results
- `jmeter/report/index.html` – HTML report

## 4) Tuning

Test plan variables (edit in GUI or pass as `-Jname=value`):

- `HOST` (default `localhost`)
- `PORT` (default `8080`)
- `ASYNC_ITERATIONS` (default `300000`)
- `ASYNC_DELAY_MS` (default `0`)
- `RACE_THREADS` (default `50`)
- `RACE_TASKS` (default `200000`)
- `RACE_TRIALS` (default `5`)

Example overriding host/port:

```powershell
jmeter -n -t .\jmeter\habittracker-async-and-race.jmx -l .\jmeter\results.jtl -JHOST=127.0.0.1 -JPORT=8080
```

