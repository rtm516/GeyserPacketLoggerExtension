package org.geyser.extension.packetlogger.types;

public record WebSocketMessage(
    String type,
    Object data) {
}
