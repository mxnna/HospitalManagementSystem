# Running the HospitalManagementSystem project (Windows)

Quick guide to run the app and tests on Windows.

Requirements
- Java 21+ (JDK installed and JAVA_HOME set)
- Apache Maven (recommended) or use your IDE's built-in Maven support

Run the app (PowerShell)
- From project root in PowerShell:
  - .\run-app.ps1
  - The script will search for `mvn` or a `mvnw.cmd` wrapper and run `mvn clean javafx:run` when found.
  - If the script reports Maven missing, install it (https://maven.apache.org/download.cgi) and add `bin` to PATH.

Run tests
- From project root in PowerShell:
  - .\run-tests.ps1
  - Or run `mvn test` from a terminal (if mvn is on PATH) or from your IDE.

VS Code Tasks & Launch Config (recommended)
- The repository includes convenient VS Code tasks to run the app or tests using the PowerShell helpers:
  - Open the Command Palette (Ctrl+Shift+P) → `Tasks: Run Task` → choose **Run App (PowerShell)** or **Run Tests (PowerShell)**.
  - These tasks call the scripts with `-ExecutionPolicy Bypass` so they run even if PowerShell's execution policy is restrictive.
  - A lightweight **Maven Compile** task is included and used by the launch configuration as a `preLaunchTask`.
- To run or debug from VS Code's Run view:
  - Open the Run panel (Ctrl+Shift+D) → choose **Launch HospitalManagementSystem** → press the green Run or Debug button.
  - Launch uses the `mainClass` `com.shahd.hospitalmanagementsystem.HospitalManagementSystem` and runs a `Maven Compile` task before launching.
- If any path or file is missing, tasks will report the issue and the scripts assume user-specific files (like `C:\Users\Lenovo\Downloads\Hospital.sql`) may be present on your machine.

If mvn is not on PATH
- Option A: Install Maven and add to PATH. After installation, restart your shell and run `mvn -v` to verify.
- Option B: Use your IDE (IntelliJ, Eclipse, NetBeans) to run the `HospitalManagementSystem` main class or run Maven goals from the Maven tool window.

Common IntelliJ run config (if you use IDEA)
- Main class: `com.shahd.hospitalmanagementsystem.HospitalManagementSystem`
- Use project SDK matching Java 21.
- Add VM options for JavaFX if necessary (IntelliJ usually picks them up via the javafx-maven-plugin when running with Maven).

Notes
- If scripts fail due to path differences, check that your SQL or resource files are at the paths you expect; for any missing or user-specific paths I used `C:\Users\Lenovo\Downloads\Hospital.sql` in tests as you indicated.
- If you'd like, I can add a VS Code task or an extra script to automatically set JAVA_HOME / add mvn to PATH for your environment (requires confirm).