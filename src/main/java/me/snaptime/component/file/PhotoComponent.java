package me.snaptime.component.file;

import me.snaptime.snap.dto.file.PhotoInfo;

public interface PhotoComponent {

    /*
        사진을 가져옵니다.
    */
    byte[] findPhoto(String fileName);

    /*
        사진을 삭제합니다.
    */
    void deletePhoto(String fileName);

    /*
        사진을 수정합니다.
    */
    void updatePhoto(String fileName, byte[] fileBytes);

    /*
        UUID를 사용하여 fileName 재정의 후 사진을 저장합니다.
    */
    PhotoInfo addPhoto(String originalFileName, byte[] fileBytes);
}
