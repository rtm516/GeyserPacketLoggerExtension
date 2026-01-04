package org.geyser.extension.packetlogger.types.messages;

import org.geyser.extension.packetlogger.types.PacketSide;
import org.geysermc.geyser.api.network.MessageDirection;

import java.time.Instant;

public record PacketData(
    String connectionId,
    Instant time,
    PacketSide side,
    MessageDirection direction,
    String packetName,
    int packetId,
    Object packetData
) {
}
