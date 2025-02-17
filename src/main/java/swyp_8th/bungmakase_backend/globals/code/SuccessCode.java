package swyp_8th.bungmakase_backend.globals.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    // 성공 응답 코드
    SUCCESS_200(HttpStatus.OK, 200, "요청 성공"),
    CREATED_201(HttpStatus.CREATED, 201, "리소스 생성 성공");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    SuccessCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
