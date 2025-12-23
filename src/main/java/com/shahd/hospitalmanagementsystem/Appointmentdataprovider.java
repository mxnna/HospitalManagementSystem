/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author Shahd
 */
public class Appointmentdataprovider {
    public static ObservableList<Appointment> getSampleAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        appointments.add(new Appointment(
            "APT001", 
            "Sarah Johnson\nP12345", 
            "Dr. Robert Smith", 
            "Cardiology", 
            "10/20/2025\n09:00 AM", 
            "confirmed"
        ));
        
        appointments.add(new Appointment(
            "APT002", 
            "Mike Brown\nP12346", 
            "Dr. Jennifer Lee", 
            "Orthopedics", 
            "10/20/2025\n09:30 AM", 
            "scheduled"
        ));
        
        appointments.add(new Appointment(
            "APT003", 
            "Emily Davis\nP12347", 
            "Dr. Robert Smith", 
            "Cardiology", 
            "10/20/2025\n10:00 AM", 
            "scheduled"
        ));
        
        appointments.add(new Appointment(
            "APT004", 
            "James Wilson\nP12348", 
            "Dr. Priya Patel", 
            "Neurology", 
            "10/20/2025\n10:30 AM", 
            "checked-in"
        ));
        
        appointments.add(new Appointment(
            "APT005", 
            "Lisa Anderson\nP12349", 
            "Dr. Jennifer Lee", 
            "Orthopedics", 
            "10/20/2025\n11:00 AM", 
            "confirmed"
        ));
        
        appointments.add(new Appointment(
            "APT006", 
            "David Martinez\nP12350", 
            "Dr. Michael Chen", 
            "Dermatology", 
            "10/21/2025\n02:00 PM", 
            "scheduled"
        ));
        
        return appointments;
    }
    
    // Appointment Model Class
    public static class Appointment {
        private String appointmentId;
        private String patientName;
        private String doctorName;
        private String specialty;
        private String dateTime;
        private String status;
        
        public Appointment(String appointmentId, String patientName, String doctorName,
                          String specialty, String dateTime, String status) {
            this.appointmentId = appointmentId;
            this.patientName = patientName;
            this.doctorName = doctorName;
            this.specialty = specialty;
            this.dateTime = dateTime;
            this.status = status;
        }
        
        // Getters
        public String getAppointmentId() { return appointmentId; }
        public String getPatientName() { return patientName; }
        public String getDoctorName() { return doctorName; }
        public String getSpecialty() { return specialty; }
        public String getDateTime() { return dateTime; }
        public String getStatus() { return status; }
        
        // Setters
        public void setAppointmentId(String appointmentId) { 
            this.appointmentId = appointmentId; 
        }
        public void setPatientName(String patientName) { 
            this.patientName = patientName; 
        }
        public void setDoctorName(String doctorName) { 
            this.doctorName = doctorName; 
        }
        public void setSpecialty(String specialty) { 
            this.specialty = specialty; 
        }
        public void setDateTime(String dateTime) { 
            this.dateTime = dateTime; 
        }
        public void setStatus(String status) { 
            this.status = status; 
        }

        String getAppointmentDate() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
    

