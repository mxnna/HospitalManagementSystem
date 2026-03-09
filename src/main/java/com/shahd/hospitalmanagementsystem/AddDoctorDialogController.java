package com.shahd.hospitalmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AddDoctorDialogController implements Initializable {

    @FXML private TextField fullNameField, emailField, phoneField, licenseField, qualificationField;
    @FXML private ComboBox<String> specializationCombo, departmentCombo, statusCombo;
    @FXML private Spinner<Integer> experienceField;
    @FXML private Button cancelBtn, saveBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup experience spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0);
        experienceField.setValueFactory(valueFactory);
        
        // Load specializations
        loadSpecializations();
        
        // Load departments
        loadDepartments();
        
        // Setup status options
        ObservableList<String> statuses = FXCollections.observableArrayList("active", "inactive");
        statusCombo.setItems(statuses);
        statusCombo.setValue("active");
    }

    private void loadSpecializations() {
        ObservableList<String> specializations = FXCollections.observableArrayList(
            "Cardiology",
            "Dermatology",
            "Neurology",
            "Orthopedics",
            "Pediatrics",
            "Psychiatry",
            "Radiology",
            "Surgery",
            "Internal Medicine",
            "General Practice"
        );
        specializationCombo.setItems(specializations);
    }

    private void loadDepartments() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT department_name FROM departments";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    departments.add(rs.getString("department_name"));
                }
                rs.close();
                st.close();
            }
        } catch (Exception ex) {
            System.err.println("[AddDoctorDialog] Error loading departments: " + ex.getMessage());
        }
        departmentCombo.setItems(departments);
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSave() {
        if (validateInputs()) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null) {
                    String fullName = fullNameField.getText();
                    String email = emailField.getText();
                    String phone = phoneField.getText();
                    String license = licenseField.getText();
                    String specialization = specializationCombo.getValue();
                    String department = departmentCombo.getValue();
                    String qualification = qualificationField.getText();
                    int experience = experienceField.getValue();
                    String status = statusCombo.getValue();

                    // Get department ID
                    int departmentId = getDepartmentId(conn, department);

                    // First, create a user record
                    String userId = generateUserId(conn);
                    String userSql = "INSERT INTO users (user_id, username, password, role, full_name, contact_number, email, is_active) " +
                                    "VALUES ('" + userId + "', '" + email + "', 'password123', 'doctor', '" + fullName.replace("'", "\\'") + "', '" + phone + "', '" + email + "', 1)";
                    
                    Statement st = conn.createStatement();
                    st.executeUpdate(userSql);
                    st.close();
                    
                    // Now create the doctor record
                    String doctorId = generateDoctorId(conn);
                    String doctorSql = "INSERT INTO doctors (doctor_id, user_id, department_id, license_number, qualifications, years_of_experience, consultation_fee, status) " +
                                      "VALUES ('" + doctorId + "', '" + userId + "', " + departmentId + ", '" + license + "', '" + qualification.replace("'", "\\'") + "', " + experience + ", 0, '" + status + "')";
                    
                    st = conn.createStatement();
                    st.executeUpdate(doctorSql);
                    st.close();
                    
                    System.out.println("[AddDoctorDialog] Doctor added successfully");
                    
                    // Close dialog
                    Stage stage = (Stage) saveBtn.getScene().getWindow();
                    stage.close();
                }
            } catch (Exception ex) {
                showError("Error saving doctor: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Please enter full name");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Please enter email");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Please enter phone number");
            return false;
        }
        if (licenseField.getText().trim().isEmpty()) {
            showError("Please enter license number");
            return false;
        }
        if (specializationCombo.getValue() == null) {
            showError("Please select specialization");
            return false;
        }
        if (departmentCombo.getValue() == null) {
            showError("Please select department");
            return false;
        }
        return true;
    }

    private int getDepartmentId(Connection conn, String departmentName) {
        try {
            Statement st = conn.createStatement();
            String sql = "SELECT department_id FROM departments WHERE department_name = '" + departmentName + "'";
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int id = rs.getInt("department_id");
                rs.close();
                st.close();
                return id;
            }
            rs.close();
            st.close();
        } catch (Exception ex) {
            System.err.println("[AddDoctorDialog] Error getting department ID: " + ex.getMessage());
        }
        return 1; // Default department ID
    }

    private String generateUserId(Connection conn) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(CAST(SUBSTRING(user_id, 2) AS UNSIGNED)) as max_id FROM users WHERE user_id LIKE 'U%'");
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                rs.close();
                st.close();
                return "U" + (maxId + 1);
            }
            rs.close();
            st.close();
        } catch (Exception ex) {
            System.err.println("[AddDoctorDialog] Error generating user ID: " + ex.getMessage());
        }
        return "U1";
    }

    private String generateDoctorId(Connection conn) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(CAST(SUBSTRING(doctor_id, 2) AS UNSIGNED)) as max_id FROM doctors WHERE doctor_id LIKE 'D%'");
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                rs.close();
                st.close();
                return "D" + (maxId + 1);
            }
            rs.close();
            st.close();
        } catch (Exception ex) {
            System.err.println("[AddDoctorDialog] Error generating doctor ID: " + ex.getMessage());
        }
        return "D1";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
