package com.jjld.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    // 클라이언트가 분기 처리에 사용할 에러 코드
    private String code;

    // 사용자 또는 로그에 표시할 에러 메시지
    private String message;
}
/*
   예시)
   code = "INVALID_REQUEST",
   message = "필수 값이 누락되었습니다."

   code = "DUPLICATE_USER",
   message = "이미 존재하는 사용자 ID입니다."
   ...
*/
