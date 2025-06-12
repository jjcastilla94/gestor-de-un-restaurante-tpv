module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    //requires java.sql;
    requires mysql.connector.java;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires itextpdf;
    requires java.desktop;
    
    opens com.example to javafx.fxml;
    exports com.example;

}
