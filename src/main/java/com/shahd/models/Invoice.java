package com.shahd.models;

import javafx.beans.property.*;

public class Invoice {
    private StringProperty invoiceId;
    private StringProperty patientId;
    private StringProperty patientName;
    private StringProperty insurance;
    private DoubleProperty totalAmount;
    private DoubleProperty copayDue;
    private StringProperty status;

    public Invoice(String invoiceId, String patientId, String patientName, String insurance,
                   double totalAmount, double copayDue, String status) {
        this.invoiceId = new SimpleStringProperty(invoiceId);
        this.patientId = new SimpleStringProperty(patientId);
        this.patientName = new SimpleStringProperty(patientName);
        this.insurance = new SimpleStringProperty(insurance);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.copayDue = new SimpleDoubleProperty(copayDue);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public String getInvoiceId() { return invoiceId.get(); }
    public String getPatientId() { return patientId.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getInsurance() { return insurance.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public double getCopayDue() { return copayDue.get(); }
    public String getStatus() { return status.get(); }

    // Property methods
    public StringProperty invoiceIdProperty() { return invoiceId; }
    public StringProperty patientIdProperty() { return patientId; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty insuranceProperty() { return insurance; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public DoubleProperty copayDueProperty() { return copayDue; }
    public StringProperty statusProperty() { return status; }

    // Display formatting
    public String getTotalAmountFormatted() {
        return String.format("$%.2f", getTotalAmount());
    }

    public String getCopayDueFormatted() {
        return String.format("$%.2f", getCopayDue());
    }
}
