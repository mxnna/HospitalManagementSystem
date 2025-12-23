<#
PowerShell helper to run the JavaFX app for this project on Windows.
Usage: .\run-app.ps1
#>

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

function Find-MvnExecutable {
  # 1) mvnw wrapper in project
  $mvnw = Join-Path $projectRoot 'mvnw.cmd'
  if (Test-Path $mvnw) { return $mvnw }

  # 2) mvn on PATH
  $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
  if ($mvnCmd) { return 'mvn' }

  # 3) search common Program Files locations (quick shallow search)
  $candidates = @(
    "${env:ProgramFiles}\apache-maven*\bin\mvn.cmd",
    "${env:ProgramFiles}\Apache\apache-maven*\bin\mvn.cmd",
    "${env:ProgramFiles(X86)}\apache-maven*\bin\mvn.cmd",
    "${env:USERPROFILE}\\apache-maven*\\bin\\mvn.cmd"
  )

  foreach ($pattern in $candidates) {
    try {
      $files = Get-ChildItem -Path $pattern -ErrorAction SilentlyContinue
      if ($files -and $files.Length -gt 0) { return $files[0].FullName }
    } catch {
      # ignore
    }
  }

  return $null
}

$mvn = Find-MvnExecutable
if (-not $mvn) {
  Write-Host "Maven (mvn) was not found on PATH nor was a mvnw wrapper detected." -ForegroundColor Yellow
  Write-Host "Options:\n  1) Install Apache Maven and add it to PATH (https://maven.apache.org/download.cgi)\n  2) Use your IDE (IntelliJ/Eclipse/NetBeans) to run the 'HospitalManagementSystem' main class\n  3) If you have mvn installed but not on PATH, set the full path to mvn in the script or run: \n     & 'C:\\path\\to\\mvn.cmd' clean javafx:run" -ForegroundColor Cyan
  exit 1
}

Write-Host "Using Maven executable: $mvn" -ForegroundColor Green

try {
  if ($mvn -eq 'mvn') {
    & mvn clean javafx:run
  } else {
    & "$mvn" clean javafx:run
  }
} catch {
  Write-Host "Failed to start the app: $_" -ForegroundColor Red
  exit 1
}
