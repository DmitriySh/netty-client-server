package ru.shishmakov.server.helper;

import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

import java.util.Base64;
import java.util.UUID;

/**
 * Simple singleton to convert {@link Binary}s to their {@link UUID} representation.
 */
public enum BinaryConverterTo2UUID implements Converter<Binary, UUID> {
    INSTANCE;

    @Override
    public UUID convert(final Binary source) {
        return source == null ? null : buildUuid(source);
    }

    private UUID buildUuid(final Binary source) {
        final byte[] decodedBytes = Base64.getDecoder().decode(source.getData());
        final String temp = new String(decodedBytes);
        String msb = temp.substring(0, 16);
        String lsb = temp.substring(16, 32);
        msb = msb.substring(14, 16) + msb.substring(12, 14) + msb.substring(10, 12) + msb.substring(8, 10) +
                msb.substring(6, 8) + msb.substring(4, 6) + msb.substring(2, 4) + msb.substring(0, 2);
        lsb = lsb.substring(14, 16) + lsb.substring(12, 14) + lsb.substring(10, 12) + lsb.substring(8, 10) +
                lsb.substring(6, 8) + lsb.substring(4, 6) + lsb.substring(2, 4) + lsb.substring(0, 2);
        final String result = msb + lsb;

        final StringBuilder builder = new StringBuilder(result.length() + 4);
        builder.append(result.substring(0, 8)).append('-').append(result.substring(8, 12)).append('-').
                append(result.substring(12, 16)).append('-').append(result.substring(16, 20))
                .append('-').append(result.substring(20, 32));
        return UUID.fromString(builder.toString());
    }
}
