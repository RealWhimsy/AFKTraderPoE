package de.realwhimsy.afktraderpoe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.realwhimsy.afktraderpoe.datamodel.*;
import de.realwhimsy.afktraderpoe.datamodel.TypeAdapters.*;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.concurrent.ScheduledExecutorService;

public class SocketClient {

    private String ipAddress;
    private int port;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Consumer<Boolean> connectionStatusChangedCallback;
    private Consumer<Reply> onReplyReceivedCallback;
    private boolean isRunning = false;
    private static SocketClient instance;
    private ScheduledExecutorService heartbeatTimer;
    private ScheduledExecutorService heartbeatExecutor;
    private Gson gson;


    private SocketClient() {}

    public void init() {
        initGson();
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket(ipAddress, port);
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isRunning = true;

                // Start a separate thread to read messages from the server
                if (connectionStatusChangedCallback != null) {
                    new Thread(this::receiveMessages).start();
                }

                startHeartbeatTimer();
                startHeartbeatExecutor();
//                startServerListener();
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (ConnectException ex) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connection failed");
            alert.setContentText("Could not connect to IP " + ipAddress + " on port " + port +
                    "\nPlease make sure the entered IP and port match the values displayed in the AfkPoeTrader mobile app.");
            alert.show();
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initGson() {
        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Transaction.class, new TransactionAdapter());
        gsonBuilder.registerTypeAdapter(Price.class, new PriceAdapter());
        gsonBuilder.registerTypeAdapter(Item.class, new ItemAdapter());
        gsonBuilder.registerTypeAdapter(Reply.class, new ReplyAdapter());
        gsonBuilder.registerTypeAdapter(AppResponse.class, new AppResponseAdapter());
        gson = gsonBuilder.create();
    }

    private void startHeartbeatExecutor() {
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(this::sendHeartbeat, 0, 3, TimeUnit.SECONDS);
    }

    private void sendHeartbeat() {
        sendMessage("heartbeat");
    }

    public void sendMessage(String message) {
        if (socket != null && !socket.isClosed() && writer != null) {
            System.out.println("Sent message: " + message);
            writer.println(message);
        } else {
            System.out.println("Error: Socket connection is closed.");
        }
    }

    private void stopHeartbeatExecutor() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdown();
        }
    }


    private void receiveMessages() {
        try {
            String message;
            while (isRunning && (message = reader.readLine()) != null) {
                var appResponse = gson.fromJson(message, AppResponse.class);

                switch (appResponse.getAction()) {
                    case "heartbeat" -> resetHeartbeatTimer();
                    case "disconnected" -> connectionStatusChangedCallback.accept(false);
                    case "connected" -> connectionStatusChangedCallback.accept(true);
                    case "reply" -> {
                        var reply = gson.fromJson(appResponse.getContent(), Reply.class);
                        onReplyReceivedCallback.accept(reply);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startHeartbeatTimer() {
        heartbeatTimer = Executors.newSingleThreadScheduledExecutor();
        heartbeatTimer.scheduleAtFixedRate(() -> {
                    connectionStatusChangedCallback.accept(false);
                    closeConnection();
                },
                10, 10, TimeUnit.SECONDS); // Check every 10 seconds for heartbeat
    }

    private void resetHeartbeatTimer() {
        // Cancel the previous heartbeat timer task and reschedule it
        heartbeatTimer.shutdown();
        startHeartbeatTimer();
    }

    private void stopHeartbeatTimer() {
        if (heartbeatTimer != null) {
            heartbeatTimer.shutdown();
        }
    }

    public void closeConnection() {
        if (socket == null) {
            return;
        }

        try {
            isRunning = false;
            stopHeartbeatExecutor();
            stopHeartbeatTimer();
            writer.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnectionStatusChangedCallback(Consumer<Boolean> connectionStatusChangedCallback) {
        this.connectionStatusChangedCallback = connectionStatusChangedCallback;
    }

    public void setOnReplyReceivedCallback(Consumer<Reply> onReplyReceivedCallback) {
        this.onReplyReceivedCallback = onReplyReceivedCallback;
    }

    public static SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }

        return instance;
    }

    public void sendTransaction(String line) {
        var transaction = MessageParseUtil.getTransactionForItem(line);

        String json = gson.toJson(transaction);
        sendMessage(json);
    }
}
