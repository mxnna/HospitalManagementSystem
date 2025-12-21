/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hospitalmanagementsystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Shahd
 */
public class HomepageController implements Initializable {
    
    @FXML
    private Button aboutpage;

    @FXML
    private Button appointmentspage;

    @FXML
    private Button billingpage;

    @FXML
    private Button doctorpage;

    @FXML
    private Button homepage;

    @FXML
    private Button patientpage;

    @FXML
    private Button servicepage;
    
    
    
    @FXML
    public void about(ActionEvent event) throws IOException {
            loadPage(event, "aboutUs.fxml");
    }
    public class AboutUSController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("aboutPage");
    }
}


    
    @FXML
    void appointment(ActionEvent event) throws IOException {
                    loadPage(event, "Appointment.fxml");
    }
    public class AppointmentsController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("AppointmentsPage");
    }
}

    @FXML
    void billing(ActionEvent event) throws IOException{
                            loadPage(event, "BillingInsurance.fxml");
    }
    public class BillingInsuranceController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("billingPage");
    }
}

    
    
    @FXML
    void doctor(ActionEvent event) throws IOException {
                                    loadPage(event, "doctor.fxml");
    }
    public class doctorController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("doctorPage");
    }
}

   
    
    @FXML
    void home(ActionEvent event) throws IOException{
        loadPage(event, "homepage.fxml");
    }
    public class homepageController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("homePage");
    }
}

    
    @FXML
    void patient(ActionEvent event) throws IOException{
        loadPage(event, "patient.fxml");
    }
    public class patientController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("patientPage");
    }
}

    
    @FXML
    void service(ActionEvent event) throws IOException{
        loadPage(event, "service.fxml");
    }
    public class ServiceController {
    @FXML
    private Label titleLabel;
    
    @FXML
    public void initialize() {
        // الكود اللي يتنفذ لما الصفحة تفتح
        titleLabel.setText("servicePage");
    }
}

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    private void loadPage(ActionEvent event, String fxmlFile) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        System.out.println("Successfully loaded: " + fxmlFile);
    } catch (IOException e) {
        System.err.println("Error loading page: " + fxmlFile);
        e.printStackTrace();
    }
    }
}
