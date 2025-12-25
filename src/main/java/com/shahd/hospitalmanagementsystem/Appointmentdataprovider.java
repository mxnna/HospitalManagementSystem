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
        String[] doctors = {"Dr. Robert Smith", "Dr. Jennifer Lee", "Dr. Priya Patel", "Dr. Michael Chen", "Dr. Aisha Khan"};
        String[] specialties = {"Cardiology", "Orthopedics", "Neurology", "Dermatology", "General"};
        String[] statuses = {"scheduled", "confirmed", "checked-in", "cancelled"};

        // Generate 30 sample appointments to better represent a busy day
        for (int i = 1; i <= 30; i++) {
            String id = String.format("APT%03d", i);
            String patient = String.format("Patient %02d\nP12%03d", i, 40 + i);
            String doctor = doctors[i % doctors.length];
            String specialty = specialties[i % specialties.length];
            // distribute times across the day
            int hour = 8 + (i % 10); // 8..17
            String ampm = hour < 12 ? "AM" : "PM";
            int displayHour = hour <= 12 ? hour : hour - 12;
            String minute = (i % 2 == 0) ? "30" : "00";
            String dateTime = String.format("10/20/2025\n%02d:%s %s", displayHour, minute, ampm);
            String status = statuses[i % statuses.length];
            appointments.add(new Appointment(id, patient, doctor, specialty, dateTime, status));
        }

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
    

