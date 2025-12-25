/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Appointment;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Shahd
 */
public class AppointmentController implements Initializable {
  @FXML
  private TableView<Appointment> appointmentTable;
  @FXML
  private Button cancelBtn;
  @FXML
  private Button closeDialogBtn;
  @FXML
  private DatePicker datePicker;
  @FXML
  private TableColumn<Appointment, String> dateTimeColumn;
  @FXML
  private StackPane dialogOverlay;
  @FXML
  private TableColumn<Appointment, String> doctorColumn;
  @FXML
  private ComboBox<String> doctorSpecialtyCombo;
  @FXML
  private TableColumn<Appointment, String> idColumn;
  @FXML
  private Button newAppointmentBtn;
  @FXML
  private TableColumn<Appointment, String> patientColumn;
  @FXML
  private TextField patientField;
  @FXML
  private TextArea reasonField;
  @FXML
  private Button scheduleBtn;
  @FXML
  private TextField searchField;
  @FXML
  private TableColumn<Appointment, String> specialtyColumn;
  @FXML
  private TableColumn<Appointment, String> statusColumn;
  @FXML
  private ComboBox<String> statusFilter;
  @FXML
  private TextField timeField;

  private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      System.out.println("Initializing AppointmentController...");
      
      // Setup table columns
      idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
      System.out.println("idColumn setup complete");

      // Custom patient column: show full name + patient id below it
      patientColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatientName()));
      patientColumn.setCellFactory(col -> new TableCell<Appointment, String>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
            setGraphic(null);
          } else {
            Appointment appt = getTableView().getItems().get(getIndex());
            javafx.scene.layout.VBox v = new javafx.scene.layout.VBox(2);
            javafx.scene.control.Label name = new javafx.scene.control.Label(appt.getPatientName());
            name.getStyleClass().add("patient-name");
            javafx.scene.control.Label id = new javafx.scene.control.Label(appt.getPatientId());
            id.getStyleClass().add("patient-id");
            id.setStyle("-fx-font-size: 11px; -fx-text-fill: #8f9aa6;");
            v.getChildren().addAll(name, id);
            setGraphic(v);
          }
        }
      });

      doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
      specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));

      // Format date/time as MM/dd/yyyy hh:mm a when possible
      dateTimeColumn.setCellValueFactory(cellData -> {
        String date = cellData.getValue().getAppointmentDate();
        String time = cellData.getValue().getAppointmentTime();
        try {
          java.time.LocalDate d = java.time.LocalDate.parse(date);
          java.time.LocalTime t = java.time.LocalTime.parse(time);
          java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
          String formatted = java.time.LocalDateTime.of(d, t).format(fmt);
          return new javafx.beans.property.SimpleStringProperty(formatted);
        } catch (Exception e) {
          return new javafx.beans.property.SimpleStringProperty(date + " " + time);
        }
      });
      statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

      // Hide dialog initially (if present)
      if (dialogOverlay != null) {
        dialogOverlay.setVisible(false);
      }

      // Load doctors into combo box (only if control exists)
      if (doctorSpecialtyCombo != null) {
        loadDoctors();
      }

      // Setup status filter
      setupStatusFilter();

      // Load appointments
      refreshTable();
      System.out.println("Appointments loaded");

      // Setup search functionality
      searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        filterAppointments(newValue);
      });
      
      System.out.println("AppointmentController initialization complete!");
    } catch (Exception e) {
      System.err.println("Error initializing AppointmentController: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  void handleNewAppointment(ActionEvent event) {
    // Load external dialog FXML and show as modal
    try {
      javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/appointment-dialog.fxml"));
      javafx.scene.Parent root = loader.load();
      AppointmentDialogController dialogCtrl = loader.getController();
      dialogCtrl.setParent(this);

      javafx.stage.Stage dialogStage = new javafx.stage.Stage();
      dialogStage.setTitle("Book New Appointment");
      dialogStage.initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
      dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
      dialogStage.setScene(new javafx.scene.Scene(root));
      dialogStage.showAndWait();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Error", "Failed to open appointment dialog: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  void handleCloseDialog(ActionEvent event) {
    if (dialogOverlay != null) {
      dialogOverlay.setVisible(false);
      clearForm();
    }
  }

  @FXML
  void handleScheduleAppointment(ActionEvent event) {
    try {
      // Validate inputs
      if (!validateInputs()) {
        return;
      }

      // Generate appointment ID
      String appointmentId = generateAppointmentId();

      // Get form data
      String patientInput = patientField.getText().trim();
      String patientId = resolvePatientId(patientInput);
      String doctorSelection = doctorSpecialtyCombo.getValue();
      String doctorId = doctorSelection != null ? doctorSelection.split(" - ")[0] : null; // Extract doctor ID
      String appointmentDate = datePicker.getValue().toString();
      String appointmentTime = timeField.getText().trim() + ":00"; // Add seconds
      String reason = reasonField.getText().trim();

      // Insert appointment into database
      String query = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, " +
          "appointment_date, appointment_time, appointment_type, status, reason) " +
          "VALUES (?, ?, ?, ?, ?, 'General Consultation', 'scheduled', ?)";

      try (Connection conn = DatabaseConnection.getConnection();
          PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, appointmentId);
        pstmt.setString(2, patientId);
        pstmt.setString(3, doctorId);
        pstmt.setString(4, appointmentDate);
        pstmt.setString(5, appointmentTime);
        pstmt.setString(6, reason);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
          showAlert(Alert.AlertType.INFORMATION, "Success",
              "Appointment scheduled successfully!\nAppointment ID: " + appointmentId);
          if (dialogOverlay != null) dialogOverlay.setVisible(false);
          clearForm();
          refreshTable();
        }
      }
    } catch (SQLException e) {
      showAlert(Alert.AlertType.ERROR, "Database Error",
          "Failed to schedule appointment: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Error",
          "An error occurred: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private boolean validateInputs() {
    // If popup controls were removed, prevent scheduling via UI until popup is reimplemented
    if (patientField == null || doctorSpecialtyCombo == null || datePicker == null || timeField == null) {
      showAlert(Alert.AlertType.WARNING, "Not Available", "Appointment UI is temporarily removed.");
      return false;
    }
    if (patientField.getText().trim().isEmpty()) {
      showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter Patient ID");
      return false;
    }
    if (doctorSpecialtyCombo.getValue() == null) {
      showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a doctor");
      return false;
    }
    if (datePicker.getValue() == null) {
      showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a date");
      return false;
    }
    if (timeField.getText().trim().isEmpty()) {
      showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter time (e.g., 09:30)");
      return false;
    }

    // Validate time format (HH:MM)
    if (!timeField.getText().matches("\\d{2}:\\d{2}")) {
      showAlert(Alert.AlertType.WARNING, "Validation Error",
          "Time must be in HH:MM format (e.g., 09:30)");
      return false;
    }

    // Resolve patient input to an ID (allow entering full name)
    try {
      String resolved = resolvePatientId(patientField.getText().trim());
      if (resolved == null) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
            "Patient not found in database");
        return false;
      }
    } catch (SQLException e) {
      showAlert(Alert.AlertType.ERROR, "Database Error",
          "Error validating patient: " + e.getMessage());
      return false;
    }

    return true;
  }

  private boolean patientExists(String patientId) throws SQLException {
    String query = "SELECT COUNT(*) FROM patients WHERE patient_id = ? AND is_active = TRUE";

    try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setString(1, patientId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        return rs.getInt(1) > 0;
      }
    }
    return false;
  }

  private String generateAppointmentId() throws SQLException {
    String query = "SELECT appointment_id FROM appointments ORDER BY appointment_id DESC LIMIT 1";

    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      if (rs.next()) {
        String lastId = rs.getString("appointment_id");
        // Extract number and increment
        int num = Integer.parseInt(lastId.substring(3)) + 1;
        return String.format("APT%012d", num);
      } else {
        return "APT000000000001";
      }
    }
  }

  private void loadDoctors() {
    ObservableList<String> doctors = FXCollections.observableArrayList();

    String query = "SELECT d.doctor_id, u.full_name, dept.department_name " +
        "FROM doctors d " +
        "JOIN users u ON d.user_id = u.user_id " +
        "LEFT JOIN departments dept ON d.department_id = dept.department_id " +
        "WHERE d.status = 'active' " +
        "ORDER BY u.full_name";

    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      while (rs.next()) {
        String doctorInfo = rs.getString("doctor_id") + " - " +
            rs.getString("full_name") +
            " (" + rs.getString("department_name") + ")";
        doctors.add(doctorInfo);
      }

      doctorSpecialtyCombo.setItems(doctors);
    } catch (SQLException e) {
      System.err.println("Error loading doctors: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void setupStatusFilter() {
    ObservableList<String> statuses = FXCollections.observableArrayList(
        "All", "scheduled", "completed", "cancelled");
    statusFilter.setItems(statuses);
    statusFilter.setValue("All");

    statusFilter.setOnAction(event -> {
      filterByStatus(statusFilter.getValue());
    });
  }

  private void filterByStatus(String status) {
    if (status.equals("All")) {
      appointmentTable.setItems(allAppointments);
    } else {
      ObservableList<Appointment> filtered = FXCollections.observableArrayList();
      for (Appointment appointment : allAppointments) {
        if (appointment.getStatus().equals(status)) {
          filtered.add(appointment);
        }
      }
      appointmentTable.setItems(filtered);
    }
  }

  private void filterAppointments(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
      appointmentTable.setItems(allAppointments);
      return;
    }

    ObservableList<Appointment> filtered = FXCollections.observableArrayList();
    String searchLower = searchText.toLowerCase();

    for (Appointment appointment : allAppointments) {
      if (appointment.getAppointmentId().toLowerCase().contains(searchLower) ||
          appointment.getPatientId().toLowerCase().contains(searchLower) ||
          appointment.getPatientName().toLowerCase().contains(searchLower) ||
          appointment.getDoctorName().toLowerCase().contains(searchLower) ||
          appointment.getSpecialty().toLowerCase().contains(searchLower)) {
        filtered.add(appointment);
      }
    }

    appointmentTable.setItems(filtered);
  }

  private ObservableList<Appointment> getAllAppointments() {
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    // Try different query variations
    String[] queries = {
      "SELECT * FROM appointments LIMIT 1000",
      "SELECT a.*, u.full_name as doctor_name FROM appointments a LEFT JOIN doctors d ON a.doctor_id = d.doctor_id LEFT JOIN users u ON d.user_id = u.user_id LIMIT 1000"
    };

    System.out.println("Loading appointments from database...");
    
    for (String query : queries) {
      try (Connection conn = DatabaseConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("Executing query...");
        java.sql.ResultSetMetaData md = rs.getMetaData();
        System.out.println("Found columns: " + md.getColumnCount());

        int count = 0;
        while (rs.next()) {
          try {
            String apptId = rs.getString("appointment_id");
            String patientId = rs.getString("patient_id");
            String patientName = getPatientName(patientId);
            String doctorId = rs.getString("doctor_id");
            String doctorName = getDoctorName(doctorId);
            String appointmentDate = rs.getString("appointment_date");
            String appointmentTime = rs.getString("appointment_time");
            String status = rs.getString("status");
            String reason = rs.getString("reason");
            
            Appointment appointment = new Appointment(
                apptId,
                patientId,
                patientName,
                doctorId,
                doctorName,
                "General", // Default specialty
                appointmentDate,
                appointmentTime,
                status,
                reason);
            appointmentList.add(appointment);
            count++;
          } catch (SQLException ex) {
            System.err.println("Error processing appointment row: " + ex.getMessage());
          }
        }
        System.out.println("Loaded " + count + " appointments from database");
        if (count > 0) {
          break; // Success, exit loop
        }
      } catch (SQLException e) {
        System.err.println("Error loading appointments: " + e.getMessage());
      }
    }

    System.out.println("Total appointments in list: " + appointmentList.size());
    return appointmentList;
  }
  
  // Helper method to get patient name from ID
  private String getPatientName(String patientId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT CONCAT(first_name, ' ', last_name) as full_name FROM patients WHERE patient_id = ?")) {
      pstmt.setString(1, patientId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("full_name");
      }
    } catch (SQLException e) {
      System.err.println("Error getting patient name: " + e.getMessage());
    }
    return patientId;
  }
  
  // Helper method to get doctor name from ID
  private String getDoctorName(String doctorId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement("SELECT doctor_name FROM doctors WHERE doctor_id = ?")) {
      pstmt.setString(1, doctorId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return rs.getString("doctor_name");
      }
    } catch (SQLException e) {
      System.err.println("Error getting doctor name: " + e.getMessage());
    }
    return doctorId;
  }

  // Resolve patient input (ID or full name) to a patient_id, or null if not found
  private String resolvePatientId(String input) throws SQLException {
    if (input == null || input.isEmpty()) return null;

    try (Connection conn = DatabaseConnection.getConnection()) {
      // Try direct patient_id match
      try (PreparedStatement pstmt = conn.prepareStatement("SELECT patient_id FROM patients WHERE patient_id = ?")) {
        pstmt.setString(1, input);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return rs.getString("patient_id");
      }

      // Try exact full name match
      try (PreparedStatement pstmt = conn.prepareStatement("SELECT patient_id FROM patients WHERE CONCAT(first_name, ' ', last_name) = ? LIMIT 1")) {
        pstmt.setString(1, input);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return rs.getString("patient_id");
      }

      // Try LIKE match
      try (PreparedStatement pstmt = conn.prepareStatement("SELECT patient_id FROM patients WHERE CONCAT(first_name, ' ', last_name) LIKE ? LIMIT 1")) {
        pstmt.setString(1, "%" + input + "%");
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) return rs.getString("patient_id");
      }
    }

    return null;
  }

  private void refreshTable() {
    allAppointments = getAllAppointments();
    appointmentTable.setItems(allAppointments);
  }

  // Public wrappers for dialog controller access
  public javafx.collections.ObservableList<String> getDoctorItems() {
    return doctorSpecialtyCombo != null ? doctorSpecialtyCombo.getItems() : FXCollections.observableArrayList();
  }

  public String generateAppointmentIdPublic() throws SQLException {
    return generateAppointmentId();
  }

  public String resolvePatientIdPublic(String input) throws SQLException {
    return resolvePatientId(input);
  }

  public void refreshTablePublic() {
    refreshTable();
  }

  private void clearForm() {
    if (patientField != null) patientField.clear();
    if (doctorSpecialtyCombo != null) doctorSpecialtyCombo.setValue(null);
    if (datePicker != null) datePicker.setValue(null);
    if (timeField != null) timeField.clear();
    if (reasonField != null) reasonField.clear();
  }

  private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
