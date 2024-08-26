package me.snaptime.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Friend Exception
    CAN_NOT_SELF_FOLLOW(HttpStatus.BAD_REQUEST, "자신에게 친구추가 요청을 보낼 수 없습니다."),
    ALREADY_FOLLOW(HttpStatus.BAD_REQUEST,"이미 팔로우관계입니다."),
    FRIEND_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 친구입니다."),
    PAGE_NOT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않는 페이지입니다."),
    FRIEND_REQ_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 친구요청입니다."),

    // Reply Exception
    REPLY_NOT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않는 댓글입니다."),
    ACCESS_FAIL_REPLY(HttpStatus.FORBIDDEN,"댓글에 대한 권한이 없습니다."),

    // Alarm Exception
    ALARM_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 알림입니다."),
    ACCESS_FAIL_ALARM(HttpStatus.FORBIDDEN, "알림에 대한 권한이 없습니다."),

    // SignIn Exception
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_AUTH_FAIL(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),

    // ProfilePhotoException
    PROFILE_PHOTO_NOT_EXIST(HttpStatus.NOT_FOUND,"존재하지 않는 프로필 사진입니다."),

    // Cipher Exception
    ENCRYPT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "암호화에 실패하였습니다. 관리자에게 문의해주세요."),
    ADD_SECRET_KEY_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "개인키생성에 실패했습니다. 관리자에게 문의해주세요."),
    DECRYPT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "복호화에 실패하였습니다. 관리자에게 문의해주세요."),

    // User Exception
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),
    CAN_NOT_UPDATE_SAME_PASSWORD(HttpStatus.BAD_REQUEST,"같은 비밀번호로 수정 할 수 없습니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 loginId 입니다."),

    // Snap Exception
    SNAP_NOT_EXIST(HttpStatus.BAD_REQUEST, "스냅이 존재하지 않습니다."),
    ACCESS_FAIL_SNAP(HttpStatus.FORBIDDEN, "해당 스냅에 대한 권한이 없습니다."),
    ALREADY_SNAP_VISIBILITY(HttpStatus.BAD_REQUEST, "이미 설정되어있는 스냅상태입니다."),

    // File Exception
    FILE_ADD_FAILE(HttpStatus.BAD_REQUEST, "파일저장에 실패했습니다."),
    FILE_DELETE_FAIL(HttpStatus.BAD_REQUEST, "파일삭제에 실패했습니다."),
    FILE_FIND_FAIL(HttpStatus.BAD_REQUEST, "파일조회에 실패했습니다."),

    // Album Exception
    ALBUM_NOT_EXIST(HttpStatus.BAD_REQUEST, "앨범이 존재하지 않습니다."),
        // 기본앨범은 삭제가 불가능하고 회원가입 시 자동생성되기때문에 500을 반환한다.
    BASIC_ALBUM_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "기본앨범이 존재하지 않습니다. 관리자에게 문의하세요."),
    ACCESS_FAIL_ALBUM(HttpStatus.FORBIDDEN, "앨범에 대한 권한이 없습니다."),
    CAN_NOT_BE_MODIFIED_OR_DELETED_BASIC_ALBUM(HttpStatus.BAD_REQUEST, "기본앨범은 수정,삭제할 수 없습니다."),

    // Jwt Exception
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,  "AccessToken 이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,  "RefreshToken 이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "토큰이 비었거나 null입니다"),
    SNAPTAG_NOT_EXIST(HttpStatus.BAD_REQUEST,"존재하지 않는 태그유저입니다."),

    // Jsoup Action
    URL_HAVING_PROBLEM(HttpStatus.BAD_REQUEST, "문제가 있는 URL입니다.");

    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
