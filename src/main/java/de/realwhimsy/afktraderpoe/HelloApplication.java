package de.realwhimsy.afktraderpoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        InetAddress localHost = InetAddress.getLocalHost();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AFKTraderPoE");
        stage.setScene(scene);

        TextField deviceIpTextField = (TextField) scene.lookup("#device_ip_textfield");
        deviceIpTextField.setText(localHost.getHostAddress());

        TextField portTextField = (TextField) scene.lookup("#port_textfield");
        portTextField.setText("4747");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}