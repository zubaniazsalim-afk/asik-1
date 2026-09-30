# Environmental Drone Inspections

Assignment 2: Factory Method and Abstract Factory, implemented in plain Java 17.

The application plans an inspection, interprets sensor samples, and produces an action report. It supports four environments: **Farm, Forest, Coast, and Mountain**. It demonstrates both patterns through working business operations, not only object creation.

[Full report and UML](report.md) · [Assignment 2 branch](https://github.com/zubaniazsalim-afk/asik-1/tree/codex/assignment-2)

## Run on Windows

Use **JDK 17 or newer**. This computer also has Java 8, so the commands below explicitly select JDK 17. Adjust `$jdk` if your JDK is installed elsewhere.

```powershell
$jdk = 'C:\Program Files\Java\jdk-17\bin'
$buildPath = Join-Path $env:TEMP 'sdp-assignment2-check'
New-Item -ItemType Directory -Force -Path $buildPath | Out-Null
& "$jdk\javac.exe" -encoding UTF-8 -Xlint:all -Werror -d $buildPath src/*.java test/*.java
& "$jdk\java.exe" -cp $buildPath Main farm 10 180 400
& "$jdk\java.exe" -cp $buildPath InspectionTest
```

Run these commands from the project root. Continue to the Java commands only if compilation succeeds. Tests should finish with `All 62 tests passed.` Compiler-based tests need the full JDK, not only a JRE. Compiled files and temporary compatibility checks stay outside the repository.

## Run on Linux or macOS

Ensure both `java -version` and `javac -version` report version 17 or newer.

```sh
buildPath=$(mktemp -d)
javac -encoding UTF-8 -Xlint:all -Werror -d "$buildPath" src/*.java test/*.java
java -cp "$buildPath" Main farm 10 180 400
java -cp "$buildPath" InspectionTest
```

## Choose an environment

```text
Main <family> [hectares] [sample] [followUpSample]
Main farm     10 180 400
Main forest   10 700 300
Main coast    10 300 100
Main mountain 10 600 200
```

The first argument selects the factory at runtime. The next arguments specify the surveyed area and two simulated readings, before and after an intervention. Raw readings must be finite numbers between 0 and 1000. Each family converts them to its own measurement unit. Valid area: greater than 0 and at most 100 hectares.

Running `Main` without arguments uses `farm 10 180 400`. Unknown families, invalid numbers, invalid ranges, and too many arguments produce an error and a nonzero exit code.

## Three operations

1. **Survey:** plan a route, calibrate and average samples, and report the required action.
2. **Schedule a follow-up:** when an alert exists, estimate a revisit covering one quarter of the original area. Otherwise, skip the extra flight.
3. **Compare surveys:** compare alert states before and after intervention and report RESOLVED, NEW_ALERT, STILL_ALERT, or CLEAR.

This is an educational console model. It does not control a real drone, read hardware, locate affected coordinates, or make scientifically validated environmental decisions.

## Files

```text
src/
  Main.java                 Entry point and argument parsing
  FactorySelector.java      Runtime family selection
  InspectionService.java    Three business operations
  InspectionFactory.java    Abstract Factory
  SensorCreator.java        Factory Method and inspection workflow
  InspectionFamily.java     Family marker interface
  InspectionData.java       Immutable Plan and Observation records
  RoutePlanner.java         Product interface
  Sensor.java               Product interface
  ReportWriter.java         Product interface
  FarmFactory.java          Factory, marker, and three Farm products
  ForestFactory.java        Factory, marker, and three Forest products
  CoastFactory.java         Factory, marker, and three Coast products
  MountainFactory.java      Fourth family added separately
test/
  InspectionTest.java       62 automated checks
README.md
report.md                   Explanation, UML, evidence for every criterion
```

Each family keeps its small product classes nested in its own file. There are no dependencies, build frameworks, generated class files, or extra diagram files in the repository. GitHub renders the Mermaid UML directly inside the report.

## Suggested reading order

Start with `Main`, then `InspectionService`, `SensorCreator`, `InspectionFactory`, and finally `FarmFactory`. The report explains generics, records, the two factory patterns, and the compile-time compatibility guarantee in simple terms.

## Development history

Assignment 1 remains on `master`. Assignment 2 is on `codex/assignment-2`.

```sh
git log --oneline --reverse 430ab05..HEAD
git show 87322b7:src/Main.java
git diff --name-status 2a0ab98 d258469
git diff 2a0ab98 d258469 -- src/InspectionService.java src/SensorCreator.java src/Main.java
```

The last command produces no changes: the fourth family did not modify the existing business workflow or entry point.
