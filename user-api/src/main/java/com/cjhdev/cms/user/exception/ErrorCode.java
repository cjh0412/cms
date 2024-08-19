package com.cjhdev.cms.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    ALREADY_REGISTER_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다.")
    , WRONG_VERIFY(HttpStatus.BAD_REQUEST, "잘못된 인증 시도입니다.")
    , EXPIRE_CODE(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다.")
    , NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다.")
    // login
    , LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "아이디 또는 패스워드를 확인해 주세요")

    , ALREADY_VERIFY(HttpStatus.BAD_REQUEST, "이미 인증이 완료된 메일입니다.");



    private final HttpStatus httpStatus;
    private final String detail;
}
