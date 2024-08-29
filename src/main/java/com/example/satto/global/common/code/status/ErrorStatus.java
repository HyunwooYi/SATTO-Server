package com.example.satto.global.common.code.status;

import com.example.satto.global.common.code.BaseErrorCode;
import com.example.satto.global.common.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Course 에러
    _NOT_FOUND_PREVIOUS_LECTURE(HttpStatus.NOT_FOUND, "COURSE400", "요청한 정보에 해당하는 강의가 존재하지 않습니다."),
    _NOT_FOUND_PREVIOUS_LECTURE_BY_SEMESTER_YEAR(HttpStatus.NOT_FOUND, "COURSE400", "요청한 년도학기에 해당하는 강의가 존재하지 않습니다."),
    _NOT_FOUND_COURSE(HttpStatus.NOT_FOUND, "COURSE400", "강의가 사용자 강의 목록에 존재하지 않습니다."),

    // User 에러
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER400", "사용자가 존재하지 않습니다."),
    _INVALID_USER(HttpStatus.BAD_REQUEST, "USER401" , "아이디 또는 비밀번호가 틀렸습니다."),
    _INVALID_PW(HttpStatus.BAD_REQUEST, "USER402", "입력한 비밀번호가 일치하지 않습니다."),

    // Follow 에러
    _NOT_FOUND_FOLLOWING_LIST(HttpStatus.NOT_FOUND, "FOLLOW400", "사용자가 존재하지 않습니다."),


    // Event 에러,
    _NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "EVENT400", "해당 이벤트가 존재하지 않습니다."),
    _NOT_FOUND_PHOTO(HttpStatus.NOT_FOUND, "EVENT401", "해당 사진을 찾을 수 없습니다."),
    _DUPLICATE_CONTEST_ENTRY(HttpStatus.BAD_REQUEST, "EVENT402", "이미 참여한 이벤트입니다."),

    // Security 에러,
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SEC4001", "잘못된 형식의 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "SEC4010", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "SEC4011", "토큰이 만료되었습니다."),
    TOKEN_SIGNATURE_ERROR(HttpStatus.UNAUTHORIZED, "SEC4012", "토큰이 위조되었거나 손상되었습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "SEC4030", "권한이 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "SEC4041", "토큰이 존재하지 않습니다."),
    TOKEN_ORGANIZATION_NOT_FOND(HttpStatus.UNAUTHORIZED, "SEC4042", "존재하지 않는 단체입니다."),
    INTERNAL_SECURITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5000", "인증 처리 중 서버 에러가 발생했습니다."),
    INTERNAL_TOKEN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEC5001", "토큰 처리 중 서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}