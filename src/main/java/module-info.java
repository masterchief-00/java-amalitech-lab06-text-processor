module com.kwizera.javaamalitechlab06textprocessor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.kwizera.javaamalitechlab06textprocessor to javafx.fxml;
    exports com.kwizera.javaamalitechlab06textprocessor;
}