package com.shahd.hospitalmanagementsystem;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AppointmentDialogController implements Initializable {
    @FXML private TextField patientNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> doctorCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> durationCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextArea notesField;
    @FXML private Button closeBtn;

    private AppointmentController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        durationCombo.setItems(FXCollections.observableArrayList("15 minutes","30 minutes","45 minutes","60 minutes"));
        typeCombo.setItems(FXCollections.observableArrayList("Consultation","Follow-up","Procedure"));
        // sensible defaults
        durationCombo.getSelectionModel().select("30 minutes");
        typeCombo.getSelectionModel().select("Consultation");
        // doctorCombo will be filled by parent when dialog is shown
    }

    public void setParent(AppointmentController parent) {
        this.parentController = parent;
        // copy doctors list if available via public accessor
        if (parent != null) {
            doctorCombo.setItems(parent.getDoctorItems());
            if (doctorCombo.getItems() == null || doctorCombo.getItems().isEmpty()) {
                // fallback: load directly from DB
                loadDoctorsFromDB();
            } else {
                doctorCombo.getSelectionModel().selectFirst();
            }
        }
    }

    private void loadDoctorsFromDB() {
        try {
            java.util.List<String> items = new java.util.ArrayList<>();
            String query = "SELECT d.doctor_id, u.full_name, dept.department_name "
                + "FROM doctors d JOIN users u ON d.user_id = u.user_id "
                + "LEFT JOIN departments dept ON d.department_id = dept.department_id "
                + "WHERE d.status = 'active' ORDER BY u.full_name";
            try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String doctorInfo = rs.getString("doctor_id") + " - " + rs.getString("full_name")
                        + " (" + rs.getString("department_name") + ")";
                    items.add(doctorInfo);
                }
            }
            javafx.collections.ObservableList<String> list = FXCollections.observableArrayList(items);
            doctorCombo.setItems(list);
            if (!list.isEmpty()) doctorCombo.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        Stage st = (Stage) ((Button) (closeBtn != null ? closeBtn : new Button())).getScene().getWindow();
        st.close();
    }

    @FXML
    private void onBook() {
        // Basic validation
        if (patientNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter patient name");
            return;
        }
        if (doctorCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a doctor");
            return;
        }
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a date");
            return;
        }
        if (timeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a time");
            return;
        }

        // Generate appointment id and insert (simple approach)
        try {
            String appointmentId = parentController != null ? parentController.generateAppointmentIdPublic() : "APT000000000000";

            // Resolve patient id from name if parent controller has resolver
            String patientId = null;
            try {
                if (parentController != null) patientId = parentController.resolvePatientIdPublic(patientNameField.getText().trim());
            } catch (Exception ex) {
                // ignore
            }
            if (patientId == null) patientId = patientNameField.getText().trim();

            String doctorSelection = doctorCombo.getValue();
            String doctorId = null;
            if (doctorSelection != null && doctorSelection.contains(" - ")) {
                doctorId = doctorSelection.split(" - ")[0];
            }
            if (doctorId == null || doctorId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Selected doctor has no valid ID");
                return;
            }

            String query = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, appointment_type, status, reason) VALUES (?, ?, ?, ?, ?, ?, 'scheduled', ?)";
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, appointmentId);
                ps.setString(2, patientId);
                ps.setString(3, doctorId);
                ps.setString(4, datePicker.getValue().toString());
                ps.setString(5, timeField.getText().trim() + ":00");
                ps.setString(6, typeCombo.getValue() != null ? typeCombo.getValue() : "Consultation");
                ps.setString(7, notesField.getText().trim());
                ps.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment created: " + appointmentId);
            if (parentController != null) parentController.refreshTablePublic();
            // close dialog
            Stage st = (Stage) closeBtn.getScene().getWindow();
            st.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
