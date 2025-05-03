module com.kwizera.javaamalitechlab06textprocessor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens com.kwizera.javaamalitechlab06textprocessor to javafx.fxml;
    opens com.kwizera.javaamalitechlab06textprocessor.controllers to javafx.fxml;
    opens com.kwizera.javaamalitechlab06textprocessor.models to javafx.base;

    exports com.kwizera.javaamalitechlab06textprocessor;
}