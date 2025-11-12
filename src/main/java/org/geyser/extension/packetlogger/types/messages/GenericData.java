package org.geyser.extension.packetlogger.types.messages;

public record GenericData(
    String connectionId
) {
    public GenericData(int connectionId) {
        this(String.valueOf(connectionId));
    }
}
