# Assignment 1 — Builder Pattern

A simple Java 17 application for creating drone missions. The project uses no external libraries, Maven, or Gradle. Code explanations, requirements, UML, and before-and-after examples are available in [report.md](report.md).

Repository: https://github.com/zubaniazsalim-afk/asik-1

## Project structure

```text
src/
  DroneMission.java       Mission and nested Builder
  LaunchSite.java         Launch location
  MissionType.java        LOCAL and LONG_RANGE
  MissionPresets.java     BASIC, SAFE, PERFORMANCE
  Main.java               Runs three examples
test/
  DroneMissionTest.java   Automated tests without libraries
docs/
  builder-uml.png         UML class diagram
README.md
report.md
```

## Run in Windows PowerShell

Open a terminal in the project root. You need JDK 17 or later, with `java` and `javac` available on your PATH.

```powershell
$buildPath = Join-Path $env:TEMP 'sdp-builder-check'
New-Item -ItemType Directory -Force -Path $buildPath | Out-Null
javac -encoding UTF-8 -Xlint:all -d $buildPath src/*.java test/*.java
java '-Dfile.encoding=UTF-8' -cp $buildPath Main
java -cp $buildPath DroneMissionTest
```

The `javac` command should finish without errors. The application then prints three missions, and the tests finish with `All 31 tests passed.` Compiled files are stored in a temporary directory to keep the project folder clean. Use a terminal with UTF-8 support to display the emoji correctly.

## Run on Linux or macOS

```sh
buildPath=$(mktemp -d)
javac -encoding UTF-8 -Xlint:all -d "$buildPath" src/*.java test/*.java
java -Dfile.encoding=UTF-8 -cp "$buildPath" Main
java -cp "$buildPath" DroneMissionTest
```

## How to read the code

1. Start with `Main.java`: it creates a launch site and prints three missions.
2. Open `MissionPresets.java` to see readable Builder method chains.
3. Study `DroneMission.java`: its fields, value copying, Builder methods, and checks inside `build()`.
4. Read `DroneMissionTest.java`: each test checks a result or an expected error.
5. Use the defense preparation section in the report.

## Development history

```sh
git log --oneline --reverse
git show 40059d3:src/DroneMission.java
git show 40059d3:src/Main.java
```

The original version using a conventional constructor is preserved in commit `40059d3`. It is not duplicated in a separate folder in the final project.
