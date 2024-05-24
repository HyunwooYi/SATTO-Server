package com.example.satto.domain.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UsersResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserPreviewDTO {
        private String name;
        private String nickname;
        private String department;
        private String profileImg;
        private LocalDateTime createdAt;
        private LocalDateTime updateAt;
    }

}
