<#
Simple local build + smoke test helper
Usage: Open PowerShell in project root and run
  ./build.ps1
Requires: Java JDK (javac/java) on PATH
#>

$out = "out"
if (!(Test-Path $out)) { New-Item -ItemType Directory $out | Out-Null }

Write-Host "Compiling sources..."
javac -d $out src\main\*.java

if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed" ; exit 1 }

Write-Host "Running smoke tests..."
java -cp $out main.TestRunner
