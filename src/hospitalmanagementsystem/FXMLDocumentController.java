/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package hospitalmanagementsystem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;




/**
 *
 * @author Shahd
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private AnchorPane login_form;

    @FXML
    private Button login_loginbtn;
    
    

    @FXML
    private PasswordField login_password;

    @FXML
    private ComboBox<String> login_user;

    @FXML
    private TextField login_username;

    @FXML
    private AnchorPane main_form;
    
    

    
    public void userList(){
        List<String> listU = new ArrayList<>();
        
        for(String data : Users.user){
            listU.add(data);
        }
        
        ObservableList<String> listData = FXCollections.observableList(listU);
        login_user.setItems(listData);
    }
    
    @FXML
    public void login(ActionEvent event) throws InterruptedException {
        System.out.println("Button clicked!");
        Thread.sleep(1000);

        if (login_username.getText().equals("shahd")
                && login_password.getText().equals("123")) {

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/hospitalmanagementsystem/homepage.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource())
                        .getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("Hospital Management System | Home page");
              
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Wrong username or password!");
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userList();
    }    
    
   
    
}
