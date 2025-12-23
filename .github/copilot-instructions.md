# Hospital Management System - AI Agent Instructions

## Project Overview
JavaFX-based hospital management desktop application with MySQL backend. Manages patients, appointments, billing, and staff information through a GUI.

**Tech Stack:**
- **Framework:** JavaFX 21 (desktop GUI)
- **Build:** Maven with javafx-maven-plugin
- **Database:** MySQL (JDBC)
- **Java Version:** 21
- **Testing:** JUnit 5

## Architecture

### Layer Structure
1. **UI Layer:** FXML-based controllers in `src/main/java/com/shahd/hospitalmanagementsystem/`
   - `HospitalManagementSystem.java` - Application entry point, loads FXMLDocument.fxml
   - Controllers: `FXMLDocumentController`, `patientController`, `AppointmentController`, `BillingInController`, `homepageController`, `AboutusController`
   
2. **Model Layer:** `src/main/java/com/shahd/models/`
   - `Patient.java` - Uses JavaFX `StringProperty` for TableView binding
   - `Appointment.java` - Observable properties for reactive UI updates

3. **Database Layer:** `DatabaseConnection.java`
   - Singleton pattern for MySQL connections
   - Hardcoded credentials: root/empty password on localhost:3306/hospital_management
   - Must be configured before deployment

### Data Flow Pattern
Controllers → Models with Properties → UI Binding via PropertyValueFactory → Database via DatabaseConnection

## Key Patterns & Conventions

### JavaFX Property Binding
Model classes use `SimpleStringProperty` wrapped in property accessors:
```java
private StringProperty appointmentId;
public StringProperty appointmentIdProperty() { return appointmentId; }
public String getAppointmentId() { return appointmentId.get(); }
```
Controllers bind these to TableView columns using `PropertyValueFactory("appointmentId")`.

### FXML Injection
Controllers use `@FXML` annotations with matching FXML file names:
- Controller: `patientController.java` → FXML: `patient.fxml`
- CSS styling in separate files: `patient.css`, `appointment.css`, etc.

### Database Access
All database operations use `DatabaseConnection.getConnection()` then direct SQL queries with try-catch blocks. No ORM framework used.

### Controller Initialization
All controllers implement `Initializable` interface with `initialize(URL, ResourceBundle)` method - this is where table population and event binding happens.

## Build & Run Commands

**Run Application:**
```bash
mvn clean javafx:run
```

**Build JAR:**
```bash
mvn clean package
```

**Run Tests:**
```bash
mvn test
```

Main class: `com.shahd.hospitalmanagementsystem.HospitalManagementSystem`

## Critical Developer Workflows

1. **Adding New View:** Create FXML in `src/main/resources/com/shahd/hospitalmanagementsystem/`, create matching Controller, update FXMLDocument.fxml navigation
2. **Database Schema Changes:** Update SQL queries in all affected controllers (no migrations framework)
3. **Model Updates:** Add properties, getters/setters, AND property accessor methods for TableView binding
4. **Styling:** Reference CSS in HospitalManagementSystem.java for main CSS, individual FXML files can add additional stylesheets

## Important Implementation Notes

- **Hardcoded Database Config:** DatabaseConnection.java uses hardcoded credentials. Must be externalized (properties file, environment variables) before production.
- **No Entity Validation:** Models lack input validation - add validation in controllers before database operations.
- **TableView Binding:** Uses `PropertyValueFactory` - column ID must match property name exactly.
- **FXML Navigation:** Controllers manipulate Stage/Scene manually - consider centralizing navigation logic.
- **Resource Management:** No connection pooling - connections created per query. Should use HikariCP or similar for production.

## File Organization Quick Reference

```
src/main/java/com/shahd/
├── hospitalmanagementsystem/        (Controllers + DatabaseConnection)
├── models/                           (Patient.java, Appointment.java)
src/main/resources/com/shahd/hospitalmanagementsystem/
└── *.fxml + *.css                   (UI Definitions + Styling)
pom.xml                              (Maven config, JavaFX plugin)
```

## Common Tasks for AI Agents

- **Reading Controllers:** Start with FXMLDocumentController.java for navigation flow, then specific controllers
- **Database Issues:** Check DatabaseConnection.java credentials and database_name in JDBC URL
- **UI Not Updating:** Verify TableView columns use PropertyValueFactory with correct property names
- **Dependencies:** Check pom.xml for mysql-connector-j version (8.3.0) and javafx-* dependencies
