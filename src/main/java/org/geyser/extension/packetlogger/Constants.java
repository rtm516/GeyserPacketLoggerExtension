package org.geyser.extension.packetlogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import org.geyser.extension.packetlogger.gson.ByteBufAdapter;
import org.geyser.extension.packetlogger.gson.ByteBufferAdapter;
import org.geyser.extension.packetlogger.gson.ColorAdapter;
import org.geyser.extension.packetlogger.gson.InstantConverter;
import org.geyser.extension.packetlogger.gson.MetadataTypeSerializer;
import org.geyser.extension.packetlogger.gson.OptionalIntAdapter;
import org.geyser.extension.packetlogger.gson.OptionalSerializer;
import org.geyser.extension.packetlogger.gson.RSAPublicKeyAdapter;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataType;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class Constants {
    // Merge Adventure's Gson with our own adapters
    public static final Gson GSON = GsonComponentSerializer.gson().toBuilder()
        .editOptions(builder -> {
            builder
                .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false) // Prevent UUIDs from being serialized as int arrays
                .build();
        })
        .build()
        .populator()
        .apply(new GsonBuilder() // Add our custom adapters and settings
            .registerTypeAdapter(Instant.class, new InstantConverter())
            .registerTypeAdapter(ByteBuffer.class, new ByteBufferAdapter())
            .registerTypeAdapter(ByteBuf.class, new ByteBufAdapter())
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .registerTypeAdapter(OptionalInt.class, new OptionalIntAdapter())
            .registerTypeHierarchyAdapter(RSAPublicKey.class, new RSAPublicKeyAdapter())
            .registerTypeAdapter(Optional.class, new OptionalSerializer<>())
            .registerTypeHierarchyAdapter(MetadataType.class, new MetadataTypeSerializer<>())
            .disableHtmlEscaping()
        ).create();

    public static final List<String> IGNORED_PACKETS = List.of(
        // Bedrock
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
        "SetEntityMotionPacket",

        // Java
        "ClientboundLevelChunkWithLightPacket",

        "ClientboundBlockUpdatePacket",
        "ClientboundMoveEntityPosPacket",
        "ClientboundEntityPositionSyncPacket",
        "ClientboundRotateHeadPacket",
        "ClientboundSetEntityMotionPacket",
        "ClientboundSectionBlocksUpdatePacket",
        "ClientboundMoveEntityPosRotPacket",
        "ServerboundClientTickEndPacket",
        "ServerboundMovePlayerPosPacket",
        "ServerboundMovePlayerRotPacket",
        "ClientboundMoveEntityRotPacket"
    );
}
