module de.realwhimsy.afktraderpoe {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires json;
    requires java.desktop;

    opens de.realwhimsy.afktraderpoe to javafx.fxml;
    exports de.realwhimsy.afktraderpoe;
}