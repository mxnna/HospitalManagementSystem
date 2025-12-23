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
public class Appointment {
    private StringProperty appointmentId;
    private StringProperty patientId;
    private StringProperty patientName; // new
    private StringProperty doctorId;
    private StringProperty doctorName;
    private StringProperty specialty;
    private StringProperty appointmentDate;
    private StringProperty appointmentTime;
    private StringProperty status;
    private StringProperty reason;

    public Appointment(String appointmentId, String patientId, String patientName, String doctorId,
                       String doctorName, String specialty, String appointmentDate,
                       String appointmentTime, String status, String reason) {
        this.appointmentId = new SimpleStringProperty(appointmentId);
        this.patientId = new SimpleStringProperty(patientId);
        this.patientName = new SimpleStringProperty(patientName == null ? "" : patientName);
        this.doctorId = new SimpleStringProperty(doctorId);
        this.doctorName = new SimpleStringProperty(doctorName);
        this.specialty = new SimpleStringProperty(specialty);
        this.appointmentDate = new SimpleStringProperty(appointmentDate);
        this.appointmentTime = new SimpleStringProperty(appointmentTime);
        this.status = new SimpleStringProperty(status);
        this.reason = new SimpleStringProperty(reason);
    }

    // Getters
    public String getAppointmentId() { return appointmentId.get(); }
    public String getPatientId() { return patientId.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getDoctorId() { return doctorId.get(); }
    public String getDoctorName() { return doctorName.get(); }
    public String getSpecialty() { return specialty.get(); }
    public String getAppointmentDate() { return appointmentDate.get(); }
    public String getAppointmentTime() { return appointmentTime.get(); }
    public String getStatus() { return status.get(); }
    public String getReason() { return reason.get(); }

    // Property methods
    public StringProperty appointmentIdProperty() { return appointmentId; }
    public StringProperty patientIdProperty() { return patientId; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty doctorNameProperty() { return doctorName; }
    public StringProperty specialtyProperty() { return specialty; }
    public StringProperty appointmentDateProperty() { return appointmentDate; }
    public StringProperty appointmentTimeProperty() { return appointmentTime; }
    public StringProperty statusProperty() { return status; }
    public StringProperty doctorIdProperty() { return doctorId; }
    
    // Convenience property for table display combining date and time
    public StringProperty appointmentDateTimeProperty() {
        return new SimpleStringProperty(getAppointmentDate() + " " + getAppointmentTime());
    }
    
    // Convenience getter for doctor column display
    public String getDoctor() {
        return getDoctorName();
    }
    
    public StringProperty doctorProperty() {
        return doctorNameProperty();
    }
    
}
