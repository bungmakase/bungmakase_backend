package swyp_8th.bungmakase_backend.globals.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import swyp_8th.bungmakase_backend.globals.code.FailureCode;
import swyp_8th.bungmakase_backend.globals.code.SuccessCode;

@Getter
@Setter
@AllArgsConstructor
public class ResponseTemplate<T> {
    private String status;  // "success" 또는 "fail"
    private int code;       // HTTP 상태 코드 (200, 400, 500 등)
    private String message; // 응답 메시지
    private T data;         // 응답 데이터

    // 성공 코드 응답 처리
    public ResponseTemplate(SuccessCode successCode, T data) {
        this.status = "success";
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
        this.data = data;
    }

    // 실패 코드 응답 처리
    public ResponseTemplate(FailureCode failureCode, T data) {
        this.status = "fail";
        this.code = failureCode.getCode();
        this.message = failureCode.getMessage();
        this.data = data;
    }
}
