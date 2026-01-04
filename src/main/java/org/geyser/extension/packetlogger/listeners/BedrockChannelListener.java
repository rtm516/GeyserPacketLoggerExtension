package org.geyser.extension.packetlogger.listeners;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketDefinition;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.geyser.extension.packetlogger.types.PacketSide;
import org.geyser.extension.packetlogger.utils.PacketLogger;
import org.geysermc.geyser.api.event.bedrock.SessionDefineNetworkChannelsEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.network.PacketChannel;
import org.geysermc.geyser.api.network.message.Message;
import org.geysermc.geyser.api.network.message.MessageHandler;
import org.geysermc.geyser.network.GameProtocol;

import java.lang.reflect.Field;

public class BedrockChannelListener {
    private static final Field packetsByIdField;

    static {
        try {
            packetsByIdField = BedrockCodec.class.getDeclaredField("packetsById");
            packetsByIdField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    public static void setup(Extension extension, SessionDefineNetworkChannelsEvent event, PacketLogger packetLog) throws NoSuchFieldException, IllegalAccessException {
        // TODO Get this using some form of API when available
        // TODO Change this post login incase they use a different protocol version
        BedrockCodec codec = GameProtocol.getBedrockCodec(GameProtocol.DEFAULT_BEDROCK_PROTOCOL);

        int packetCount = ((BedrockPacketDefinition<? extends BedrockPacket>[]) packetsByIdField.get(codec)).length;

        extension.logger().info("Defining " + packetCount + " channels for Bedrock " + codec.getMinecraftVersion() + " (" + codec.getProtocolVersion() + ") for packet logging...");

        for (int packetId = 0; packetId < packetCount; packetId++) {
            BedrockPacketDefinition<? extends BedrockPacket> definition = codec.getPacketDefinition(packetId);
            if (definition == null) continue; // Skip undefined packet IDs
            BedrockPacket packet = definition.getFactory().get();

            PacketChannel packetChannel = PacketChannel.bedrock(extension, packetId, packet.getClass());
            event.define(packetChannel, Message.Packet.of(() -> packet))
                .bidirectional((message, direction) -> {
                    packetLog.log(PacketSide.BEDROCK, direction, message.packet(), packetChannel.packetId());
                    return MessageHandler.State.UNHANDLED;
                })
                .register();
        }
    }
}
