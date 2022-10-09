package com.main.project.exception;

import lombok.Getter;

public enum ExceptionCode {
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다"),
    CANT_FIND_RESTAURANT(404, "음식과 지역이 정확한지 확인해주세요"),
    WRITER_IS_NOT_MATCH(404, "작성자가 아닙니다"),
    USER_IS_NOT_MATCH(   303, "입력한 사용자와 작성자가 다릅니다."),
    USER_IS_NOT_EXIST(   304, "해당 이메일의 유저가 존재하지 않습니다."),
    ALREADY_DEACTICATED_USER(305, "이미 비활성화된 회원입니다."),
    COMMENT_IS_NOT_EXIST(404, "해당 댓글을 찾을 수 없습니다"),
    RESTAURANT_NOT_FOUND(404, "해당 식당을 찾을 수 없습니다."),
    LIKE_IS_NOT_EXISTS(404, "해당 좋아요를 찾을 수 없습니다"),

    PASSWORD_NOT_MATCH(601, "비밀번호가 맞지 않습니다 "),
    FOODTYPE_NOT_EXIST(701, "해당 푸드타입이 존재하지 않습니다. "),
    FOODTYPE_DUPLICATE(702, "동일한 푸드타입이 존재합니다. "),
    Badge_ID_IS_NOT_CORRECT(701, "일치하는 뱃지가 없습니다"),
    ID_NOT_FOUND(402, "해당 코드 아이디를 찾을 수 없습니다"),

    FILE_IS_NOT_EXIST_IN_BUCKET(1001, "해당 파일명은 확인되지 않습니다."),

    FOOD_NOT_EXIST(901, "해당 푸드는 존재하지 않습니다. "),
    DUPLICATE_FOOD(902, "동일한 푸드가 존재합니다."),
    TOKEN_IS_EXPIRED(902, "토큰이 만료 되었습니다."),

    FILE_IS_NOT_EXIST(102, "해당 파일은 존재하지 않습니다 ."),
    LOCATION_NOT_FOUND(402, "지역이 존재하지 않습니다."),
    STATE_NOT_FOUND(402, "해당 광역시, 도가 존재하지 않습니다."),
    CITY_NOT_FOUND(402, "시, 군, 구가 존재하지 않습니다."),
    ALREADY_LIKE(404, "이미 좋아요를 눌렀습니다.")


    ;

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
