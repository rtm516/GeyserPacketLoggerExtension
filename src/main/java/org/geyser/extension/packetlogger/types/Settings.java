package org.geyser.extension.packetlogger.types;

import java.util.List;

// TODO Replace this with a real system
public record Settings(
    SettingsValue logToFile,
    SettingsValue enableWebserver,
    SettingsValue webserverPort,
    SettingsValue ignoredPackets
) {
    public Settings(boolean logToFile, boolean enableWebserver, int webserverPort, List<String> ignoredPackets) {
        this(
            new SettingsValue(logToFile, "Whether to log packets to a file", "boolean"),
            new SettingsValue(enableWebserver, "Whether to enable the webserver for live packet viewing", "boolean"),
            new SettingsValue(webserverPort, "The port the webserver will run on", "integer"),
            new SettingsValue(ignoredPackets, "List of packet class names to ignore", "list")
        );
    }

    public record SettingsValue(
        Object value,
        String description,
        String type
    ) {
    }
}
