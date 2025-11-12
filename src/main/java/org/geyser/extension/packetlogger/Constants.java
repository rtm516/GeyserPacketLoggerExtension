package org.geyser.extension.packetlogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import org.geyser.extension.packetlogger.utils.ByteBufAdapter;
import org.geyser.extension.packetlogger.utils.ByteBufferAdapter;
import org.geyser.extension.packetlogger.utils.ColorAdapter;
import org.geyser.extension.packetlogger.utils.InstantConverter;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;

public class Constants {
    public static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantConverter())
        .registerTypeAdapter(ByteBuffer.class, new ByteBufferAdapter())
        .registerTypeAdapter(ByteBuf.class, new ByteBufAdapter())
        .registerTypeAdapter(Color.class, new ColorAdapter())
        .disableHtmlEscaping()
        .create();

    public static final List<String> IGNORED_PACKETS = List.of(
        "NetworkStackLatencyPacket",
        "LevelChunkPacket",
        "MovePlayerPacket",
        "PlayerAuthInputPacket",
        "NetworkChunkPublisherUpdatePacket",
        "ClientCacheBlobStatusPacket",
        "ClientCacheMissResponsePacket",

        "UpdateBlockPacket",
        "MoveEntityDeltaPacket",
        "MoveEntityAbsolutePacket",
        "SetEntityMotionPacket"
    );
}
