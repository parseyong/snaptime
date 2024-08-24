package me.snaptime.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileNameGenerator {

    static String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));

    public static String generatorName(String fileName) {
        String uuId = UUID.randomUUID().toString();
        return currentTime + "_" + uuId + "_" + fileName;
    }
}
