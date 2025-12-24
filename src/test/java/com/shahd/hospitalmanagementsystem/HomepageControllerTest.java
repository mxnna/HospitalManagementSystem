package com.shahd.hospitalmanagementsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HomepageControllerTest {

    private static void initFxToolkit() throws Exception {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("JavaFX platform failed to start");
            }
        } catch (IllegalStateException ignored) {
            // already started - ok
        }
    }

    @BeforeAll
    public static void setupOnce() throws Exception {
        initFxToolkit();
    }

    @Test
    public void testSidebarPagesLoadAndBindSize() throws Exception {
        final CountDownLatch loadLatch = new CountDownLatch(1);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/homepage.fxml"));
        final Parent[] rootHolder = new Parent[1];

        Platform.runLater(() -> {
            try {
                rootHolder[0] = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                loadLatch.countDown();
            }
        });

        assertTrue(loadLatch.await(5, TimeUnit.SECONDS), "Timed out loading homepage.fxml");
        assertNotNull(rootHolder[0], "homepage root should be loaded");

        final Object controller = loader.getController();
        assertNotNull(controller, "homepage controller should be available");

        // get contentArea via reflection
        Field f = controller.getClass().getDeclaredField("contentArea");
        f.setAccessible(true);
        final AnchorPane contentArea = (AnchorPane) f.get(controller);
        assertNotNull(contentArea, "contentArea should be injected");

        // After initialization, dashboard should be loaded by initialize()
        // give a short time for initialize code to run
        Thread.sleep(200);
        assertTrue(contentArea.getChildren().size() > 0, "Dashboard or default content should be present");

        // check binding on the loaded child
        boolean boundFound = false;
        for (var child : contentArea.getChildren()) {
            if (child instanceof Region) {
                Region r = (Region) child;
                if (r.prefWidthProperty().isBound() && r.prefHeightProperty().isBound()) {
                    boundFound = true;
                    break;
                }
            }
        }
        assertTrue(boundFound, "Loaded dashboard should have its size properties bound to contentArea");

        // Now test loading each page via the public action methods
        String[] methods = new String[] {"appointment", "doctor", "patient", "service", "billing", "about", "home"};

        for (String m : methods) {
            final CountDownLatch actionLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    // invoke the method reflectively: method(ActionEvent)
                    var method = controller.getClass().getMethod(m, ActionEvent.class);
                    method.invoke(controller, new ActionEvent());
                } catch (NoSuchMethodException e) {
                    // Some methods might not exist in older versions - fail fast
                    fail("Method not found on controller: " + m);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Exception invoking method: " + m + " -> " + e.getMessage());
                } finally {
                    actionLatch.countDown();
                }
            });

            assertTrue(actionLatch.await(3, TimeUnit.SECONDS), "Timed out invoking " + m);
            // allow the FXML load to complete
            Thread.sleep(150);

            assertTrue(contentArea.getChildren().size() > 0, "After invoking " + m + ", contentArea must contain children");

            boolean thisBound = false;
            for (var child : contentArea.getChildren()) {
                if (child instanceof Region) {
                    Region r = (Region) child;
                    if (r.prefWidthProperty().isBound() && r.prefHeightProperty().isBound()) {
                        thisBound = true;
                        break;
                    }
                }
            }
            assertTrue(thisBound, "After invoking " + m + ", loaded content should be bound to contentArea");
        }
    }
}
