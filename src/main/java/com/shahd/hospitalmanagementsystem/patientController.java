/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.sql.*;

import com.shahd.models.Patient;

/**
 *
 * @author Shahd
 */
public class patientController implements Initializable {
  @FXML
  private TextField searchField;

  @FXML
  private TableView<Patient> patientTable;

  @FXML
  private TableColumn<Patient, String> idColumn;

  @FXML
  private TableColumn<Patient, VBox> nameColumn;

  @FXML
  private TableColumn<Patient, VBox> contactColumn;

  @FXML
  private TableColumn<Patient, String> insuranceColumn;

  @FXML
  private TableColumn<Patient, Button> actionsColumn;

  @FXML
  private Button registerButton;

  private ObservableList<Patient> patientList = FXCollections.observableArrayList();
  private ObservableList<Patient> filteredList = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      System.out.println("Initializing patientController...");
      
      // Setup table columns
      setupTableColumns();
      System.out.println("Table columns setup complete");

      // Load data from database
      loadPatientsFromDb();
      System.out.println("Patient data loaded: " + patientList.size() + " records");

      // Bind data to table
      filteredList.setAll(patientList);
      patientTable.setItems(filteredList);
      System.out.println("Table items set");

      // Setup search functionality
      setupSearch();
      System.out.println("Search functionality setup complete");
      
      System.out.println("patientController initialization complete!");
    } catch (Exception e) {
      System.err.println("Error initializing patientController: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void loadPatientsFromDb() {
    patientList.clear();

    // Try different query variations
    String[] queries = {
      "SELECT * FROM patients LIMIT 1000",
      "SELECT * FROM patients WHERE is_active = TRUE LIMIT 1000"
    };
    
    System.out.println("Loading patients from database...");

    for (String query : queries) {
      try (Connection conn = DatabaseConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("Executing query: " + query);
        java.sql.ResultSetMetaData md = rs.getMetaData();
        System.out.println("Found columns: " + md.getColumnCount());
        
        int count = 0;
        while (rs.next()) {
          String id = safeGet(rs, md, "patient_id");
          String first = safeGet(rs, md, "first_name");
          String last = safeGet(rs, md, "last_name");
          String dob = safeGet(rs, md, "date_of_birth", "dob");
          String phone = safeGet(rs, md, "phone");
          String email = safeGet(rs, md, "email");
          String insurance = safeGet(rs, md, "insurance_provider", "insurance");
          // If patient row has no insurance, try to look up from invoices table (billing data)
          if (insurance == null || insurance.trim().isEmpty()) {
            try (PreparedStatement insStmt = conn.prepareStatement(
                "SELECT insurance FROM invoices WHERE patient_id = ? ORDER BY rowid DESC LIMIT 1")) {
              insStmt.setString(1, id);
              try (ResultSet insRs = insStmt.executeQuery()) {
                if (insRs.next()) {
                  String invIns = insRs.getString(1);
                  if (invIns != null && !invIns.trim().isEmpty()) {
                    insurance = invIns;
                  }
                }
              }
            } catch (Exception e) {
              // invoices table may not exist in user's DB; ignore and continue
            }
          }
          String emergencyName = safeGet(rs, md, "emergency_contact_name", "emergency_contact");
          String emergencyPhone = safeGet(rs, md, "emergency_contact_phone", "emergency_phone");
          String lastVisit = safeGet(rs, md, "last_visit", "last_visit_date");
          String gender = safeGet(rs, md, "gender");

          // If insurance is still empty, seed a default value and persist it to the patients table
          if (insurance == null || insurance.trim().isEmpty()) {
            insurance = "Self-pay";
            try (PreparedStatement upd = conn.prepareStatement(
                "UPDATE patients SET insurance_provider = ? WHERE patient_id = ?")) {
              upd.setString(1, insurance);
              upd.setString(2, id);
              upd.executeUpdate();
            } catch (SQLException ex) {
              // ignore failures to update DB (table may be read-only or schema different)
            }
          }

          Patient p = new Patient(id, first, last, dob, phone, email, insurance, emergencyName, emergencyPhone, gender, lastVisit);
          patientList.add(p);
          count++;
        }
        System.out.println("Loaded " + count + " patients from database");
        if (count > 0) {
          break; // Success, exit loop
        }
      } catch (SQLException e) {
        System.err.println("Error with query: " + e.getMessage());
      }
    }
    
    System.out.println("Total patients in list: " + patientList.size());
  }

  private String safeGet(ResultSet rs, java.sql.ResultSetMetaData md, String... names) throws SQLException {
    for (String n : names) {
      try {
        int idx = -1;
        for (int i = 1; i <= md.getColumnCount(); i++) {
          if (md.getColumnLabel(i).equalsIgnoreCase(n) || md.getColumnName(i).equalsIgnoreCase(n)) {
            idx = i; break;
          }
        }
        if (idx != -1) {
          return rs.getString(idx);
        }
      } catch (SQLException ex) {
        // ignore and try next
      }
    }
    return "";
  }

  private void setupTableColumns() {
    // Patient ID Column
    idColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
    idColumn.setCellFactory(col -> new TableCell<Patient, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
          setText(null);
        } else {
          setText(item);
          setStyle("-fx-font-weight: 500; -fx-text-fill: #1f2937;");
        }
      }
    });

    // Name Column - display full name with DOB
    nameColumn.setCellValueFactory(cellData -> {
      Patient p = cellData.getValue();
      return new javafx.beans.property.SimpleObjectProperty<>(p.getNameBox());
    });
    nameColumn.setCellFactory(col -> new TableCell<Patient, VBox>() {
      @Override
      protected void updateItem(VBox item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
        } else {
          setGraphic(item);
        }
      }
    });

    // Contact Column - display phone and email
    contactColumn.setCellValueFactory(cellData -> {
      Patient p = cellData.getValue();
      return new javafx.beans.property.SimpleObjectProperty<>(p.getContactBox());
    });
    contactColumn.setCellFactory(col -> new TableCell<Patient, VBox>() {
      @Override
      protected void updateItem(VBox item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
        } else {
          setGraphic(item);
        }
      }
    });

    // Insurance Column
    insuranceColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceProvider"));
    insuranceColumn.setCellFactory(col -> new TableCell<Patient, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
        } else {
          String out = (item == null || item.trim().isEmpty()) ? "N/A" : item;
          setText(out);
          setStyle("-fx-text-fill: #374151; -fx-font-weight: 500;");
        }
      }
    });

    // Note: Last Visit column removed from UI; keeping data in model but not displayed.

    // Actions Column
    actionsColumn.setCellFactory(col -> new TableCell<Patient, Button>() {
      @Override
      protected void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getIndex() < 0 || getTableView().getItems().size() <= getIndex()) {
          setGraphic(null);
        } else {
          Patient patient = getTableView().getItems().get(getIndex());
          setGraphic(patient.getActionButton());
        }
      }
    });
  }

  private void setupSearch() {
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
      filterPatients(newValue);
    });
  }

  private void filterPatients(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
      filteredList.setAll(patientList);
    } else {
      String lowerCaseFilter = searchText.toLowerCase();
      filteredList.setAll(
          patientList.stream()
              .filter(p -> p.getId().toLowerCase().contains(lowerCaseFilter) ||
                  p.getName().toLowerCase().contains(lowerCaseFilter) ||
                  p.getPhone().contains(searchText) ||
                  p.getEmail().toLowerCase().contains(lowerCaseFilter))
              .toList());
    }
  }

  @FXML
  private void showRegisterDialog() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/patient-dialog.fxml"));
      Parent root = loader.load();

      PatientDialogController controller = loader.getController();
      controller.setParent(this);

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.WINDOW_MODAL);
      if (registerButton != null && registerButton.getScene() != null) {
        dialogStage.initOwner(registerButton.getScene().getWindow());
      }
      dialogStage.setTitle("Register New Patient");
      Scene scene = new Scene(root);
      dialogStage.setScene(scene);
      dialogStage.showAndWait();
    } catch (Exception ex) {
      ex.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to open register dialog: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  private VBox createRegisterDialog(Stage stage) {
    VBox mainBox = new VBox(25);
    mainBox.setPadding(new Insets(30));
    mainBox.setStyle("-fx-background-color: white;");

    // Header
    VBox header = new VBox(8);
    Text title = new Text("Register New Patient");
    title.getStyleClass().add("dialog-header");
    Text subtitle = new Text("Enter patient demographic and contact information");
    subtitle.getStyleClass().add("dialog-subtitle");
    header.getChildren().addAll(title, subtitle);

    // Form Fields
    GridPane form = new GridPane();
    form.setHgap(15);
    form.setVgap(15);

    // First Name
    Label fnLabel = new Label("First Name");
    fnLabel.getStyleClass().add("form-label");
    TextField firstNameField = new TextField();
    firstNameField.setPromptText("Enter first name");
    firstNameField.getStyleClass().add("form-field");

    // Last Name
    Label lnLabel = new Label("Last Name");
    lnLabel.getStyleClass().add("form-label");
    TextField lastNameField = new TextField();
    lastNameField.setPromptText("Enter last name");
    lastNameField.getStyleClass().add("form-field");

    // Date of Birth
    Label dobLabel = new Label("Date of Birth");
    dobLabel.getStyleClass().add("form-label");
    DatePicker dobPicker = new DatePicker();
    dobPicker.setPromptText("mm/dd/yyyy");
    dobPicker.getStyleClass().add("form-field");

    // Gender
    Label genderLabel = new Label("Gender");
    genderLabel.getStyleClass().add("form-label");
    ComboBox<String> genderField = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other", "Undisclosed"));
    genderField.setPromptText("Select gender");
    genderField.getStyleClass().add("form-field");

    // Phone Number
    Label phoneLabel = new Label("Phone Number");
    phoneLabel.getStyleClass().add("form-label");
    TextField phoneField = new TextField();
    phoneField.setPromptText("(555) 123-4567");
    phoneField.getStyleClass().add("form-field");

    // Email
    Label emailLabel = new Label("Email Address");
    emailLabel.getStyleClass().add("form-label");
    TextField emailField = new TextField();
    emailField.setPromptText("patient@email.com");
    emailField.getStyleClass().add("form-field");

    // Insurance
    Label insuranceLabel = new Label("Insurance Provider");
    insuranceLabel.getStyleClass().add("form-label");
    ComboBox<String> insuranceField = new ComboBox<>(FXCollections.observableArrayList(
            "Self-pay", "Medicare", "Medicaid", "Bupa", "Other"));
    insuranceField.setPromptText("Select provider");
    insuranceField.getStyleClass().add("form-field");

    // Emergency Contact
    Label emergencyLabel = new Label("Emergency Contact");
    emergencyLabel.getStyleClass().add("form-label");
    TextField emergencyField = new TextField();
    emergencyField.setPromptText("Name and phone number");
    emergencyField.getStyleClass().add("form-field");

    // Add to grid
    form.add(fnLabel, 0, 0);
    form.add(firstNameField, 0, 1);
    form.add(lnLabel, 1, 0);
    form.add(lastNameField, 1, 1);

    form.add(dobLabel, 0, 2);
    form.add(dobPicker, 0, 3);
    form.add(genderLabel, 1, 2);
    form.add(genderField, 1, 3);
    form.add(phoneLabel, 0, 4);
    form.add(phoneField, 0, 5);

    form.add(emailLabel, 0, 4);
    form.add(emailField, 0, 5);
    GridPane.setColumnSpan(emailField, 2);

    form.add(insuranceLabel, 0, 6);
    form.add(insuranceField, 0, 7);
    GridPane.setColumnSpan(insuranceField, 2);

    form.add(emergencyLabel, 0, 8);
    form.add(emergencyField, 0, 9);
    GridPane.setColumnSpan(emergencyField, 2);

    // Buttons
    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);

    Button cancelBtn = new Button("Cancel");
    cancelBtn.getStyleClass().addAll("dialog-button", "cancel-button");
    cancelBtn.setOnAction(e -> stage.close());

    Button submitBtn = new Button("Register Patient");
    submitBtn.getStyleClass().addAll("dialog-button", "submit-button");
    submitBtn.setOnAction(e -> {
      // Validate fields are not empty
      if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
        return;
      }

      // leave patient ID blank; database or controller will assign automatically
      String newId = "";
      // standardize date to ISO format for database
      String dob = "";
      if (dobPicker.getValue() != null) {
        dob = dobPicker.getValue().toString();
      }
      String genderVal = genderField.getValue() != null ? genderField.getValue() : "";

      Patient newPatient = new Patient(newId,
          firstNameField.getText(),
          lastNameField.getText(),
          dob,
          phoneField.getText(),
          emailField.getText(),
          insuranceField.getValue() != null ? insuranceField.getValue() : "",
          emergencyField.getText(),
          "", // emergency contact phone unknown
          genderVal,
          LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy")));

      // Persist to DB and update lists
      try {
        addPatientToDb(newPatient);
        patientList.add(newPatient);
        filteredList.add(newPatient);
      } catch (SQLException ex) {
        ex.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to register patient: " + ex.getMessage());
        alert.showAndWait();
      }

      stage.close();
    });

    buttonBox.getChildren().addAll(cancelBtn, submitBtn);

    mainBox.getChildren().addAll(header, form, buttonBox);

    return mainBox;
  }

  public void addPatientToDb(Patient patient) throws SQLException {
    boolean hasInsurance = patient.getInsuranceProvider() != null && !patient.getInsuranceProvider().trim().isEmpty();
    boolean hasGender = patient.getGender() != null && !patient.getGender().trim().isEmpty();
    boolean idProvided = patient.getPatientId() != null && !patient.getPatientId().trim().isEmpty();
    boolean autoId = false;

    try (Connection conn = DatabaseConnection.getConnection()) {
      // If no ID provided, check if database has AUTO_INCREMENT
      if (!idProvided) {
        autoId = isPatientIdAutoIncrement(conn);
        // If database doesn't have AUTO_INCREMENT, generate ID manually
        if (!autoId) {
          String gen = generatePatientId();
          patient.patientIdProperty().set(gen);
          idProvided = true;
        }
      }
      autoId = !idProvided && isPatientIdAutoIncrement(conn);
        // check for gender column dynamically
      hasGender = hasGenderColumn(conn) && hasGender;
      boolean hasEmergencyName = hasColumn(conn, "emergency_contact_name");
      boolean hasEmergencyPhone = hasColumn(conn, "emergency_contact_phone");
      String insertSql;
      if (autoId) {
        // database will supply patient_id automatically
        insertSql = "INSERT INTO patients (first_name, last_name, date_of_birth, phone, email" +
            (hasGender ? ", gender" : "") +
            (hasInsurance ? ", insurance_provider" : "") +
            (hasEmergencyName ? ", emergency_contact_name" : "") +
            (hasEmergencyPhone ? ", emergency_contact_phone" : "") +
            ", is_active) VALUES (?, ?, ?, ?, ?" +
            (hasGender ? ", ?" : "") +
            (hasInsurance ? ", ?" : "") +
            (hasEmergencyName ? ", ?" : "") +
            (hasEmergencyPhone ? ", ?" : "") +
            ", TRUE)";
      } else {
        insertSql = "INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, phone, email" +
            (hasGender ? ", gender" : "") +
            (hasInsurance ? ", insurance_provider" : "") +
            (hasEmergencyName ? ", emergency_contact_name" : "") +
            (hasEmergencyPhone ? ", emergency_contact_phone" : "") +
            ", is_active) VALUES (?, ?, ?, ?, ?, ?" +
            (hasGender ? ", ?" : "") +
            (hasInsurance ? ", ?" : "") +
            (hasEmergencyName ? ", ?" : "") +
            (hasEmergencyPhone ? ", ?" : "") +
            ", TRUE)";
      }

      try (PreparedStatement pstmt = conn.prepareStatement(insertSql, autoId ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
        int idx = 1;
        if (!autoId) {
          pstmt.setString(idx++, patient.getPatientId());
        }
        pstmt.setString(idx++, patient.getFirstName());
        pstmt.setString(idx++, patient.getLastName());
        pstmt.setString(idx++, patient.getDateOfBirth());
        pstmt.setString(idx++, patient.getPhone());
        pstmt.setString(idx++, patient.getEmail());
        if (hasGender) {
          pstmt.setString(idx++, patient.getGender());
        }
        if (hasInsurance) {
          pstmt.setString(idx++, patient.getInsuranceProvider());
        }
        if (hasEmergencyName) {
          pstmt.setString(idx++, patient.getEmergencyContactName());
        }
        if (hasEmergencyPhone) {
          pstmt.setString(idx++, patient.getEmergencyContactPhone());
        }
        pstmt.executeUpdate();

        if (autoId) {
          try (ResultSet keys = pstmt.getGeneratedKeys()) {
            if (keys.next()) {
              long generated = keys.getLong(1);
              String assigned = "P" + generated;
              patient.patientIdProperty().set(assigned);
            }
          }
        }
        return;
      } catch (SQLException ex) {
        // if a non-nullable column is missing in schema, drop it and retry
        if ((hasInsurance && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("insurance_provider"))
            || (hasGender && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("gender"))
            || (hasEmergencyName && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("emergency_contact_name"))
            || (hasEmergencyPhone && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("emergency_contact_phone"))) {
          if (hasInsurance && ex.getMessage().toLowerCase().contains("insurance_provider")) {
            hasInsurance = false;
          }
          if (hasGender && ex.getMessage().toLowerCase().contains("gender")) {
            hasGender = false;
          }
          if (hasEmergencyName && ex.getMessage().toLowerCase().contains("emergency_contact_name")) {
            hasEmergencyName = false;
          }
          if (hasEmergencyPhone && ex.getMessage().toLowerCase().contains("emergency_contact_phone")) {
            hasEmergencyPhone = false;
          }
          // rebuild insert SQL without those problematic fields
          if (autoId) {
            insertSql = "INSERT INTO patients (first_name, last_name, date_of_birth, phone, email" +
                (hasGender ? ", gender" : "") +
                (hasInsurance ? ", insurance_provider" : "") +
                (hasEmergencyName ? ", emergency_contact_name" : "") +
                (hasEmergencyPhone ? ", emergency_contact_phone" : "") +
                ", is_active) VALUES (?, ?, ?, ?, ?" +
                (hasGender ? ", ?" : "") +
                (hasInsurance ? ", ?" : "") +
                (hasEmergencyName ? ", ?" : "") +
                (hasEmergencyPhone ? ", ?" : "") +
                ", TRUE)";
          } else {
            insertSql = "INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, phone, email" +
                (hasGender ? ", gender" : "") +
                (hasInsurance ? ", insurance_provider" : "") +
                (hasEmergencyName ? ", emergency_contact_name" : "") +
                (hasEmergencyPhone ? ", emergency_contact_phone" : "") +
                ", is_active) VALUES (?, ?, ?, ?, ?, ?" +
                (hasGender ? ", ?" : "") +
                (hasInsurance ? ", ?" : "") +
                (hasEmergencyName ? ", ?" : "") +
                (hasEmergencyPhone ? ", ?" : "") +
                ", TRUE)";
          }
          // try execute again
          try (PreparedStatement pstmt2 = conn.prepareStatement(insertSql, autoId ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
            int idx2 = 1;
            if (!autoId) pstmt2.setString(idx2++, patient.getPatientId());
            pstmt2.setString(idx2++, patient.getFirstName());
            pstmt2.setString(idx2++, patient.getLastName());
            pstmt2.setString(idx2++, patient.getDateOfBirth());
            pstmt2.setString(idx2++, patient.getPhone());
            pstmt2.setString(idx2++, patient.getEmail());
            if (hasGender) pstmt2.setString(idx2++, patient.getGender());
            if (hasInsurance) {
              pstmt2.setString(idx2++, patient.getInsuranceProvider());
            }
            if (hasEmergencyName) {
              pstmt2.setString(idx2++, patient.getEmergencyContactName());
            }
            if (hasEmergencyPhone) {
              pstmt2.setString(idx2++, patient.getEmergencyContactPhone());
            }
            pstmt2.executeUpdate();
            if (autoId) {
              try (ResultSet keys = pstmt2.getGeneratedKeys()) {
                if (keys.next()) {
                  long generated = keys.getLong(1);
                  String assigned = "P" + generated;
                  patient.patientIdProperty().set(assigned);
                }
              }
            }
            return;
          }
        }
        throw ex;
      }

    } catch (SQLException ex) {
      // fallback to old behaviour: generate our own id and retry
      if (!idProvided) {
        String gen = generatePatientId();
        patient.patientIdProperty().set(gen);
        addPatientToDb(patient);
        return;
      }
      throw ex;
    }
  }

  public void addPatientToLists(Patient patient) {
    patientList.add(patient);
    filteredList.add(patient);
  }

  // Public wrapper for ID generation (still available if DB doesn't auto-generate)
  public String generatePatientIdPublic() throws SQLException {
    return generatePatientId();
  }

  // Detects whether the patient_id column is auto-incrementing
  private boolean isPatientIdAutoIncrement(Connection conn) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();
    try (ResultSet cols = meta.getColumns(conn.getCatalog(), null, "patients", "patient_id")) {
      if (cols.next()) {
        String autoinc = cols.getString("IS_AUTOINCREMENT");
        return "YES".equalsIgnoreCase(autoinc);
      }
    }
    return false;
  }

  // Detects whether the patients table has insurance_provider column
  private boolean hasInsuranceColumn(Connection conn) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();
    try (ResultSet cols = meta.getColumns(conn.getCatalog(), null, "patients", "insurance_provider")) {
      return cols.next();
    }
  }

  private boolean hasGenderColumn(Connection conn) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();
    try (ResultSet cols = meta.getColumns(conn.getCatalog(), null, "patients", "gender")) {
      return cols.next();
    }
  }

  /**
   * Generic helper to detect whether a given column exists in patients table.
   */
  private boolean hasColumn(Connection conn, String columnName) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();
    try (ResultSet cols = meta.getColumns(conn.getCatalog(), null, "patients", columnName)) {
      return cols.next();
    }
  }

  // Generates next patient ID based on existing ones in the database.
  private String generatePatientId() throws SQLException {
    String query = "SELECT patient_id FROM patients ORDER BY CAST(SUBSTRING(patient_id, 2) AS UNSIGNED) DESC LIMIT 1";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
      if (rs.next()) {
        String lastId = rs.getString("patient_id");
        int num = Integer.parseInt(lastId.substring(1)) + 1;
        // produce compact ID without zero padding, to fit small column definitions
        return "P" + num;
      } else {
        return "P1";
      }
    }
  }

}
