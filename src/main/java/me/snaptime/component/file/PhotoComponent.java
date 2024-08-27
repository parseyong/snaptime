package me.snaptime.component.file;


public interface PhotoComponent {

    /*
        사진을 가져옵니다.
        사진의 byte[]를 반환합니다.

        fileName : 가져올 사진의 파일명
    */
    byte[] findPhoto(String fileName);

    /*
        사진을 삭제합니다.

        fileName : 삭제할 사진의 파일명
    */
    void deletePhoto(String fileName);

    /*
        사진을 수정합니다. 기존사진을 암호화하여 저장하거나 복호화하여 저장할 떄 호출됩니다.

        fileName  : 수정할 사진의 파일명
        fileBytes : 암호화(복호화)된 사진의 바이트 데이터
    */
    void updatePhotoVisibility(String fileName, byte[] fileBytes);

    /*
        UUID를 사용하여 fileName 재정의 후 사진을 저장합니다.
        사진을 저장한 후 저장된 파일경로와 파일이름이 담긴 PhotoInfo를 반환합니다.

        originalFileName : 기존 파일명
        fileBytes        : 저장할 사진의 바이트데이터
    */
    String addPhoto(String originalFileName, byte[] fileBytes);
}
