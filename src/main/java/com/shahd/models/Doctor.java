package com.shahd.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Doctor {
    private final IntegerProperty doctorId;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty licenseNumber;
    private final StringProperty specialization;
    private final StringProperty department;
    private final StringProperty qualification;
    private final StringProperty experience;
    private final StringProperty status;

    public Doctor(int doctorId, String name, String email, String phone, String licenseNumber, 
                  String specialization, String department, String qualification, String experience, String status) {
        this.doctorId = new SimpleIntegerProperty(doctorId);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.licenseNumber = new SimpleStringProperty(licenseNumber);
        this.specialization = new SimpleStringProperty(specialization);
        this.department = new SimpleStringProperty(department);
        this.qualification = new SimpleStringProperty(qualification);
        this.experience = new SimpleStringProperty(experience);
        this.status = new SimpleStringProperty(status);
    }

    // Properties for TableView binding
    public IntegerProperty doctorIdProperty() { return doctorId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty licenseNumberProperty() { return licenseNumber; }
    public StringProperty specializationProperty() { return specialization; }
    public StringProperty departmentProperty() { return department; }
    public StringProperty qualificationProperty() { return qualification; }
    public StringProperty experienceProperty() { return experience; }
    public StringProperty statusProperty() { return status; }

    // Getters
    public int getDoctorId() { return doctorId.get(); }
    public String getName() { return name.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }
    public String getLicenseNumber() { return licenseNumber.get(); }
    public String getSpecialization() { return specialization.get(); }
    public String getDepartment() { return department.get(); }
    public String getQualification() { return qualification.get(); }
    public String getExperience() { return experience.get(); }
    public String getStatus() { return status.get(); }

    // Setters
    public void setDoctorId(int doctorId) { this.doctorId.set(doctorId); }
    public void setName(String name) { this.name.set(name); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber.set(licenseNumber); }
    public void setSpecialization(String specialization) { this.specialization.set(specialization); }
    public void setDepartment(String department) { this.department.set(department); }
    public void setQualification(String qualification) { this.qualification.set(qualification); }
    public void setExperience(String experience) { this.experience.set(experience); }
    public void setStatus(String status) { this.status.set(status); }
}
