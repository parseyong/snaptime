package me.snaptime.component.file.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.snaptime.component.file.PhotoComponent;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.snap.dto.file.PhotoInfo;
import me.snaptime.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoComponentImpl implements PhotoComponent {

    @Value("${fileSystemPath}")
    private String FOLDER_PATH;

    @Override
    public byte[] findPhoto(String fileName) {

        String filePath = FOLDER_PATH + fileName;
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_FIND_FAIL);
        }
    }

    @Override
    public void deletePhoto(String fileName) {

        String filePath = FOLDER_PATH + fileName;
        try {
            Path path = Paths.get(filePath);
            Files.delete(path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_DELETE_FAIL);
        }
    }

    @Override
    public void updatePhoto(String fileName, byte[] fileBytes) {

        String filePath = FOLDER_PATH + fileName;
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_ADD_FAILE);
        }
    }

    @Override
    public PhotoInfo addPhoto(String originalFileName, byte[] fileBytes) {

        // UUID를 통한 파일명 생성
        String generatedName = FileNameGenerator.generatorName(originalFileName);

        // 파일이 저장될 경로 생성
        String filePath = FOLDER_PATH + generatedName;

        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.FILE_ADD_FAILE);
        }

        return PhotoInfo.builder()
                .filePath(filePath)
                .fileName(generatedName)
                .build();
    }
}
