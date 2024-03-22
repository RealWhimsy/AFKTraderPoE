package de.realwhimsy.afktraderpoe;

import de.realwhimsy.afktraderpoe.datamodel.Item;
import de.realwhimsy.afktraderpoe.datamodel.MessageParseUtil;
import de.realwhimsy.afktraderpoe.datamodel.Price;
import de.realwhimsy.afktraderpoe.datamodel.Transaction;
import de.realwhimsy.afktraderpoe.datamodel.TypeAdapters.ItemAdapter;
import de.realwhimsy.afktraderpoe.datamodel.TypeAdapters.PriceAdapter;
import de.realwhimsy.afktraderpoe.datamodel.TypeAdapters.TransactionAdapter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private GsonBuilder gsonBuilder;

    @FXML
    public void initialize() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        deviceIpTextfield.setText(localHost.getHostAddress());
        portTextfield.setText("4747");
        connectButton.setOnAction(e -> onConnectButtonClicked());
        initGsonBuilder();
    }

    private void initGsonBuilder() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Transaction.class, new TransactionAdapter());
        gsonBuilder.registerTypeAdapter(Price.class, new PriceAdapter());
        gsonBuilder.registerTypeAdapter(Item.class, new ItemAdapter());
    }

    private void onConnectButtonClicked() {
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
        logFileTailer = new LogFileTailer(fileDialogController.chooseFileTextField.getText(), this::handleNewLine);
        logFileTailer.start();
    }

    private void handleNewLine(String line) {
        if (MessageParseUtil.matchesItemBuyPattern(line)) {
            var transaction = MessageParseUtil.getTransactionForItem(line);

            Gson gson = gsonBuilder.create();
            String json = gson.toJson(transaction);
            SocketClient.sendMessage(json);
        }
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