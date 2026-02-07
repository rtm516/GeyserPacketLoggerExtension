package org.geyser.extension.packetlogger.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

public class RSAPublicKeyAdapter extends TypeAdapter<RSAPublicKey> {
    @Override
    public void write(JsonWriter out, RSAPublicKey value) throws IOException {
        out.beginObject();
        out.name("algorithm").value(value.getAlgorithm());
        out.name("modulus").value(value.getModulus().toString(16));
        out.name("exponent").value(value.getPublicExponent().toString(16));
        out.endObject();
    }

    @Override
    public RSAPublicKey read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Deserialization of RSAPublicKey is not supported.");
    }
}
