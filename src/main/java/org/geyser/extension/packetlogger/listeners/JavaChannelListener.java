package org.geyser.extension.packetlogger.listeners;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.geyser.extension.packetlogger.types.PacketSide;
import org.geyser.extension.packetlogger.utils.PacketLogger;
import org.geyser.extension.packetlogger.utils.ProtocolStateConverter;
import org.geysermc.geyser.api.event.bedrock.SessionDefineNetworkChannelsEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.network.MessageDirection;
import org.geysermc.geyser.api.network.PacketChannel;
import org.geysermc.geyser.api.network.message.Message;
import org.geysermc.geyser.api.network.message.MessageBuffer;
import org.geysermc.geyser.api.network.message.MessageCodec;
import org.geysermc.geyser.api.network.message.MessageHandler;
import org.geysermc.mcprotocollib.network.codec.PacketDefinition;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.network.packet.PacketRegistry;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftCodec;
import org.geysermc.mcprotocollib.protocol.data.ProtocolState;

import java.lang.reflect.Field;

public class JavaChannelListener {
    private static Field clientboundField;
    private static Field serverboundField;

    static {
        try {
            clientboundField = PacketRegistry.class.getDeclaredField("clientbound");
            clientboundField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e); // TODO
        }

        try {
            serverboundField = PacketRegistry.class.getDeclaredField("serverbound");
            serverboundField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    public static void setup(Extension extension, SessionDefineNetworkChannelsEvent event, PacketLogger packetLog) throws NoSuchFieldException, IllegalAccessException {
        for (ProtocolState state : ProtocolState.values()) {
            setupState(extension, event, packetLog, state);
        }
    }

    private static void setupState(Extension extension,SessionDefineNetworkChannelsEvent event, PacketLogger packetLog, ProtocolState protocolState) throws NoSuchFieldException, IllegalAccessException {
        PacketRegistry packetRegistry = MinecraftCodec.CODEC.getCodec(protocolState); // TODO Double check we get this from geyser not shaded

        int clientboundPacketCount = ((Int2ObjectMap<PacketDefinition<? extends Packet>>) clientboundField.get(packetRegistry)).size();
        int serverboundPacketCount = ((Int2ObjectMap<PacketDefinition<? extends Packet>>) serverboundField.get(packetRegistry)).size();
        int packetCount = clientboundPacketCount + serverboundPacketCount;

        extension.logger().info("Defining " + packetCount + " channels for Java " + MinecraftCodec.CODEC.getMinecraftVersion() + " (" + MinecraftCodec.CODEC.getProtocolVersion() + ") in state " + protocolState.name() + " for packet logging...");

        // Define clientbound packet channels
        for (int packetId = 0; packetId < clientboundPacketCount; packetId++) {
            PacketDefinition<? extends Packet> definition = packetRegistry.getClientboundDefinition(packetId);
            if (definition == null) continue; // Skip undefined packet IDs

            PacketChannel packetChannel = PacketChannel.java(extension, packetId, definition.getPacketClass());
            event.define(packetChannel, MessageCodec.provided(ByteBuf.class), Message.Packet.of(MessageBuffer.Wrapped::buffer, definition::newInstance))
                .protocolState(ProtocolStateConverter.of(protocolState))
                .clientbound(message -> {
                    packetLog.log(PacketSide.JAVA, MessageDirection.CLIENTBOUND, message.packet(), packetChannel.packetId());
                    return MessageHandler.State.UNHANDLED;
                })
                .register();
        }

        // Define serverbound packet channels
        for (int packetId = 0; packetId < serverboundPacketCount; packetId++) {
            PacketDefinition<? extends Packet> definition = packetRegistry.getServerboundDefinition(packetId);
            if (definition == null) continue; // Skip undefined packet IDs

            PacketChannel packetChannel = PacketChannel.java(extension, packetId, definition.getPacketClass());
            event.define(packetChannel, MessageCodec.provided(ByteBuf.class), Message.Packet.of(MessageBuffer.Wrapped::buffer, definition::newInstance))
                .protocolState(ProtocolStateConverter.of(protocolState))
                .serverbound(message -> {
                    packetLog.log(PacketSide.JAVA, MessageDirection.SERVERBOUND, message.packet(), packetChannel.packetId());
                    return MessageHandler.State.UNHANDLED;
                })
                .register();
        }
    }
}
