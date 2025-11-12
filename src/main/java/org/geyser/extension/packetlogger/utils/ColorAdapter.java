package org.geyser.extension.packetlogger.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.netty.buffer.ByteBuf;

import java.awt.Color;
import java.io.IOException;

/**
 * A Gson adapter for converting {@link Color} objects.
 */
public class ColorAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter out, Color value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Write the color as hexadecimal RGBA
            String hexValue = String.format("#%02X%02X%02X", value.getRed(), value.getGreen(), value.getBlue());
            if (value.getAlpha() != 255) hexValue += String.format("%02X", value.getAlpha()); // Append alpha if not fully opaque
            out.value(hexValue);
        }
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        in.skipValue();
        return null;
    }
}
