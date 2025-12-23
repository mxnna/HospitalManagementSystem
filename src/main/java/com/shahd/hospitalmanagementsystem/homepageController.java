/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author Shahd
 */
public class homepageController {
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
    loadPage(event, "aboutus.fxml");
  }

  private Object getScene() {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                   // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }

  public class AboutusController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("aboutPage");
    }
  }

  @FXML
  public void appointment(ActionEvent event) throws IOException {
    loadPage(event, "appointment.fxml");
  }

  public class AppointmentController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("AppointmentsController");
    }
  }

  @FXML
  public void billing(ActionEvent event) throws IOException {
    loadPage(event, "BillingInsurance.fxml");
  }

  public class BillingInController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("billingPage");
    }
  }

  @FXML
  public void doctor(ActionEvent event) throws IOException {
    loadPage(event, "doctor.fxml");
  }

  public class doctorController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("doctoePage");
    }
  }

  @FXML
  public void home(ActionEvent event) throws IOException {
    loadPage(event, "homepage.fxml");
  }

  public class homePageController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("homePage");
    }
  }

  @FXML
  public void patient(ActionEvent event) throws IOException {
    loadPage(event, "patient.fxml");
  }

  public class patientController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("patientPage");
    }
  }

  @FXML
  public void service(ActionEvent event) throws IOException {
    loadPage(event, "service.fxml");
  }

  public class serviceController {
    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
      titleLabel.setText("servicePage");
    }
  }

  private void loadPage(ActionEvent event, String fxmlFile) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
      System.out.println("successfuly loaded: " + fxmlFile);
    } catch (IOException e) {
      System.err.println("error loading page:" + fxmlFile);
      e.printStackTrace();
    }
  }

}
