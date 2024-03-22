package de.realwhimsy.afktraderpoe;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class LogFileTailer {
    private static volatile LogFileTailer instance;

    private final String filePath;
    private volatile boolean running;
    private Thread thread;
    private Consumer<String> handleNewLineConsumer;

    private LogFileTailer(String filePath, Consumer<String> handleNewLineConsumer) {
        this.filePath = filePath;
        this.handleNewLineConsumer = handleNewLineConsumer;
    }

    public static LogFileTailer getInstance(String filePath, Consumer<String> handleNewLineConsumer) {
        if (instance == null) {
            synchronized (LogFileTailer.class) {
                if (instance == null) {
                    instance = new LogFileTailer(filePath, handleNewLineConsumer);
                }
            }
        }
        return instance;
    }

    public void start() {
        if (thread == null || !thread.isAlive()) {
            running = true;
            thread = new Thread(this::tailLogFile);
            thread.start();
        }
    }

    public void stop() {
        running = false;
    }

    private void tailLogFile() {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long length = file.length();
            long position = Math.max(0, length - 512); // Start reading from the last 512 bytes

            file.seek(position);

            String line;
            while ((line = file.readLine()) != null) {
                // do nothing
//                handleNewLineConsumer.accept(line);
            }

            while (running) {
                if (file.length() > length) {
                    // File has grown, read new lines
                    position = length;
                    length = file.length();
                    file.seek(position);
                    while ((line = file.readLine()) != null) {
                        handleNewLineConsumer.accept(line);
                    }
                }
                Thread.sleep(1000); // Wait for 1 second before checking for new content
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
