package org.geyser.extension.packetlogger.types.messages;

import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.geyser.extension.packetlogger.types.PacketDirection;
import org.geyser.extension.packetlogger.types.PacketSide;

import java.time.Instant;

public record PacketData(
    String connectionId,
    Instant time,
    PacketSide side,
    PacketDirection direction,
    String packetName,
    int packetId,
    BedrockPacket packetData
) {
}
