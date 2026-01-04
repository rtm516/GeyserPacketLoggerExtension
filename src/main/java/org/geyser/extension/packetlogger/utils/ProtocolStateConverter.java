package org.geyser.extension.packetlogger.utils;

import org.geysermc.geyser.api.network.ProtocolState;

public class ProtocolStateConverter {
    public static ProtocolState of(org.geysermc.mcprotocollib.protocol.data.ProtocolState state) {
        return switch (state) {
            case HANDSHAKE -> ProtocolState.HANDSHAKE;
            case STATUS -> ProtocolState.STATUS;
            case LOGIN -> ProtocolState.LOGIN;
            case CONFIGURATION ->  ProtocolState.CONFIGURATION;
            case GAME -> ProtocolState.GAME;
        };
    }
}
