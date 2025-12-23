package com.shahd.models;

import javafx.beans.property.*;

public class Transaction {
    private StringProperty transactionId;
    private StringProperty patientName;
    private StringProperty type;
    private DoubleProperty amount;
    private StringProperty method;
    private StringProperty dateTime;

    public Transaction(String transactionId, String patientName, String type,
                       double amount, String method, String dateTime) {
        this.transactionId = new SimpleStringProperty(transactionId);
        this.patientName = new SimpleStringProperty(patientName);
        this.type = new SimpleStringProperty(type);
        this.amount = new SimpleDoubleProperty(amount);
        this.method = new SimpleStringProperty(method);
        this.dateTime = new SimpleStringProperty(dateTime);
    }

    // Getters
    public String getTransactionId() { return transactionId.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getType() { return type.get(); }
    public double getAmount() { return amount.get(); }
    public String getMethod() { return method.get(); }
    public String getDateTime() { return dateTime.get(); }

    // Property methods
    public StringProperty transactionIdProperty() { return transactionId; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty methodProperty() { return method; }
    public StringProperty dateTimeProperty() { return dateTime; }

    // Display formatting
    public String getAmountFormatted() {
        return String.format("$%.2f", getAmount());
    }
}
