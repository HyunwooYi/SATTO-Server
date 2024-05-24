package com.example.satto.mail.dto;

import lombok.Getter;

public class EmailRequestDTO {

    @Getter
    public static class EmailAuthRequest {
        private String certificationNum;
    }

    @Getter
    public static class EmailCheckRequest {
        private String email;
    }
}
