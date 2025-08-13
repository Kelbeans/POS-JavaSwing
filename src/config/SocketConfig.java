package config;

import java.io.*;
import java.net.Socket;

public class SocketConfig {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private volatile boolean connected = false;

    public SocketConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized boolean connect() {
        if (connected) {
            return true;
        }

        try {
            socket = new Socket(host, port);
            OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(outputStream);
            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStream);
            connected = true;
            System.out.println("Connected to the Virtual Journal Server at " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            connected = false;
            return false;
        }
    }

    public synchronized void disconnect() {
        if (!connected) {
            return;
        }

        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        } finally {
            connected = false;
            socket = null;
            bufferedWriter = null;
            bufferedReader = null;
            System.out.println("Disconnected from server");
        }
    }

    public synchronized boolean sendLog(String logMessage) {
        if (!connected && !connect()) {
            return false;
        }

        try {
            bufferedWriter.write(logMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String response = bufferedReader.readLine();
            if (response != null) {
                System.out.println("Server response: " + response);
                return true;
            } else {
                System.out.println("No response from server - connection may be closed");
                connected = false;
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error sending log: " + e.getMessage());
            connected = false;
            // Try to reconnect on next send
            return false;
        }
    }

    public void sendLogAsync(String logMessage) {
        new Thread(() -> sendLog(logMessage)).start();
    }

    public boolean isConnected() {
        return connected;
    }
}