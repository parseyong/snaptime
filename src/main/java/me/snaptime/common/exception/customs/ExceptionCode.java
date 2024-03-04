package me.snaptime.common.exception.customs;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    // Social Exception
    SELF_FRIEND_REQ(HttpStatus.BAD_REQUEST, "자신에게 친구추가 요청을 보낼 수 없습니다."),
    WATING_FRIEND_REQ(HttpStatus.BAD_REQUEST,"이미 친구요청을 보냈습니다."),
    REJECT_FRIEND_REQ(HttpStatus.BAD_REQUEST,"팔로우요청이 거절되었습니다."),
    ALREADY_FOLLOW(HttpStatus.BAD_REQUEST,"이미 팔로우관계입니다."),
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 친구입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다."),
    ACCESS_FAIL_FRIENDSHIP(HttpStatus.FORBIDDEN,"해당 친구에 대한 권한이 없습니다."),

    // ProfilePhotoException
    PROFILE_PHOTO_EXIST(HttpStatus.BAD_REQUEST,"이미 프로필 사진이 존재합니다."),
    PROFILE_PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 프로필 사진입니다."),
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND,"해당하는 경로에 파일이 존재하지 않습니다."),

    // Encryption Exception
    ENCRYPTION_NOT_EXIST(HttpStatus.BAD_REQUEST, "암호키를 찾을 수 없습니다."),
    ENCRYPTION_ERROR(HttpStatus.BAD_REQUEST, "암호화키를 생성하던 도중 문제가 발생했습니다. 관리자에게 문의해주세요"),

    // User Exception
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),

    // Snap Exception
    SNAP_NOT_EXIST(HttpStatus.BAD_REQUEST, "스냅이 존재하지 않습니다."),

    // Photo Exception
    PHOTO_NOT_EXIST(HttpStatus.BAD_REQUEST, "사진을 찾을 수 없습니다."),

    // File Exception
    FILE_WRITE_ERROR(HttpStatus.BAD_REQUEST, "파일을 쓰던 중 문제가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.BAD_REQUEST, "파일을 시스템에서 삭제하던 중 문제가 발생했습니다."),
    FILE_READ_ERROR(HttpStatus.BAD_REQUEST, "파일을 시스템에서 읽어오던 중 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
