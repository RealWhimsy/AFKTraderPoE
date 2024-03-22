module de.realwhimsy.afktraderpoe {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens de.realwhimsy.afktraderpoe to javafx.fxml;
    exports de.realwhimsy.afktraderpoe;
}