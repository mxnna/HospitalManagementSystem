/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

  private ObservableList<Patient> patientList;
  private ObservableList<Patient> filteredList;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Initialize patient data
    initializePatientData();

    // Setup table columns
    setupTableColumns();

    // Load data into table
    patientTable.setItems(filteredList);

    // Setup search functionality
    setupSearch();
  }

  private void initializePatientData() {
    patientList = FXCollections.observableArrayList(
        new Patient("P12345", "SJ", "Sarah Johnson", "3/15/1985",
            "(555) 123-4567", "sarah.j@email.com", "Blue Cross PPO", "10/15/2025"),
        new Patient("P12346", "MB", "Mike Brown", "7/22/1990",
            "(555) 234-5678", "mike.b@email.com", "Medicare", "10/10/2025"),
        new Patient("P12347", "ED", "Emily Davis", "11/30/1978",
            "(555) 345-6789", "emily.d@email.com", "Aetna HMO", "10/18/2025"),
        new Patient("P12348", "JW", "James Wilson", "5/8/1965",
            "(555) 456-7890", "james.w@email.com", "United Healthcare", "9/25/2025"),
        new Patient("P12349", "LA", "Lisa Anderson", "1/18/1992",
            "(555) 567-8901", "lisa.a@email.com", "Cigna PPO", "10/19/2025"));

    filteredList = FXCollections.observableArrayList(patientList);
  }

  private void setupTableColumns() {
    // Patient ID Column with Badge
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    idColumn.setCellFactory(col -> new TableCell<Patient, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setGraphic(null);
        } else {
          Patient patient = getTableView().getItems().get(getIndex());
          HBox hbox = new HBox(10);
          hbox.setAlignment(Pos.CENTER_LEFT);

          Label badge = new Label(patient.getInitials());
          badge.getStyleClass().add("avatar-badge");

          Text idText = new Text(item);
          idText.setStyle("-fx-font-weight: 500; -fx-fill: #1f2937;");

          hbox.getChildren().addAll(badge, idText);
          setGraphic(hbox);
        }
      }
    });

    // Name Column
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameBox"));

    // Contact Column
    contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactBox"));

    // Insurance Column
    insuranceColumn.setCellValueFactory(new PropertyValueFactory<>("insurance"));
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
    actionsColumn.setCellValueFactory(new PropertyValueFactory<>("actionButton"));
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
    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle("Register New Patient");

    // Create dialog content
    VBox dialogContent = createRegisterDialog(dialogStage);

    Scene scene = new Scene(dialogContent, 720, 700);
    scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

    dialogStage.setScene(scene);
    dialogStage.showAndWait();
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

      // Add patient to list
      String initials = (firstNameField.getText().substring(0, 1) +
          lastNameField.getText().substring(0, 1)).toUpperCase();
      String newId = "P" + (12349 + patientList.size() + 1);
      String fullName = firstNameField.getText() + " " + lastNameField.getText();
      String dob = dobPicker.getValue() != null ? dobPicker.getValue().format(DateTimeFormatter.ofPattern("M/d/yyyy"))
          : "";

      Patient newPatient = new Patient(newId, initials, fullName, dob,
          phoneField.getText(), emailField.getText(),
          insuranceField.getText(),
          LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy")));

      patientList.add(newPatient);
      filteredList.add(newPatient);

      stage.close();
    });

    buttonBox.getChildren().addAll(cancelBtn, submitBtn);

    mainBox.getChildren().addAll(header, form, buttonBox);

    return mainBox;
  }

  // Patient Model Class
  public static class Patient {
    private String id;
    private String initials;
    private String name;
    private String dob;
    private String phone;
    private String email;
    private String insurance;
    private String lastVisit;

    public Patient(String id, String initials, String name, String dob,
        String phone, String email, String insurance, String lastVisit) {
      this.id = id;
      this.initials = initials;
      this.name = name;
      this.dob = dob;
      this.phone = phone;
      this.email = email;
      this.insurance = insurance;
      this.lastVisit = lastVisit;
    }

    public String getId() {
      return id;
    }

    public String getInitials() {
      return initials;
    }

    public String getName() {
      return name;
    }

    public String getDob() {
      return dob;
    }

    public String getPhone() {
      return phone;
    }

    public String getEmail() {
      return email;
    }

    public String getInsurance() {
      return insurance;
    }

    public String getLastVisit() {
      return lastVisit;
    }

    public VBox getNameBox() {
      VBox box = new VBox(4);
      Text nameText = new Text(name);
      nameText.setStyle("-fx-font-weight: 500; -fx-fill: #1f2937; -fx-font-size: 14px;");
      Text dobText = new Text("DOB: " + dob);
      dobText.setStyle("-fx-fill: #6b7280; -fx-font-size: 13px;");
      box.getChildren().addAll(nameText, dobText);
      return box;
    }

    public VBox getContactBox() {
      VBox box = new VBox(4);
      Text phoneText = new Text(phone);
      phoneText.setStyle("-fx-fill: #1f2937; -fx-font-size: 14px;");
      Text emailText = new Text(email);
      emailText.setStyle("-fx-fill: #6b7280; -fx-font-size: 13px;");
      box.getChildren().addAll(phoneText, emailText);
      return box;
    }

    public Button getActionButton() {
      Button btn = new Button("View Details");
      btn.getStyleClass().add("view-details-button");
      btn.setOnAction(e -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Patient Details");
        alert.setHeaderText(name);
        alert.setContentText("Patient ID: " + id + "\nDOB: " + dob +
            "\nPhone: " + phone + "\nEmail: " + email +
            "\nInsurance: " + insurance + "\nLast Visit: " + lastVisit);
        alert.showAndWait();
      });
      return btn;
    }
  }
}
