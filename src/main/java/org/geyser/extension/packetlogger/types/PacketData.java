package org.geyser.extension.packetlogger.types;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.time.Instant;

public record PacketData(
        Instant time,
        PacketSide side,
        PacketDirection direction,
        String packetName,
        int packetId,
        BedrockPacket packetData) {
}
