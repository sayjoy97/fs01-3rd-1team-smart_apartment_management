package com.jjld.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    // 요청 처리 성공 여부 (true: 성공, false: 실패)
    private boolean success;

    // 성공 시 실제 응답 데이터
    // 실패 시에는 null
    private T data;

    // 실패 시 에러 정보
    // 성공 시에는 null
    private ApiError error;

    // 성공 + 데이터 반환
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 성공 + 데이터 없이 반환
    // (등록, 삭제 등 <- 이런 경우에도 String으로 데이터를 보내고 싶으면 위에 거 사용)
    public static ApiResponse<?> success() {
        return new ApiResponse<>(true, null, null);
    }

    // 실패 응답 반환
    public static ApiResponse<?> error(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }
}
