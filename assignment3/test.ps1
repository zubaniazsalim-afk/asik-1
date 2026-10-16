param([string]$JdkPath = 'C:\Program Files\Java\jdk-17')
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $junit = 'lib/junit-platform-console-standalone-1.10.2.jar'
    if (!(Test-Path $junit)) {
        New-Item -ItemType Directory -Force lib | Out-Null
        Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar' -OutFile $junit
    }
    New-Item -ItemType Directory -Force build/test-classes | Out-Null
    $sources = (Get-ChildItem src/main/java/*.java,src/test/java/*.java).FullName
    & "$JdkPath/bin/javac.exe" -encoding UTF-8 -Xlint:all -Werror -cp $junit -d build/test-classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & "$JdkPath/bin/java.exe" -jar $junit execute --class-path build/test-classes --scan-class-path --disable-ansi-colors
    if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
} finally {
    Pop-Location
}
