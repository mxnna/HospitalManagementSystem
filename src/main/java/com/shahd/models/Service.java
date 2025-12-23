package com.shahd.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Service {

    private final StringProperty serviceCode;
    private final StringProperty serviceName;
    private final StringProperty category;
    private final DoubleProperty cost;
    private final StringProperty description;
    private final BooleanProperty active;

    public Service(String code, String name, String category, double cost, String description, boolean active) {
        this.serviceCode = new SimpleStringProperty(code);
        this.serviceName = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.cost = new SimpleDoubleProperty(cost);
        this.description = new SimpleStringProperty(description);
        this.active = new SimpleBooleanProperty(active);
    }

    public String getServiceCode() { return serviceCode.get(); }
    public StringProperty serviceCodeProperty() { return serviceCode; }

    public String getServiceName() { return serviceName.get(); }
    public StringProperty serviceNameProperty() { return serviceName; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }

    public double getCost() { return cost.get(); }
    public DoubleProperty costProperty() { return cost; }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }

    public boolean isActive() { return active.get(); }
    public BooleanProperty activeProperty() { return active; }
}
