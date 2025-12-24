package com.shahd.hospitalmanagementsystem;

import com.shahd.models.Service;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ServiceController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private TilePane servicesFlow;
    @FXML private ScrollPane servicesScroll;

    private ObservableList<Service> services = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadServices();
        // ensure scroll and tilepane accept mouse input and allow panning
        if (servicesScroll != null) {
            servicesScroll.setPannable(true);
            servicesScroll.setFocusTraversable(true);
        }
        if (servicesFlow != null) {
            servicesFlow.setMouseTransparent(false);
            servicesFlow.setPickOnBounds(true);
            servicesFlow.setFocusTraversable(true);
        }
        // Instrument clicks to diagnose blocking overlays
        if (rootPane != null) {
            rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                System.out.println("[ServiceRoot] Mouse clicked target=" + e.getTarget() + " node=" + e.getPickResult().getIntersectedNode());
            });
        }
        if (servicesScroll != null) {
            servicesScroll.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                System.out.println("[ScrollPane] Mouse clicked target=" + e.getTarget());
            });
        }
        if (servicesFlow != null) {
            servicesFlow.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                System.out.println("[TilePane] Mouse clicked target=" + e.getTarget());
            });
        }
    }

    private void loadServices() {
        services.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                Statement st = conn.createStatement();
                String sql = "SELECT service_code, service_name, category, cost, description, is_active FROM services WHERE is_active = 1 ORDER BY service_name";
                ResultSet rs = st.executeQuery(sql);
                while (rs.next()) {
                    String code = rs.getString("service_code");
                    String name = rs.getString("service_name");
                    String category = rs.getString("category");
                    double cost = rs.getDouble("cost");
                    String desc = rs.getString("description");
                    boolean active = rs.getBoolean("is_active");
                    services.add(new Service(code, name, category, cost, desc, active));
                }
                rs.close();
                st.close();
            }
        } catch (Exception ex) {
            services.addAll(getSampleServices());
        }

        // build UI cards
        servicesFlow.getChildren().clear();
        // Make cards responsive: adjust card widths based on available TilePane width
        servicesFlow.widthProperty().addListener((obs, oldW, newW) -> {
            double available = newW.doubleValue();
            double gap = servicesFlow.getHgap();
            int columns = available > 900 ? 2 : 1;
            double totalGaps = gap * (columns - 1);
            double tileWidth = Math.max(280, (available - totalGaps - 40) / columns);
            for (javafx.scene.Node node : servicesFlow.getChildren()) {
                if (node instanceof javafx.scene.layout.Region) {
                    ((javafx.scene.layout.Region) node).setPrefWidth(tileWidth);
                }
            }
        });

        for (Service s : services) {
            VBox card = createCard(s);
            card.setPrefHeight(180);
            // make each card clickable and report clicks
            card.setPickOnBounds(true);
            card.setOnMouseClicked(evt -> {
                System.out.println("[Card] clicked: " + s.getServiceName() + " target=" + evt.getTarget());
                evt.consume();
            });
            servicesFlow.getChildren().add(card);
            System.out.println("[ServiceController] Added card for: " + s.getServiceName());
        }
        System.out.println("[ServiceController] Total cards added: " + servicesFlow.getChildren().size());
    }

    private VBox createCard(Service s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("service-card.fxml"));
            VBox card = loader.load();
            System.out.println("[ServiceController] Loaded FXML card for: " + s.getServiceName());
            
            // Get the child nodes and populate them
            Label serviceName = (Label) card.lookup("#serviceName");
            Label serviceCategory = (Label) card.lookup("#serviceCategory");
            Label serviceDescription = (Label) card.lookup("#serviceDescription");
            
            if (serviceName != null) {
                serviceName.setText(s.getServiceName());
            } else {
                System.err.println("[ServiceController] serviceName label not found in card");
            }
            if (serviceCategory != null) {
                serviceCategory.setText(s.getCategory());
            } else {
                System.err.println("[ServiceController] serviceCategory label not found in card");
            }
            if (serviceDescription != null) {
                serviceDescription.setText(s.getDescription());
            } else {
                System.err.println("[ServiceController] serviceDescription label not found in card");
            }
            
            // Set icon and color based on category
            setIconAndColor(card, s.getCategory());
            
            return card;
        } catch (Exception e) {
            System.err.println("Error loading service card template: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple card
            return createSimpleCard(s);
        }
    }
    
    private VBox createSimpleCard(Service s) {
        VBox card = new VBox(10);
        card.getStyleClass().add("service-card");
        card.setPadding(new Insets(18));
        HBox top = new HBox(12);

        Label iconBox = new Label(s.getServiceCode().substring(0, Math.min(3, s.getServiceCode().length())).toUpperCase());
        iconBox.getStyleClass().add(getIconClassForService(s.getCategory()));
        iconBox.setMinSize(50, 50);
        iconBox.setStyle("-fx-alignment: center; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox meta = new VBox(4);
        Label name = new Label(s.getServiceName());
        name.getStyleClass().add("service-name");
        Label cat = new Label(s.getCategory());
        cat.getStyleClass().add("service-category");
        meta.getChildren().addAll(name, cat);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(iconBox, meta, spacer);

        Label desc = new Label(s.getDescription());
        desc.getStyleClass().add("service-description");
        desc.setWrapText(true);

        Label prepLabel = new Label("Preparation:");
        prepLabel.getStyleClass().add("preparation-label");
        Label prepText = new Label("See department for pre-procedure instructions");
        prepText.getStyleClass().add("preparation-text");

        Label badge = new Label("24/7");
        badge.getStyleClass().add("availability-badge");

        card.getChildren().addAll(top, desc, prepLabel, prepText, badge);
        return card;
    }
    
    private void setIconAndColor(VBox card, String category) {
        VBox iconBox = (VBox) card.lookup("#iconBox");
        if (iconBox != null) {
            iconBox.getStyleClass().clear();
            iconBox.getStyleClass().add(getIconClassForService(category));
        }
    }

    private String getIconClassForService(String category) {
        if (category == null) return "icon-box-blue";
        String cat = category.toLowerCase();
        if (cat.contains("radiology") || cat.contains("imaging")) return "icon-box-blue";
        if (cat.contains("lab") || cat.contains("laboratory")) return "icon-box-green";
        if (cat.contains("cardio")) return "icon-box-red";
        if (cat.contains("neuro")) return "icon-box-purple";
        if (cat.contains("therapy") || cat.contains("physio")) return "icon-box-orange";
        if (cat.contains("pediatric")) return "icon-box-pink";
        if (cat.contains("surgery")) return "icon-box-indigo";
        if (cat.contains("consultation")) return "icon-box-yellow";
        return "icon-box-blue";
    }

    private ObservableList<Service> getSampleServices() {
        ObservableList<Service> list = FXCollections.observableArrayList();
        list.add(new Service("SRV-RAD", "Radiology", "Imaging Services", 120.00, "X-Ray, CT, MRI, Ultrasound imaging services", true));
        list.add(new Service("SRV-LAB", "Laboratory Services", "Clinical Lab", 45.00, "Blood tests, urinalysis, pathology services", true));
        list.add(new Service("SRV-PT", "Physiotherapy", "Rehabilitation", 60.00, "Physical therapy and rehabilitation services", true));
        list.add(new Service("SRV-INP", "Inpatient Care", "General Ward", 250.00, "24-hour nursing care and medical monitoring", true));
        list.add(new Service("SRV-CARD", "Cardiology", "Cardiovascular", 180.00, "Heart disease diagnosis and treatment", true));
        list.add(new Service("SRV-NEU", "Neurology", "Neuroscience", 150.00, "Brain and nervous system treatment", true));
        return list;
    }
}
