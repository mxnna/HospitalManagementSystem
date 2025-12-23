/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.models;

import javafx.beans.property.*;

/**
 *
 * @author Shahd
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

  public Patient(String patientId, String firstName, String lastName,
      String dateOfBirth, String phone, String email,
      String insuranceProvider, String emergencyContactName,
      String emergencyContactPhone) {
    this.patientId = new SimpleStringProperty(patientId);
    this.firstName = new SimpleStringProperty(firstName);
    this.lastName = new SimpleStringProperty(lastName);
    this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
    this.phone = new SimpleStringProperty(phone);
    this.email = new SimpleStringProperty(email);
    this.insuranceProvider = new SimpleStringProperty(insuranceProvider);
    this.emergencyContactName = new SimpleStringProperty(emergencyContactName);
    this.emergencyContactPhone = new SimpleStringProperty(emergencyContactPhone);
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

  public StringProperty insuranceProviderProperty() {
    return insuranceProvider;
  }

}
