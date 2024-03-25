package de.realwhimsy.afktraderpoe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jna.platform.win32.WinDef;
import de.realwhimsy.afktraderpoe.datamodel.*;
import de.realwhimsy.afktraderpoe.datamodel.TypeAdapters.SettingsAdapter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import javafx.scene.input.KeyCode;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import java.awt.*;
import java.awt.datatransfer.*;


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

    @FXML
    private TextField windowNameTextfield;

    private LogFileTailer logFileTailer;
    private boolean isConnected = false;
    private final String SETTINGS_FILE_PATH = "settings.json";

    @FXML
    public void initialize() {
        File file = new File(SETTINGS_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder jsonData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }

                // Parse JSON data using Gson
                Gson gson = new GsonBuilder().registerTypeAdapter(Settings.class, new SettingsAdapter()).create();
                Settings settings = gson.fromJson(jsonData.toString(), Settings.class);
                deviceIpTextfield.setText(settings.getIpAddress());
                portTextfield.setText(settings.getPort());
                fileDialogController.chooseFileTextField.setText(settings.getClientTxtPath());
                windowNameTextfield.setText(settings.getWindowName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            deviceIpTextfield.setText("192.168.xxx.xx");
            portTextfield.setText("4747");
            windowNameTextfield.setText("Path of Exile");
        }

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
            saveSettings();
            logFileTailer = LogFileTailer.getInstance(fileDialogController.chooseFileTextField.getText(), this::handleNewLine);
            logFileTailer.start();
        }
    }

    private void saveSettings() {
        var ipAddress = deviceIpTextfield.getText();
        var port = portTextfield.getText();
        var clientTxtPath = fileDialogController.chooseFileTextField.getText();
        var windowName = windowNameTextfield.getText();
        var settings = new Settings(ipAddress, port, clientTxtPath, windowName);
        var gson = new GsonBuilder().registerTypeAdapter(Settings.class, new SettingsAdapter()).setPrettyPrinting().create();
        var settingsJson = gson.toJson(settings, Settings.class);

        // Write JSON object to file
        try (FileWriter file = new FileWriter(SETTINGS_FILE_PATH)) {
            file.write(settingsJson);
        } catch (IOException e) {
            e.printStackTrace();
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
        socketClient.setOnReplyReceivedCallback(this::onReplyReceived);
        socketClient.init();
    }

    private void onReplyReceived(Reply reply) {
        System.out.println("Received reply: Target: " + reply.getWhisperTarget() + ", Message: " + reply.getMessage());
        HWND poeWindow = findWindowHandle("Path of Exile");

        if (poeWindow != null) {
            User32.INSTANCE.ShowWindow(poeWindow, User32.SW_RESTORE);
            User32.INSTANCE.SetForegroundWindow(poeWindow);

            typeMessage(reply.getWhisperTarget(), reply.getMessage());
        }
    }

    private void typeMessage(String target, String message) {

        setClipboardContents("@" + target + " " + message);

        try {
            Robot robot = new Robot();
            robot.delay(2);
            // open the chat window
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(20);
            simulateDeleteAllText(robot);
            simulatePaste(robot);

            // send the message
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void simulatePaste(Robot robot) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public static void setClipboardContents(String content) {
        StringSelection stringSelection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * Makes the robot press "Ctrl + A" and then Backspace, to simulate deleting all text in the message box
     */
    private void simulateDeleteAllText(Robot robot) {
        // Simulate Ctrl+A to select all text
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        // Simulate Backspace to delete the selected text
        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    // Function to find the window handle by its title
    private static WinDef.HWND findWindowHandle(String windowTitle) {
        char[] buffer = new char[1024];
        WinDef.HWND[] hwnd = {null};
        User32.INSTANCE.EnumWindows((hWnd, pointer) -> {
            User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.length);
            String title = Native.toString(buffer);
            if (title.trim().equals(windowTitle)) {
                hwnd[0] = hWnd;
                return false;
            }
            return true;
        }, null);
        return hwnd[0];
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