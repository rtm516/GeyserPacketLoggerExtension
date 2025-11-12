package org.geyser.extension.packetlogger.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A Gson adapter for converting {@link ByteBuffer} objects.
 */
public class ByteBufferAdapter extends TypeAdapter<ByteBuffer> {
    @Override
    public void write(JsonWriter out, ByteBuffer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Write just the length as a string
            out.value("ByteBuffer[length=" + value.capacity() + "]");
        }
    }

    @Override
    public ByteBuffer read(JsonReader in) throws IOException {
        in.skipValue();
        return null;
    }
}
