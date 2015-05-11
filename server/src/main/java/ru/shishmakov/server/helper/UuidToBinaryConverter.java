package ru.shishmakov.server.helper;

import org.bson.BSON;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Simple singleton to convert {@link UUID}s to their {@link Binary} representation.
 */
public enum UuidToBinaryConverter implements Converter<UUID, Binary> {
    INSTANCE;

    @Override
    public Binary convert(final UUID source) {
        return source == null ? null : buildBinaryData(source);
    }

    private Binary buildBinaryData(final UUID source) {
        final String uuid = source.toString();
        String msb = uuid.substring(0, 16);
        String lsb = uuid.substring(16, 32);
        msb = msb.substring(14, 16) + msb.substring(12, 14) + msb.substring(10, 12) + msb.substring(8, 10) +
                msb.substring(6, 8) + msb.substring(4, 6) + msb.substring(2, 4) + msb.substring(0, 2);
        lsb = lsb.substring(14, 16) + lsb.substring(12, 14) + lsb.substring(10, 12) + lsb.substring(8, 10) +
                lsb.substring(6, 8) + lsb.substring(4, 6) + lsb.substring(2, 4) + lsb.substring(0, 2);
        final String result = msb + lsb;
        final String base64 = Base64.getEncoder().encodeToString(result.getBytes(StandardCharsets.UTF_8));
        return new Binary(BSON.B_UUID, base64.getBytes(StandardCharsets.UTF_8));
    }
}
