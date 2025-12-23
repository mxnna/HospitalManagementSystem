<#
PowerShell helper to run project tests using Maven.
Usage: .\run-tests.ps1
#>

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

function Find-MvnExecutable {
  $mvnw = Join-Path $projectRoot 'mvnw.cmd'
  if (Test-Path $mvnw) { return $mvnw }
  $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
  if ($mvnCmd) { return 'mvn' }

  # Try shallow search in Program Files
  $candidates = @(
    "${env:ProgramFiles}\apache-maven*\bin\mvn.cmd",
    "${env:ProgramFiles(X86)}\apache-maven*\bin\mvn.cmd"
  )
  foreach ($pattern in $candidates) {
    try { $files = Get-ChildItem -Path $pattern -ErrorAction SilentlyContinue; if ($files -and $files.Length -gt 0) { return $files[0].FullName } } catch { }
  }
  return $null
}

$mvn = Find-MvnExecutable
if (-not $mvn) { Write-Host "Maven not found. Please install Maven or use your IDE to run tests." -ForegroundColor Yellow; exit 1 }
Write-Host "Running tests with: $mvn" -ForegroundColor Green
if ($mvn -eq 'mvn') { & mvn test } else { & "$mvn" test }
