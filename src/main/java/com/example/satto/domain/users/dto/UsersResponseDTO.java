package com.example.satto.domain.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        private int grade;
        private String profileImg;
        private LocalDateTime createdAt;
        private LocalDateTime updateAt;
    }

    @Getter
    @Builder
    public static class ExistUserDTO {
        private String profileImg;
        private String name;
        private String nickname;
        private String department;
        private int grade;
    }

    @Getter
    @Builder
    public static class UserProfileDTO {
        private String profileImg;
        private String studentId;
        private String name;
        private String department;
        private String nickname;
        private String email;
        private int grade;
        private int followingNum;
        private int followerNum;
    }
    @Getter
    @Builder
    public static class UserInformation2 {
        private String profileImg;
        private String studentId;
        private String name;
        private String nickname;
        private String department;
        private String password;
        private String email;
        private int grade;

        @JsonProperty("isPublic")
        private boolean isPublic;
    }



}
