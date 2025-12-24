package com.shahd.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class Department {
    private final IntegerProperty departmentId;
    private final StringProperty name;
    private final StringProperty icon;
    private final IntegerProperty activeDoctorCount;

    public Department(int departmentId, String name, String icon, int activeDoctorCount) {
        this.departmentId = new SimpleIntegerProperty(departmentId);
        this.name = new SimpleStringProperty(name);
        this.icon = new SimpleStringProperty(icon);
        this.activeDoctorCount = new SimpleIntegerProperty(activeDoctorCount);
    }

    // Properties
    public IntegerProperty departmentIdProperty() { return departmentId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty iconProperty() { return icon; }
    public IntegerProperty activeDoctorCountProperty() { return activeDoctorCount; }

    // Getters
    public int getId() { return departmentId.get(); }
    public String getName() { return name.get(); }
    public String getIcon() { return icon.get(); }
    public int getActiveDoctorCount() { return activeDoctorCount.get(); }

    // Setters
    public void setId(int departmentId) { this.departmentId.set(departmentId); }
    public void setName(String name) { this.name.set(name); }
    public void setIcon(String icon) { this.icon.set(icon); }
    public void setActiveDoctorCount(int activeDoctorCount) { this.activeDoctorCount.set(activeDoctorCount); }
}
