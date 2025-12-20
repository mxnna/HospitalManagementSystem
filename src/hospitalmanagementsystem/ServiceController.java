/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hospitalmanagementsystem;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Shahd
 */
public class ServiceController implements Initializable {
    @FXML
    private VBox mainContainer;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label subtitleLabel;
    
    @FXML
    private VBox servicesContainer;
    
    @FXML
    public void initialize() {
        // يمكن إضافة أي كود تهيئة هنا
        System.out.println("Services page initialized");
    }
    
    @FXML
    private void handleServiceCatalog() {
        System.out.println("Service Catalog clicked");
    }
    
    @FXML
    private void handleFacilityStatus() {
        System.out.println("Facility Status clicked");
    }
}

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
