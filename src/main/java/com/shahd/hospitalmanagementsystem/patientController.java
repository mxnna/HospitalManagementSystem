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
  private TableColumn<Patient, String> visitColumn;

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
          String emergencyName = safeGet(rs, md, "emergency_contact_name", "emergency_contact");
          String emergencyPhone = safeGet(rs, md, "emergency_contact_phone", "emergency_phone");
          String lastVisit = safeGet(rs, md, "last_visit", "last_visit_date");

          Patient p = new Patient(id, first, last, dob, phone, email, insurance, emergencyName, emergencyPhone, lastVisit);
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
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item);
          setStyle("-fx-text-fill: #374151; -fx-font-weight: 500;");
        }
      }
    });

    // Last Visit Column
    visitColumn.setCellValueFactory(new PropertyValueFactory<>("lastVisit"));
    visitColumn.setCellFactory(col -> new TableCell<Patient, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item);
          setStyle("-fx-text-fill: #6b7280;");
        }
      }
    });

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
    TextField insuranceField = new TextField();
    insuranceField.setPromptText("Insurance company name");
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
    form.add(phoneLabel, 1, 2);
    form.add(phoneField, 1, 3);

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

      String newId = "P" + System.currentTimeMillis();
      String dob = dobPicker.getValue() != null ? dobPicker.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
          : "";

      Patient newPatient = new Patient(newId,
          firstNameField.getText(),
          lastNameField.getText(),
          dob,
          phoneField.getText(),
          emailField.getText(),
          insuranceField.getText(),
          emergencyField.getText(),
          "",
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
    String insertWithInsurance = "INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, phone, email, insurance_provider, emergency_contact_name, emergency_contact_phone, last_visit, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)";

    String insertFallback = "INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, phone, email, last_visit, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)";

    try (Connection conn = DatabaseConnection.getConnection()) {
      try (PreparedStatement pstmt = conn.prepareStatement(insertWithInsurance)) {
        pstmt.setString(1, patient.getPatientId());
        pstmt.setString(2, patient.getFirstName());
        pstmt.setString(3, patient.getLastName());
        pstmt.setString(4, patient.getDateOfBirth());
        pstmt.setString(5, patient.getPhone());
        pstmt.setString(6, patient.getEmail());
        pstmt.setString(7, patient.getInsuranceProvider());
        pstmt.setString(8, patient.getEmergencyContactName());
        pstmt.setString(9, patient.getEmergencyContactPhone());
        pstmt.setString(10, patient.getLastVisit());
        pstmt.executeUpdate();
        return;
      } catch (SQLException ex) {
        // If the schema doesn't have insurance columns, try fallback insert
        try (PreparedStatement pstmt2 = conn.prepareStatement(insertFallback)) {
          pstmt2.setString(1, patient.getPatientId());
          pstmt2.setString(2, patient.getFirstName());
          pstmt2.setString(3, patient.getLastName());
          pstmt2.setString(4, patient.getDateOfBirth());
          pstmt2.setString(5, patient.getPhone());
          pstmt2.setString(6, patient.getEmail());
          pstmt2.setString(7, patient.getLastVisit());
          pstmt2.executeUpdate();
          return;
        }
      }
    }
  }

  public void addPatientToLists(Patient patient) {
    patientList.add(patient);
    filteredList.add(patient);
  }

}
