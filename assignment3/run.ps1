param([string]$JdkPath = 'C:\Program Files\Java\jdk-17')
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force build/classes | Out-Null
    $sources = (Get-ChildItem src/main/java/*.java).FullName
    & "$JdkPath/bin/javac.exe" -encoding UTF-8 -Xlint:all -Werror -d build/classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & "$JdkPath/bin/java.exe" -cp build/classes Main
    if ($LASTEXITCODE -ne 0) { throw 'Demo failed' }
} finally {
    Pop-Location
}
