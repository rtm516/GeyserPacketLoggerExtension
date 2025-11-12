package org.geyser.extension.packetlogger.utils;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.geyser.extension.packetlogger.Constants;
import org.geyser.extension.packetlogger.types.PacketDirection;
import org.geyser.extension.packetlogger.types.PacketSide;
import org.geyser.extension.packetlogger.types.WebSocketMessage;
import org.geyser.extension.packetlogger.types.PacketData;
import org.geyser.extension.packetlogger.web.WebApplication;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.extension.ExtensionLogger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PacketLogger {
    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss:SSS").withZone(ZoneId.systemDefault());;

    private final Path sessionsFolder;
    private final ExtensionLogger logger;
    private final GeyserConnection connection;
    private final WebApplication application;
    private final long startedAt;

    private FileWriter packetLogWriter; // TODO Look at not using FileWriter directly for performance
    private boolean hasBeenRenamed;

    public PacketLogger(Path sessionsFolder, GeyserConnection connection, ExtensionLogger logger, WebApplication application) {
        this.sessionsFolder = sessionsFolder;
        this.logger = logger;
        this.connection = connection;
        this.application = application;

        this.startedAt = System.currentTimeMillis();
        this.hasBeenRenamed = false;

        try {
            String fileName = connection.hashCode() + "-" + startedAt + ".log";
            packetLogWriter = new FileWriter(sessionsFolder.resolve(fileName).toFile());
        } catch (IOException e) {
            this.logger.error("Failed to create packet log writer for " + connection.bedrockUsername() + ": " + e.getMessage());
        }
    }

    // This is pretty cursed but gives us nice file names
    private void moveLog() {
        hasBeenRenamed = true;
        try {
            String oldFileName = connection.hashCode() + "-" + startedAt + ".log";
            String newFileName = connection.bedrockUsername() + "-" + startedAt + ".log";

            synchronized (packetLogWriter) {
                // Flush current
                packetLogWriter.flush();
                packetLogWriter.close();

                // Move the file to the new location
                Files.move(
                    sessionsFolder.resolve(oldFileName),
                    sessionsFolder.resolve(newFileName),
                    StandardCopyOption.REPLACE_EXISTING
                );

                // Reopen the writer in append mode
                packetLogWriter = new FileWriter(sessionsFolder.resolve(newFileName).toFile(), true);
            }
        } catch (IOException e) {
            this.logger.error("Failed to move packet log writer for " + connection.bedrockUsername() + ": " + e.getMessage());
        }
    }

    public void log(PacketSide side, PacketDirection direction, BedrockPacket packet) {
        if (!hasBeenRenamed && packet instanceof PlayStatusPacket) {
            moveLog();
        }

        String packetName = packet.getClass().getSimpleName();

        if (Constants.IGNORED_PACKETS.contains(packetName)) return;

        try {
            Instant time = Instant.now();
            synchronized (packetLogWriter) {
                packetLogWriter.write("[%s] [%s] [%s] - %s%n".formatted(
                    TIME_FORMAT.format(time),
                    side.toString().toUpperCase(Locale.ROOT),
                    direction.toString().toUpperCase(Locale.ROOT),
                    packet
                ));
            }
            application.broadcastMessage(new WebSocketMessage("packet", new PacketData(time, side, direction, packetName, 0, packet)));
        } catch (IOException e) {
            this.logger.error("Failed to log packet: " + e.getMessage());
        }
    }

    public void close() throws IOException {
        if (packetLogWriter != null) {
            packetLogWriter.flush();
            packetLogWriter.close();
        }
    }
}
