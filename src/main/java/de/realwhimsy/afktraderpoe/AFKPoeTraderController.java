package de.realwhimsy.afktraderpoe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    public void initialize() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        deviceIpTextfield.setText(localHost.getHostAddress());
        portTextfield.setText("4747");
        connectButton.setOnAction(e -> onConnectButtonClicked());
    }

    private void onConnectButtonClicked() {
        // TODO init file watch service for client txt, open socket connection
    }
}