package swyp_8th.bungmakase_backend.globals.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FailureCode {

    // 실패 응답 코드
    BAD_REQUEST_400(HttpStatus.BAD_REQUEST, 400, "잘못된 요청"),
    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED, 401, "인증이 필요합니다."),
    FORBIDDEN_403(HttpStatus.FORBIDDEN, 403, "접근이 거부되었습니다."),
    NOT_FOUND_404(HttpStatus.NOT_FOUND, 404, "리소스를 찾을 수 없습니다."),
    SERVER_ERROR_500(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류 발생"),
    USED_EMAIL_409(HttpStatus.CONFLICT, 409, "이미 사용 중인 이메일입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    FailureCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
