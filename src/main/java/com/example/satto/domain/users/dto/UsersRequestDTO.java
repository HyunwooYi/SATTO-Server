package com.example.satto.domain.users.dto;

import lombok.Getter;
import org.springframework.aop.ClassFilter;

public class UsersRequestDTO {

    @Getter
    public static class UpdateUserDTO {
        private String name;
        private String nickname;
        private String profileImg;
        private String department;
        private int grade;
    }

    @Getter
    public static class UpdateUserPasswordDTO {
        private String password;
    }

}
