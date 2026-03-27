module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    //requires java.sql;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires itextpdf;
    requires java.desktop;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires mysql.connector.j;
    requires lombok;
    
    opens com.example to javafx.fxml;
    exports com.example;

}
