package org.geyser.extension.packetlogger;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketDefinition;
import org.cloudburstmc.protocol.bedrock.codec.v859.Bedrock_v859;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.geyser.extension.packetlogger.types.messages.AuthData;
import org.geyser.extension.packetlogger.types.PacketDirection;
import org.geyser.extension.packetlogger.types.PacketSide;
import org.geyser.extension.packetlogger.types.WebSocketMessage;
import org.geyser.extension.packetlogger.types.messages.GenericData;
import org.geyser.extension.packetlogger.utils.PacketLogger;
import org.geyser.extension.packetlogger.web.WebApplication;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.event.bedrock.SessionDefineNetworkChannelsEvent;
import org.geysermc.geyser.api.event.bedrock.SessionDisconnectEvent;
import org.geysermc.geyser.api.event.bedrock.SessionJoinEvent;
import org.geysermc.geyser.api.event.bedrock.SessionLoginEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserShutdownEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.network.NetworkChannel;
import org.geysermc.geyser.api.network.message.Message;
import org.geysermc.geyser.api.network.message.MessageHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The main class of your extension - must implement extension, and be in the extension.yml file.
 * See {@link Extension} for available methods - for example to get the path to the configuration folder.
 */
public class PacketLoggerExtension implements Extension {
    private Map<String, PacketLogger> packetLoggers = new HashMap<>();
    private Path sessionsFolder;
    private WebApplication webApplication;

    /*
     * You can use the GeyserPostInitializeEvent to run anything after Geyser fully initialized and is ready to accept bedrock player connections.
     */
    @Subscribe
    public void onPostInitialize(GeyserPostInitializeEvent event) throws IOException {
        this.logger().info("Loading %s...".formatted(this.description().name()));

        sessionsFolder = this.dataFolder().resolve("sessions");
        sessionsFolder.toFile().mkdirs();

        webApplication = new WebApplication(this.logger());
        webApplication.start();
    }

    /*
     * Registering custom items/blocks, or adding resource packs (and basically all other events that are fired before Geyser initializes fully)
     * are done in their respective events. See below for an example:
     */
    @Subscribe
    public void onDefineChannels(SessionDefineNetworkChannelsEvent event) throws NoSuchFieldException, IllegalAccessException {
        BedrockCodec codec = Bedrock_v859.CODEC;

        Field packetsByIdField = BedrockCodec.class.getDeclaredField("packetsById");
        packetsByIdField.setAccessible(true);

        int packetCount = ((BedrockPacketDefinition<? extends BedrockPacket>[]) packetsByIdField.get(codec)).length;

        this.logger().info("Defining " + packetCount + " channels for " + codec.getMinecraftVersion() + " (" + codec.getProtocolVersion() + ") for packet logging...");

        PacketLogger packetLog = new PacketLogger(sessionsFolder, event.connection(), this.logger(), webApplication);
        packetLoggers.put(String.valueOf(event.connection().hashCode()), packetLog);

        for (int packetId = 0; packetId < packetCount; packetId++) {
            BedrockPacketDefinition<? extends BedrockPacket> definition = codec.getPacketDefinition(packetId);
            if (definition == null) continue; // Skip undefined packet IDs
            BedrockPacket packet = definition.getFactory().get();

            NetworkChannel packetChannel = NetworkChannel.packet("packetlogger-" + packet.getPacketType().name().toLowerCase(), packetId, packet.getClass());
            event.define(packetChannel, Message.Packet.of(() -> packet))
                .serverbound(message -> {
                    packetLog.log(PacketSide.BEDROCK, PacketDirection.SERVERBOUND, message.packet());
                    return MessageHandler.State.UNHANDLED;
                })
                .clientbound(message -> {
                    packetLog.log(PacketSide.BEDROCK, PacketDirection.CLIENTBOUND, message.packet());
                    return MessageHandler.State.UNHANDLED;
                })
                .register();
        }
    }

    @Subscribe
    public void onSessionJoin(SessionJoinEvent event) {
        webApplication.broadcastMessage(new WebSocketMessage("join", new GenericData(event.connection().hashCode())));
    }

    @Subscribe
    public void onSessionLogin(SessionLoginEvent event) {
        webApplication.broadcastMessage(new WebSocketMessage("auth", new AuthData(String.valueOf(event.connection().hashCode()), event.connection().bedrockUsername())));
    }

    @Subscribe
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        GeyserConnection connection = event.connection();
        String key = String.valueOf(connection.hashCode());
        PacketLogger packetLogger = packetLoggers.get(key);

        webApplication.broadcastMessage(new WebSocketMessage("leave", new GenericData(key)));

        if (packetLogger != null) {
            try {
                packetLogger.close();
                packetLoggers.remove(key);
            } catch (IOException e) {
                this.logger().error("Failed to flush and close packet logger for " + connection.bedrockUsername() + ": " + e.getMessage());
            }
        }
    }

    @Subscribe
    public void onGeyserShutdown(GeyserShutdownEvent event) throws IOException {
        for (PacketLogger packetLogger : packetLoggers.values()) {
            try {
                packetLogger.close();
            } catch (IOException e) {
                this.logger().error("Failed to flush and close packet logger: " + e.getMessage());
            }
        }

        webApplication.stop();
    }
}
