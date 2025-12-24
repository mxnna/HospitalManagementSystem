package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Doctor;
import com.shahd.models.Department;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DoctorManagementController implements Initializable {

    @FXML private Button addDoctorBtn, doctorsTabBtn, departmentsTabBtn;
    @FXML private VBox doctorsTab, departmentsTab;
    @FXML private TableView<Doctor> doctorsTable;
    @FXML private TableColumn<Doctor, String> nameColumn, departmentColumn, contactColumn, experienceColumn, statusColumn, actionsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilter, statusFilter;
    @FXML private FlowPane departmentsFlow;

    private ObservableList<Doctor> allDoctors = FXCollections.observableArrayList();
    private ObservableList<Doctor> filteredDoctors = FXCollections.observableArrayList();
    private ObservableList<Department> departments = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadDoctorsFromDatabase();
        loadDepartmentsFromDatabase();
        setupFilters();
        doctorsTable.setItems(filteredDoctors);
        
        // Show doctors tab by default
        switchToDoctorsTab(null);
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        experienceColumn.setCellValueFactory(cellData -> cellData.getValue().experienceProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        // Contact column: show phone and email on two lines
        contactColumn.setCellFactory(col -> new TableCell<Doctor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Doctor d = (Doctor) getTableRow().getItem();
                    Label phone = new Label(d.getPhone());
                    phone.setStyle("-fx-text-fill: #444;");
                    Label email = new Label(d.getEmail());
                    email.setStyle("-fx-text-fill: #888; -fx-font-size: 11;");
                    VBox v = new VBox(4, phone, email);
                    setGraphic(v);
                }
            }
        });
        
        // Add action buttons (edit/delete)
        actionsColumn.setCellFactory(col -> new TableCell<Doctor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Button editBtn = new Button("✎");
                    Button deleteBtn = new Button("🗑");
                    editBtn.setStyle("-fx-padding: 5 10 5 10; -fx-font-size: 12;");
                    deleteBtn.setStyle("-fx-padding: 5 10 5 10; -fx-font-size: 12; -fx-text-fill: #d32f2f;");
                    HBox hbox = new HBox(5, editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void loadDoctorsFromDatabase() {
        allDoctors.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT d.doctor_id, u.full_name AS full_name, u.email AS email, u.contact_number AS phone, d.license_number, '' AS specialization, dept.department_name AS department_name, d.qualifications AS qualification, d.years_of_experience AS experience, d.status AS status FROM doctors d LEFT JOIN users u ON d.user_id = u.user_id LEFT JOIN departments dept ON d.department_id = dept.department_id";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    int parsedId = 0;
                    try {
                        String rawId = rs.getString("doctor_id");
                        if (rawId != null) parsedId = Integer.parseInt(rawId);
                    } catch (Exception parseEx) {
                        parsedId = 0;
                    }
                    Doctor doctor = new Doctor(
                        parsedId,
                        rs.getString("full_name") != null ? rs.getString("full_name") : "",
                        rs.getString("email") != null ? rs.getString("email") : "",
                        rs.getString("phone") != null ? rs.getString("phone") : "",
                        rs.getString("license_number") != null ? rs.getString("license_number") : "",
                        rs.getString("specialization") != null ? rs.getString("specialization") : "",
                        rs.getString("department_name") != null ? rs.getString("department_name") : "Unknown",
                        rs.getString("qualification") != null ? rs.getString("qualification") : "",
                        rs.getString("experience") != null ? rs.getString("experience") : "0",
                        rs.getString("status") != null ? rs.getString("status") : "active"
                    );
                    allDoctors.add(doctor);
                }
                rs.close();
                st.close();
                System.out.println("[DoctorManagement] Loaded " + allDoctors.size() + " doctors from database");
            }
        } catch (Exception ex) {
            System.err.println("[DoctorManagement] Error loading doctors: " + ex.getMessage());
            ex.printStackTrace();
        }
        filteredDoctors.setAll(allDoctors);
    }

    private void loadDepartmentsFromDatabase() {
        departments.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT department_id, department_name FROM departments";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    int deptId = rs.getInt("department_id");
                    String deptName = rs.getString("department_name");
                    int doctorCount = (int) allDoctors.stream().filter(d -> d.getDepartment().equals(deptName)).count();
                    departments.add(new Department(deptId, deptName, "", doctorCount));
                }
                rs.close();
                st.close();
                displayDepartmentCards();
            }
        } catch (Exception ex) {
            System.err.println("[DoctorManagement] Error loading departments: " + ex.getMessage());
        }
    }

    private void displayDepartmentCards() {
        departmentsFlow.getChildren().clear();
        for (Department dept : departments) {
            VBox card = createDepartmentCard(dept);
            departmentsFlow.getChildren().add(card);
        }
    }

    private VBox createDepartmentCard(Department dept) {
        VBox card = new VBox(10);
        card.getStyleClass().add("doctor-card");
        card.setMinWidth(280);

        Label deptName = new Label(dept.getName());
        deptName.getStyleClass().add("dept-name");

        Label activeCount = new Label("Active Doctors: " + dept.getActiveDoctorCount());
        activeCount.getStyleClass().add("muted");

        Label totalCount = new Label("Total: " + dept.getActiveDoctorCount());
        totalCount.getStyleClass().add("muted");

        card.getChildren().addAll(deptName, activeCount, totalCount);
        return card;
    }

    private void setupFilters() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        departments.add("All Departments");
        for (Doctor doc : allDoctors) {
            if (!departments.contains(doc.getDepartment())) {
                departments.add(doc.getDepartment());
            }
        }
        departmentFilter.setItems(departments);
        departmentFilter.setValue("All Departments");
        
        ObservableList<String> statuses = FXCollections.observableArrayList("All Status", "active", "inactive");
        statusFilter.setItems(statuses);
        statusFilter.setValue("All Status");
    }

    @FXML
    public void applyFilters(javafx.event.ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        String deptFilter = departmentFilter.getValue();
        String statusFilter = this.statusFilter.getValue();
        
        filteredDoctors.setAll(allDoctors.filtered(doctor -> {
            boolean matchesSearch = doctor.getName().toLowerCase().contains(searchText) ||
                                   doctor.getEmail().toLowerCase().contains(searchText) ||
                                   doctor.getSpecialization().toLowerCase().contains(searchText);
            boolean matchesDept = deptFilter == null || deptFilter.equals("All Departments") || 
                                 doctor.getDepartment().equals(deptFilter);
            boolean matchesStatus = statusFilter == null || statusFilter.equals("All Status") || 
                                   doctor.getStatus().equals(statusFilter);
            return matchesSearch && matchesDept && matchesStatus;
        }));
    }

    @FXML
    public void switchToDoctorsTab(javafx.event.ActionEvent event) {
        doctorsTab.setVisible(true);
        doctorsTab.setManaged(true);
        departmentsTab.setVisible(false);
        departmentsTab.setManaged(false);
        doctorsTabBtn.getStyleClass().removeAll("tab-inactive");
        doctorsTabBtn.getStyleClass().add("tab-active");
        departmentsTabBtn.getStyleClass().removeAll("tab-active");
        departmentsTabBtn.getStyleClass().add("tab-inactive");
    }

    @FXML
    public void switchToDepartmentsTab(javafx.event.ActionEvent event) {
        doctorsTab.setVisible(false);
        doctorsTab.setManaged(false);
        departmentsTab.setVisible(true);
        departmentsTab.setManaged(true);
        doctorsTabBtn.getStyleClass().removeAll("tab-active");
        doctorsTabBtn.getStyleClass().add("tab-inactive");
        departmentsTabBtn.getStyleClass().removeAll("tab-inactive");
        departmentsTabBtn.getStyleClass().add("tab-active");
    }

    @FXML
    public void openAddDoctorDialog(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-doctor-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Doctor");
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(getClass().getResource("doctor.css").toExternalForm());
            } catch (Exception ignore) {
            }
            dialogStage.setScene(scene);
            dialogStage.sizeToScene();
            dialogStage.showAndWait();
            
            // Reload doctors after dialog closes
            loadDoctorsFromDatabase();
            loadDepartmentsFromDatabase();
            applyFilters(null);
        } catch (IOException ex) {
            System.err.println("[DoctorManagement] Error opening add doctor dialog: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
