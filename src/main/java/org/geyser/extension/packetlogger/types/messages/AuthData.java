package org.geyser.extension.packetlogger.types.messages;

public record AuthData(
    String connectionId,
    String username
) {
}
