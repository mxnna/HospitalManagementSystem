package com.shahd.hospitalmanagementsystem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.Node;

public class ServicePageTest {

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

    private <T extends Node> T findNodeOfType(Parent root, Class<T> cls) {
        if (cls.isInstance(root)) return cls.cast(root);
        for (Node c : root.getChildrenUnmodifiable()) {
            if (cls.isInstance(c)) return cls.cast(c);
            if (c instanceof Parent) {
                T found = findNodeOfType((Parent) c, cls);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Test
    public void testServiceFXMLLoadsAndHasTable() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shahd/hospitalmanagementsystem/service.fxml"));
        final Parent[] rootHolder = new Parent[1];

        Platform.runLater(() -> {
            try {
                rootHolder[0] = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed loading service.fxml: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(rootHolder[0], "service root should load");

        TableView<?> table = findNodeOfType(rootHolder[0], TableView.class);
        assertNotNull(table, "services TableView should be present in service.fxml");
        assertEquals(4, table.getColumns().size(), "Expected 4 columns in services table");
    }
}
