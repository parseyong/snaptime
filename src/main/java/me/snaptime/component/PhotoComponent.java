package me.snaptime.component;


import lombok.extern.slf4j.Slf4j;
import me.snaptime.exception.CustomException;
import me.snaptime.exception.ExceptionCode;
import me.snaptime.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class PhotoComponent {

    private final String folderPath;

    @Autowired
    public PhotoComponent(@Value("${fileSystemPath}") String folderPath){
        this.folderPath = folderPath;
    }

    /*
        사진을 가져옵니다.
        사진의 byte[]를 반환합니다.

        fileName : 가져올 사진의 파일명
    */
    public byte[] findPhoto(String fileName) {

        String filePath = folderPath + fileName;
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.PHOTO_FIND_FAIL);
        }
    }

    /*
        사진을 삭제합니다.

        fileName : 삭제할 사진의 파일명
    */
    public void deletePhoto(String fileName) {

        String filePath = folderPath + fileName;
        try {
            Path path = Paths.get(filePath);
            Files.delete(path);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.PHOTO_DELETE_FAIL);
        }
    }

    /*
        사진의 공개여부를 수정합니다. 기존사진을 암호화하여 저장하거나 복호화하여 저장할 떄 호출됩니다.

        fileName  : 수정할 사진의 파일명
        fileBytes : 암호화(복호화)된 사진의 바이트 데이터
    */
    public void updatePhotoVisibility(String fileName, byte[] fileBytes) {

        String filePath = folderPath + fileName;
        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.PHOTO_ADD_FAILE);
        }
    }

    /*
        UUID를 사용하여 fileName 재정의 후 사진을 저장합니다.
        사진을 저장한 후 저장된 파일경로와 파일이름이 담긴 PhotoInfo를 반환합니다.

        originalFileName : 기존 파일명
        fileBytes        : 저장할 사진의 바이트데이터
    */
    public String addPhoto(String originalFileName, byte[] fileBytes) {

        // UUID를 통한 파일명 생성
        String fileName = FileNameGenerator.generateName(originalFileName);

        // 파일이 저장될 경로 생성
        String filePath = folderPath + fileName;

        try {
            Files.write(Paths.get(filePath), fileBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ExceptionCode.PHOTO_ADD_FAILE);
        }

        return fileName;
    }
}
