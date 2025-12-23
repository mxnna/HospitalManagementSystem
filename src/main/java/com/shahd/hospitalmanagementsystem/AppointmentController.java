/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Appointment;
import java.sql.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Shahd
 */
public class AppointmentController {
  @FXML
  private TableColumn<Appointment, String> actionsColumn;
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

  @FXML
  public void initialize() {
    // Setup table columns
    idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
    patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
    doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
    specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
    dateTimeColumn.setCellValueFactory(cellData -> {
      String dateTime = cellData.getValue().getAppointmentDate() + " " +
          cellData.getValue().getAppointmentTime();
      return new javafx.beans.property.SimpleStringProperty(dateTime);
    });
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    // Hide dialog initially
    dialogOverlay.setVisible(false);

    // Load doctors into combo box
    loadDoctors();

    // Setup status filter
    setupStatusFilter();

    // Load appointments
    refreshTable();

    // Setup search functionality
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
      filterAppointments(newValue);
    });
  }

  @FXML
  void handleNewAppointment(ActionEvent event) {
    // Show the dialog overlay
    dialogOverlay.setVisible(true);
    clearForm();
  }

  @FXML
  void handleCloseDialog(ActionEvent event) {
    // Hide the dialog overlay
    dialogOverlay.setVisible(false);
    clearForm();
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
      String patientId = patientField.getText().trim();
      String doctorSelection = doctorSpecialtyCombo.getValue();
      String doctorId = doctorSelection.split(" - ")[0]; // Extract doctor ID
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
          dialogOverlay.setVisible(false);
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

    // Check if patient exists
    try {
      if (!patientExists(patientField.getText().trim())) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
            "Patient ID not found in database");
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
          appointment.getDoctorName().toLowerCase().contains(searchLower) ||
          appointment.getSpecialty().toLowerCase().contains(searchLower)) {
        filtered.add(appointment);
      }
    }

    appointmentTable.setItems(filtered);
  }

  private ObservableList<Appointment> getAllAppointments() {
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    String query = "SELECT a.*, u.full_name, COALESCE(dept.department_name, 'General') as department_name " +
        "FROM appointments a " +
        "JOIN doctors d ON a.doctor_id = d.doctor_id " +
        "JOIN users u ON d.user_id = u.user_id " +
        "LEFT JOIN departments dept ON d.department_id = dept.department_id " +
        "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

    try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      while (rs.next()) {
        Appointment appointment = new Appointment(
            rs.getString("appointment_id"),
            rs.getString("patient_id"),
            rs.getString("doctor_id"),
            rs.getString("full_name"),
            rs.getString("department_name"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getString("status"),
            rs.getString("reason"));
        appointmentList.add(appointment);
      }
    } catch (SQLException e) {
      System.err.println("Error loading appointments: " + e.getMessage());
      e.printStackTrace();
    }

    return appointmentList;
  }

  private void refreshTable() {
    allAppointments = getAllAppointments();
    appointmentTable.setItems(allAppointments);
  }

  private void clearForm() {
    patientField.clear();
    doctorSpecialtyCombo.setValue(null);
    datePicker.setValue(null);
    timeField.clear();
    reasonField.clear();
  }

  private void showAlert(Alert.AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
