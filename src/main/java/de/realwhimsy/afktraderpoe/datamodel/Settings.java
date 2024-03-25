package de.realwhimsy.afktraderpoe.datamodel;

public class Settings {
    private String ipAddress;
    private String port;
    private String clientTxtPath;
    private String windowName;

    public Settings(String ipAddress, String port, String clientTxtPath, String windowName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientTxtPath = clientTxtPath;
        this.windowName = windowName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getClientTxtPath() {
        return clientTxtPath;
    }

    public String getWindowName() {
        return windowName;
    }
}
