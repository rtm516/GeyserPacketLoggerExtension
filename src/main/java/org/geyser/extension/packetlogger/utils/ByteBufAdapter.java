package org.geyser.extension.packetlogger.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * A Gson adapter for converting {@link ByteBuf} objects.
 */
public class ByteBufAdapter extends TypeAdapter<ByteBuf> {
    @Override
    public void write(JsonWriter out, ByteBuf value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Write just the length as a string
            out.value("ByteBuf[length=" + value.readableBytes() + "]");
        }
    }

    @Override
    public ByteBuf read(JsonReader in) throws IOException {
        in.skipValue();
        return null;
    }
}
