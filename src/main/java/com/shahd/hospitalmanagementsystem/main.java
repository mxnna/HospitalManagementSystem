/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Shahd
 */
public class main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    // Load FXML file - starting with homepage for navigation
    Parent root = FXMLLoader.load(getClass().getResource("homepage.fxml"));

    // Create scene
    Scene scene = new Scene(root, 1100, 1297);

    // Set stage properties
    primaryStage.setTitle("Hospital Management System");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
