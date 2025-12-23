/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 * @author Shahd
 */
public class homepageController implements Initializable {
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
  private AnchorPane contentArea;
  
  @FXML
  private Button logoutBtn;

  @FXML
  public void logout(ActionEvent event) throws IOException {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
      javafx.scene.Scene scene = new javafx.scene.Scene(root);
      javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.setTitle("Hospital Management System | Login");
      System.out.println("Logged out successfully");
    } catch (IOException e) {
      System.err.println("Error loading login page: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  public void about(ActionEvent event) throws IOException {
    loadPage(event, "aboutus.fxml");
  }

  @FXML
  public void appointment(ActionEvent event) throws IOException {
    loadPage(event, "appointment.fxml");
  }

  @FXML
  public void billing(ActionEvent event) throws IOException {
    loadPage(event, "BillingInsurance.fxml");
  }

  @FXML
  public void doctor(ActionEvent event) throws IOException {
    loadPage(event, "doctor.fxml");
  }

  @FXML
  public void home(ActionEvent event) throws IOException {
    loadPage(event, "dashboard.fxml");
  }

  @FXML
  public void patient(ActionEvent event) throws IOException {
    loadPage(event, "patient.fxml");
  }

  @FXML
  public void service(ActionEvent event) throws IOException {
    System.out.println("[homepageController] Service button clicked");
    loadPage(event, "service.fxml");
  }

  private void loadPage(ActionEvent event, String fxmlFile) {
    try {
      System.out.println("[homepageController] loading fxml: " + fxmlFile);
      java.net.URL res = getClass().getResource(fxmlFile);
      System.out.println("[homepageController] resource URL=" + res);
      if (res == null) {
        javafx.application.Platform.runLater(() -> {
          javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
          a.setHeaderText("Load error");
          a.setContentText("FXML resource not found: " + fxmlFile);
          a.showAndWait();
        });
        return;
      }
      FXMLLoader loader = new FXMLLoader(res);
      Parent root = loader.load();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(root);
      AnchorPane.setTopAnchor(root, 0.0);
      AnchorPane.setLeftAnchor(root, 0.0);
      AnchorPane.setRightAnchor(root, 0.0);
      AnchorPane.setBottomAnchor(root, 0.0);
      // Make the loaded root resize with the content area
      if (root instanceof javafx.scene.layout.Region) {
        javafx.scene.layout.Region regionRoot = (javafx.scene.layout.Region) root;
        regionRoot.prefWidthProperty().bind(contentArea.widthProperty());
        regionRoot.prefHeightProperty().bind(contentArea.heightProperty());
        regionRoot.minWidthProperty().bind(contentArea.widthProperty());
        regionRoot.minHeightProperty().bind(contentArea.heightProperty());
      }
      System.out.println("Successfully loaded: " + fxmlFile);
    } catch (IOException e) {
      System.err.println("Error loading page: " + fxmlFile + " -> " + e.getMessage());
      e.printStackTrace();
      javafx.application.Platform.runLater(() -> {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        a.setHeaderText("Load exception");
        a.setContentText(e.getMessage());
        a.showAndWait();
      });
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Load the default dashboard content when page initializes
    try {
      Parent dashboardContent = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
      contentArea.getChildren().clear();
      contentArea.getChildren().add(dashboardContent);
      AnchorPane.setTopAnchor(dashboardContent, 0.0);
      AnchorPane.setLeftAnchor(dashboardContent, 0.0);
      AnchorPane.setRightAnchor(dashboardContent, 0.0);
      AnchorPane.setBottomAnchor(dashboardContent, 0.0);
      // Bind dashboard size to content area so it always fills the center
      if (dashboardContent instanceof javafx.scene.layout.Region) {
        javafx.scene.layout.Region region = (javafx.scene.layout.Region) dashboardContent;
        region.prefWidthProperty().bind(contentArea.widthProperty());
        region.prefHeightProperty().bind(contentArea.heightProperty());
        region.minWidthProperty().bind(contentArea.widthProperty());
        region.minHeightProperty().bind(contentArea.heightProperty());
      }
      System.out.println("Dashboard loaded successfully");
    } catch (IOException e) {
      System.err.println("Error loading dashboard: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
