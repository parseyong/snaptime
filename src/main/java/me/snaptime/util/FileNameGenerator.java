package me.snaptime.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileNameGenerator {

    public static String generatorName(String fileName) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));
        String uuId = UUID.randomUUID().toString();
        return currentTime + "_" + uuId + "_" + fileName;
    }
}
