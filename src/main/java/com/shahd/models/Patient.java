/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.models;

import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Patient data model with convenience UI methods for TableView cells
 */
public class Patient {
  private StringProperty patientId;
  private StringProperty firstName;
  private StringProperty lastName;
  private StringProperty dateOfBirth;
  private StringProperty phone;
  private StringProperty email;
  private StringProperty insuranceProvider;
  private StringProperty emergencyContactName;
  private StringProperty emergencyContactPhone;
  private StringProperty gender;
  private StringProperty lastVisit;

  public Patient(String patientId, String firstName, String lastName,
      String dateOfBirth, String phone, String email,
      String insuranceProvider, String emergencyContactName,
      String emergencyContactPhone, String gender, String lastVisit) {
    this.patientId = new SimpleStringProperty(patientId);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
    this.phone = new SimpleStringProperty(phone);
    this.email = new SimpleStringProperty(email);
    this.insuranceProvider = new SimpleStringProperty(insuranceProvider);
    this.emergencyContactName = new SimpleStringProperty(emergencyContactName);
    this.emergencyContactPhone = new SimpleStringProperty(emergencyContactPhone);
    this.gender = new SimpleStringProperty(gender == null ? "" : gender);
    this.lastVisit = new SimpleStringProperty(lastVisit == null ? "" : lastVisit);
  }

  public String getPatientId() {
    return patientId.get();
  }

  public String getFirstName() {
    return firstName.get();
  }

  public String getLastName() {
    return lastName.get();
  }

  public String getDateOfBirth() {
    return dateOfBirth.get();
  }

  public String getPhone() {
    return phone.get();
  }

  public String getEmail() {
    return email.get();
  }

  public String getInsuranceProvider() {
    return insuranceProvider.get();
  }

  public String getEmergencyContactName() {
    return emergencyContactName.get();
  }

  public String getEmergencyContactPhone() {
    return emergencyContactPhone.get();
  }

  public String getGender() {
    return gender.get();
  }

  public String getLastVisit() {
    return lastVisit.get();
  }

  public String getFullName() {
    return getFirstName() + " " + getLastName();
  }

  public String getInitials() {
    String first = getFirstName();
    String last = getLastName();
    return (first.isEmpty() ? "" : String.valueOf(first.charAt(0))) + 
           (last.isEmpty() ? "" : String.valueOf(last.charAt(0)));
  }

  public StringProperty patientIdProperty() {
    return patientId;
  }

  public StringProperty firstNameProperty() {
    return firstName;
  }

  public StringProperty lastNameProperty() {
    return lastName;
  }

  public StringProperty phoneProperty() {
    return phone;
  }

  public StringProperty genderProperty() {
    return gender;
  }

  public StringProperty lastVisitProperty() {
    return lastVisit;
  }
  public StringProperty emailProperty() {
    return email;
  }

  public StringProperty insuranceProviderProperty() {
    return insuranceProvider;
  }

  public StringProperty dateOfBirthProperty() {
    return dateOfBirth;
  }

  public StringProperty fullNameProperty() {
    return new SimpleStringProperty(getFullName());
  }

  public StringProperty insuranceProperty() {
    return insuranceProviderProperty();
  }

  // Convenience getters for filtering
  public String getId() {
    return getPatientId();
  }

  public String getName() {
    return getFullName();
  }

  // Convenience UI helpers used by the TableView cell factories
  public VBox getNameBox() {
    VBox box = new VBox(4);
    Text nameText = new Text(getFullName());
    nameText.setStyle("-fx-font-weight: 500; -fx-fill: #1f2937; -fx-font-size: 14px;");
    Text dobText = new Text("DOB: " + getDateOfBirth());
    dobText.setStyle("-fx-fill: #6b7280; -fx-font-size: 13px;");
    box.getChildren().addAll(nameText, dobText);
    return box;
  }

  public VBox getContactBox() {
    VBox box = new VBox(4);
    Text phoneText = new Text(getPhone());
    phoneText.setStyle("-fx-fill: #1f2937; -fx-font-size: 14px;");
    Text emailText = new Text(getEmail());
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
      alert.setHeaderText(getFullName());
      alert.setContentText("Patient ID: " + getPatientId() + "\nDOB: " + getDateOfBirth() +
          "\nPhone: " + getPhone() + "\nEmail: " + getEmail() +
          "\nInsurance: " + getInsuranceProvider() + "\nLast Visit: " + getLastVisit());
      alert.showAndWait();
    });
    return btn;
  }

}
