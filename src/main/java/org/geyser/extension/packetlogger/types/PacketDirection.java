package org.geyser.extension.packetlogger.types;

public enum PacketDirection {
    CLIENTBOUND("Client Bound"),
    SERVERBOUND("Server Bound");

    private final String displayName;

    PacketDirection(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
