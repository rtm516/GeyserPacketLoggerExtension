package org.geyser.extension.packetlogger.types;

public enum PacketSide {
    BEDROCK("Bedrock"),
    JAVA("Java");

    private final String displayName;

    PacketSide(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
