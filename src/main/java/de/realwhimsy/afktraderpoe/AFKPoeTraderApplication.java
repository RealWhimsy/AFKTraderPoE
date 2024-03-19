package de.realwhimsy.afktraderpoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AFKPoeTraderApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AFKPoeTraderApplication.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("AFKTraderPoE");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            if (fxmlLoader.getController() instanceof AFKPoeTraderController poeController) {
                poeController.stopLogFileTailer();
            }
            SocketClient.closeConnection();
            Platform.exit();
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}