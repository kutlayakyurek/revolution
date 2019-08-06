package com.ka.revolution.util;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@UtilityClass
public class FileUtil {

    private static final Gson gson = new Gson();

    public static String convertInputStreamToString(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            log.debug("Input stream is null");
            return null;
        }

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        // StandardCharsets.UTF_8.name() > JDK 7
        return result.toString(StandardCharsets.UTF_8.name());
    }

    public static <T> T convertJsonStreamToObject(final InputStream inputStream, final Class<T> type)
            throws IOException {

        final String json = convertInputStreamToString(inputStream);
        return gson.fromJson(json, type);
    }

    public static String convertObjectToJson(final Object object) {
        if (object == null) {
            log.debug("Object is null");
            return null;
        }

        return gson.toJson(object);
    }

}
