module com.shahd.hospitalmanagementsystem {
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.base;
  requires transitive javafx.graphics;

  // Allow the FXMLLoader (and other JavaFX reflection) to access controllers and models
  opens com.shahd.hospitalmanagementsystem to javafx.fxml;
  opens com.shahd.models to javafx.fxml;

  requires transitive java.sql;

  exports com.shahd.hospitalmanagementsystem;
  exports com.shahd.models;
}
