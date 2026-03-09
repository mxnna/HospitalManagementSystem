package com.shahd.hospitalmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PatientDialogController implements Initializable {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> insuranceField;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField emergencyField;
    @FXML private Button closeBtn;

    private patientController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // no-op
    }

    public void setParent(patientController parent) {
        this.parentController = parent;
    }

    @FXML
    private void onCancel() {
        Stage st = (Stage) closeBtn.getScene().getWindow();
        st.close();
    }

    @FXML
    private void onRegister() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "First and last name are required.");
            return;
        }

        // do not assign ID manually; let database or controller handle it
        String newId = "";
        // use ISO date (yyyy-MM-dd) for database compatibility
        String dob = "";
        if (dobPicker.getValue() != null) {
            dob = dobPicker.getValue().toString();
        }
        String gender = genderField.getValue() != null ? genderField.getValue() : "";

        com.shahd.models.Patient newPatient = new com.shahd.models.Patient(
            newId,
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            dob,
            phoneField.getText().trim(),
            emailField.getText().trim(),
            insuranceField.getValue() != null ? insuranceField.getValue().trim() : "",
            emergencyField.getText().trim(),
            "", // no separate phone available
            gender,
            java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
        );

        try {
            // Use parent's DB insertion wrapper if available
            if (parentController != null) {
                parentController.addPatientToDb(newPatient);
                parentController.addPatientToLists(newPatient);
            } else {
                // fallback: attempt to insert directly using DatabaseConnection
                patientController temp = new patientController();
                temp.addPatientToDb(newPatient);
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Patient registered: " + newId);
            Stage st = (Stage) closeBtn.getScene().getWindow();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register patient: " + ex.getMessage());
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
