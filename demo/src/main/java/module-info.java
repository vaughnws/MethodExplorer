module com.example {
    requires java.prefs;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    
    exports com.example;
    opens com.example to javafx.fxml;
}
