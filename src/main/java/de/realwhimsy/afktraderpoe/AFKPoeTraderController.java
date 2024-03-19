package de.realwhimsy.afktraderpoe;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.*;

public class AFKPoeTraderController {
    @FXML
    private TextField deviceIpTextfield;

    @FXML
    private TextField portTextfield;

    @FXML
    private Button connectButton;

    @FXML
    private FileDialogController fileDialogController;

    @FXML
    private Label statusLabel;

    private LogFileTailer logFileTailer;

    @FXML
    public void initialize() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        deviceIpTextfield.setText(localHost.getHostAddress());
        portTextfield.setText("4747");
        connectButton.setOnAction(e -> onConnectButtonClicked());

    }

    private void onConnectButtonClicked() {
        startSocketConnection();
        logFileTailer = new LogFileTailer(fileDialogController.chooseFileTextField.getText(), this::handleNewLine);
        logFileTailer.start();
    }

    private void handleNewLine(String line) {
        SocketClient.sendMessage(line);
    }

    public void startSocketConnection() {
        String ipAddress = deviceIpTextfield.getText();
        int port = Integer.parseInt(portTextfield.getText());
        SocketClient socketClient = new SocketClient(ipAddress, port, this::onMessageReceived);
        socketClient.init();
    }

    private void onMessageReceived(String message) {
        Platform.runLater(() -> {
            if (message.equals("connected")) {
                statusLabel.setText("Status: Connected");
            } else if (message.equals("disconnected")) {
                statusLabel.setText("Status: Disconnected");
            }
        });
    }

    public void stopLogFileTailer() {
        logFileTailer.stop();
    }
}