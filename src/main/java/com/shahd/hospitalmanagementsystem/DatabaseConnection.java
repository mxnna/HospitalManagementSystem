/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.shahd.hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Shahd
 */
public class DatabaseConnection {
  private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "";

  public static Connection getConnection() {
    Connection connection = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
      System.out.println("Database connected successfully!");
    } catch (ClassNotFoundException | SQLException e) {
      System.out.println("Database connection failed!");
      e.printStackTrace();
    }
    return connection;
  }
}
