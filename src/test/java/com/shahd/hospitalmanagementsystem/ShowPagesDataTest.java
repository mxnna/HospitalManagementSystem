package com.shahd.hospitalmanagementsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ShowPagesDataTest {

  private static void initFxToolkit() throws Exception {
    try {
      final CountDownLatch latch = new CountDownLatch(1);
      Platform.startup(() -> latch.countDown());
      if (!latch.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("JavaFX platform failed to start");
    } catch (IllegalStateException ignored) {
      // already started
    }
  }

  @BeforeAll
  public static void setupOnce() throws Exception {
    initFxToolkit();
  }

  @Test
  public void loadAppointmentsAndPatientsAndPrintSampleRows() throws Exception {
    // Load appointment.fxml
    final CountDownLatch aLatch = new CountDownLatch(1);
    final Parent[] aRoot = new Parent[1];
    final FXMLLoader aLoader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/appointment.fxml"));

    Platform.runLater(() -> {
      try {
        aRoot[0] = aLoader.load();
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed to load appointment.fxml: " + e.getMessage());
      } finally {
        aLatch.countDown();
      }
    });
    assertTrue(aLatch.await(5, TimeUnit.SECONDS));

    Object aController = aLoader.getController();
    assertNotNull(aController);

    // Trigger refreshTable if present
    try {
      var m = aController.getClass().getDeclaredMethod("refreshTable");
      m.setAccessible(true);
      Platform.runLater(() -> {
        try { m.invoke(aController); } catch (Exception e) { /* ignore */ }
      });
    } catch (NoSuchMethodException ns) {
      // ignore
    }

    TableView<?> aTable = (TableView<?>) aLoader.getNamespace().get("appointmentTable");
    if (aTable == null) System.out.println("appointmentTable not found in appointment.fxml");
    else {
      System.out.println("--- Appointments (up to 5 rows) ---");
      for (int i = 0; i < Math.min(5, aTable.getItems().size()); i++) {
        Object row = aTable.getItems().get(i);
        try {
          var gm = row.getClass().getMethod("getAppointmentId");
          var gp = row.getClass().getMethod("getPatientId");
          var gn = row.getClass().getMethod("getPatientName");
          System.out.println(String.format("%s | %s | %s", gm.invoke(row), gp.invoke(row), gn.invoke(row)));
        } catch (Exception ex) {
          System.out.println("Row " + i + " could not be introspected: " + ex.getMessage());
        }
      }
    }

    // Load patient.fxml
    final CountDownLatch pLatch = new CountDownLatch(1);
    final Parent[] pRoot = new Parent[1];
    final FXMLLoader pLoader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/patient.fxml"));

    Platform.runLater(() -> {
      try {
        pRoot[0] = pLoader.load();
      } catch (Exception e) {
        e.printStackTrace();
        fail("Failed to load patient.fxml: " + e.getMessage());
      } finally {
        pLatch.countDown();
      }
    });
    assertTrue(pLatch.await(5, TimeUnit.SECONDS));

    Object pController = pLoader.getController();
    assertNotNull(pController);

    // Trigger loadPatientsFromDb if present
    try {
      var m = pController.getClass().getDeclaredMethod("loadPatientsFromDb");
      m.setAccessible(true);
      Platform.runLater(() -> {
        try { m.invoke(pController); } catch (Exception e) { /* ignore */ }
      });
    } catch (NoSuchMethodException ns) {
      // ignore
    }

    TableView<?> pTable = (TableView<?>) pLoader.getNamespace().get("patientTable");
    if (pTable == null) System.out.println("patientTable not found in patient.fxml");
    else {
      System.out.println("--- Patients (up to 5 rows) ---");
      for (int i = 0; i < Math.min(5, pTable.getItems().size()); i++) {
        Object row = pTable.getItems().get(i);
        try {
          var gm = row.getClass().getMethod("getPatientId");
          java.lang.reflect.Method fullNameMethod = null;
          try { fullNameMethod = row.getClass().getMethod("getFullName"); } catch (NoSuchMethodException ns) { /* not present */ }

          if (fullNameMethod != null) {
            System.out.println(String.format("%s | %s", gm.invoke(row), fullNameMethod.invoke(row)));
          } else {
            // fallback to first/last
            java.lang.reflect.Method g1 = null;
            java.lang.reflect.Method g2 = null;
            try { g1 = row.getClass().getMethod("getFirstName"); } catch (NoSuchMethodException ns) { /* ignore */ }
            try { g2 = row.getClass().getMethod("getLastName"); } catch (NoSuchMethodException ns) { /* ignore */ }
            if (g1 != null && g2 != null) {
              System.out.println(String.format("%s | %s %s", gm.invoke(row), g1.invoke(row), g2.invoke(row)));
            } else {
              // last resort, try toString
              System.out.println(String.format("%s | %s", gm.invoke(row), row.toString()));
            }
          }
        } catch (Exception ex) {
          System.out.println("Row " + i + " could not be introspected: " + ex.getMessage());
        }
      }
    }
  }
}