package de.realwhimsy.afktraderpoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class SocketClient {

    private final String ipAddress;
    private final int port;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Consumer<String> messageReceivedCallback;
    private static boolean isRunning = false;


    public SocketClient(String ipAddress, int port, Consumer<String> messageReceivedCallback) {
        this.ipAddress = ipAddress;
        this.port = port;
        SocketClient.messageReceivedCallback = messageReceivedCallback;
    }

    public void init() {
        try {
            socket = new Socket(ipAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isRunning = true;

            // Start a separate thread to read messages from the server
            new Thread(SocketClient::receiveMessages).start();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        if (socket != null && !socket.isClosed() && out != null) {
            out.println(message);
        } else {
            System.out.println("Error: Socket connection is closed.");
        }
    }

    private static void receiveMessages() {
        try {
            String message;
            while (isRunning && (message = in.readLine()) != null) {
                messageReceivedCallback.accept(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (socket == null) {
            return;
        }

        try {
            isRunning = false;
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
