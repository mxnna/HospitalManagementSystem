/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Shahd
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    private VBox login_form;

    @FXML
    private Button login_loginbtn;
    
    @FXML
    private PasswordField login_password;

    @FXML
    private ComboBox<String> login_user;

    @FXML
    private TextField login_username;

    @FXML
    private StackPane main_form;
    
    public void userList(){
        List<String> listU = new ArrayList<>();
        
        for(String data : Users.user){
            listU.add(data);
        }
        
        ObservableList<String> listData = FXCollections.observableList(listU);
        login_user.setItems(listData);
    }
    
    @FXML
    public void login(ActionEvent event) {
        System.out.println("Button clicked!");
        System.out.println("Username entered: " + login_username.getText());
        System.out.println("Password entered: " + login_password.getText());

        String username = login_username.getText().trim();
        String password = login_password.getText().trim();

        if (username.equals("admin1") && password.equals("12345")) {
            System.out.println("Credentials correct, loading homepage...");

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("homepage.fxml")
                );

                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource())
                        .getScene().getWindow();

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("pagedesign.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Hospital Management System | Home page");
                
                System.out.println("Homepage loaded successfully!");

            } catch (IOException e) {
                System.err.println("Error loading homepage: " + e.getMessage());
                e.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load homepage");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Unexpected error occurred");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }

        } else {
            System.out.println("Wrong username or password!");
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("Please check your username and password.");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userList();
    }    
}
