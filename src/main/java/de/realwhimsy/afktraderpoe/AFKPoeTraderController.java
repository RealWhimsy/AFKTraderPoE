package de.realwhimsy.afktraderpoe;

import de.realwhimsy.afktraderpoe.datamodel.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    private boolean isConnected = false;

    @FXML
    public void initialize() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        deviceIpTextfield.setText(localHost.getHostAddress());
        portTextfield.setText("4747");
        connectButton.setOnAction(e -> onConnectButtonClicked());
    }


    private void onConnectButtonClicked() {
        if (!isConnected) {
            // check if the file path is set to client.txt, if not, show warning and return
            if (!fileDialogController.chooseFileTextField.getText().endsWith("Client.txt")) {
                var alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Incorrect Client.txt location");
                alert.setContentText("The provided path does not point to the Client.txt file." +
                        "\nPlease update the file path.");
                alert.show();
                return;
            }
            startSocketConnection();
            logFileTailer = LogFileTailer.getInstance(fileDialogController.chooseFileTextField.getText(), this::handleNewLine);
            logFileTailer.start();
        }
    }

    private void handleNewLine(String line) {
        if (MessageParseUtil.matchesItemBuyPattern(line)) {
            SocketClient.getInstance().sendTransaction(line);
        }
    }

    public void startSocketConnection() {
        String ipAddress = deviceIpTextfield.getText();
        int port = Integer.parseInt(portTextfield.getText());
        SocketClient socketClient = SocketClient.getInstance();
        socketClient.setIpAddress(ipAddress);
        socketClient.setPort(port);
        socketClient.setConnectionStatusChangedCallback(this::onConnectionStatusChanged);
        socketClient.init();
    }

    private void onConnectionStatusChanged(Boolean isConnected) {
        this.isConnected = isConnected;
        updateStatusLabelText(isConnected);
    }

    private void updateStatusLabelText(Boolean isConnected) {
        Platform.runLater(() -> {
            if (isConnected) {
                statusLabel.setText("Status: Connected");
            } else {
                logFileTailer.stop();
                SocketClient.getInstance().closeConnection();
                statusLabel.setText("Status: Disconnected");
            }
        });
    }

    public void stopLogFileTailer() {
        if (logFileTailer != null) {
            logFileTailer.stop();
        }
    }
}