module com.shahd.hospitalmanagementsystem {
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive javafx.base;

  // Add this line so JavaFX can access your FXML and Controllers
  opens com.shahd.hospitalmanagementsystem to javafx.fxml;

  requires transitive java.sql;

  exports com.shahd.hospitalmanagementsystem;
  exports com.shahd.models;
}
