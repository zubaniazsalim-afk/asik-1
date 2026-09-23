# Assignment 1 — Builder Pattern

Простое приложение на Java 17 для создания миссий дрона. В проекте нет внешних библиотек, Maven или Gradle. Объяснение кода, критерии, UML и примеры «до → после» находятся в [report.md](report.md).

Репозиторий: https://github.com/zubaniazsalim-afk/asik-1

## Структура

```text
src/
  DroneMission.java       Миссия и вложенный Builder
  LaunchSite.java         Место старта
  MissionType.java        LOCAL и LONG_RANGE
  MissionPresets.java     BASIC, SAFE, PERFORMANCE
  Main.java               Запуск трёх примеров
test/
  DroneMissionTest.java   Автоматические проверки без библиотек
docs/
  builder-uml.png         UML классов
README.md
report.md
```

## Запуск в Windows PowerShell

Откройте терминал в корне проекта. Нужен JDK 17 или новее, команды `java` и `javac` должны быть доступны.

```powershell
$buildPath = Join-Path $env:TEMP 'sdp-builder-check'
New-Item -ItemType Directory -Force -Path $buildPath | Out-Null
javac -encoding UTF-8 -Xlint:all -d $buildPath src/*.java test/*.java
java '-Dfile.encoding=UTF-8' -cp $buildPath Main
java -cp $buildPath DroneMissionTest
```

После `javac` не должно быть ошибок. Затем приложение выводит три миссии, тесты заканчиваются строкой `All 31 tests passed.` Компилированные файлы сохраняются во временной папке, поэтому папка проекта остаётся чистой. Для корректного отображения эмодзи используйте терминал с поддержкой UTF-8.

## Запуск в Linux или macOS

```sh
buildPath=$(mktemp -d)
javac -encoding UTF-8 -Xlint:all -d "$buildPath" src/*.java test/*.java
java -Dfile.encoding=UTF-8 -cp "$buildPath" Main
java -cp "$buildPath" DroneMissionTest
```

## Как читать код

1. Начните с `Main.java`: он создаёт место старта и выводит три миссии.
2. Откройте `MissionPresets.java`: там видны понятные цепочки Builder.
3. Изучите `DroneMission.java`: поля, копирование значений, методы Builder и проверки в `build()`.
4. Прочитайте `DroneMissionTest.java`: каждый тест проверяет результат или ожидаемую ошибку.
5. Используйте раздел «Подготовка к защите» в отчёте.

## История разработки

```sh
git log --oneline --reverse
git show 40059d3:src/DroneMission.java
git show 40059d3:src/Main.java
```

Исходная версия с обычным конструктором сохранена в коммите `40059d3`. Она не дублируется отдельной папкой в окончательном проекте.
