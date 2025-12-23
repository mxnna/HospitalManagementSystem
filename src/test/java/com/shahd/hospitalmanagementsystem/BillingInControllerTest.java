package com.shahd.hospitalmanagementsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BillingInControllerTest {

    private static void initFxToolkit() throws Exception {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX platform failed to start");
            }
        } catch (IllegalStateException ignored) {
            // already started
        }
    }

    @BeforeAll
    public static void setupOnce() throws Exception {
        initFxToolkit();
    }

    @Test
    public void testVerifyCoverageShowsResult() throws Exception {
        final CountDownLatch loadLatch = new CountDownLatch(1);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/BillingInsurance.fxml"));
        final Parent[] rootHolder = new Parent[1];

        Platform.runLater(() -> {
            try {
                rootHolder[0] = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed loading BillingInsurance.fxml: " + e.getMessage());
            } finally {
                loadLatch.countDown();
            }
        });

        assertTrue(loadLatch.await(5, TimeUnit.SECONDS));
        assertNotNull(rootHolder[0], "Billing root should load");

        final Object controller = loader.getController();
        assertNotNull(controller, "Billing controller should be available");

        Field vf = controller.getClass().getDeclaredField("verificationResult");
        vf.setAccessible(true);
        final VBox verificationResult = (VBox) vf.get(controller);
        assertNotNull(verificationResult, "verificationResult node should be injected");
        assertFalse(verificationResult.isVisible(), "verification result should be hidden initially");

        // Invoke verifyCoverage(ActionEvent) and capture any exception from the FX thread
        final Exception[] thrown = new Exception[1];
        final CountDownLatch actionLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                var m = controller.getClass().getDeclaredMethod("verifyCoverage", ActionEvent.class);
                m.setAccessible(true);
                m.invoke(controller, new ActionEvent());
            } catch (Exception e) {
                thrown[0] = e;
            } finally {
                actionLatch.countDown();
            }
        });

        assertTrue(actionLatch.await(2, TimeUnit.SECONDS), "Timed out invoking verifyCoverage");
        if (thrown[0] != null) fail("Exception during verifyCoverage: " + thrown[0]);

        // Wait (up to 2s) for the UI to update instead of a fixed short sleep
        long start = System.currentTimeMillis();
        while (!verificationResult.isVisible() && System.currentTimeMillis() - start < 2000) {
            Thread.sleep(50);
        }
        assertTrue(verificationResult.isVisible(), "verification result should be visible after verifyCoverage");

        Field statusF = controller.getClass().getDeclaredField("statusLabel");
        statusF.setAccessible(true);
        javafx.scene.control.Label statusLabel = (javafx.scene.control.Label) statusF.get(controller);
        assertEquals("Verified", statusLabel.getText());

        Field coverageF = controller.getClass().getDeclaredField("coverageTypeLabel");
        coverageF.setAccessible(true);
        javafx.scene.control.Label coverageLabel = (javafx.scene.control.Label) coverageF.get(controller);
        assertEquals("Full", coverageLabel.getText());

        Field copayF = controller.getClass().getDeclaredField("copayAmountLabel");
        copayF.setAccessible(true);
        javafx.scene.control.Label copayLabel = (javafx.scene.control.Label) copayF.get(controller);
        assertEquals("$20", copayLabel.getText());

        Field dedF = controller.getClass().getDeclaredField("deductibleLabel");
        dedF.setAccessible(true);
        javafx.scene.control.Label dedLabel = (javafx.scene.control.Label) dedF.get(controller);
        assertEquals("$100", dedLabel.getText());
    }
}
