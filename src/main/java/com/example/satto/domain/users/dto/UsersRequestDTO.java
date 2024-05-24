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
    }

    @Getter
    public static class passwordDTO {
        private String password;
    }

}
