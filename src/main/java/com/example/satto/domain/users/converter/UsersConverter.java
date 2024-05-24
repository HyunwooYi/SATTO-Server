package com.example.satto.domain.users.converter;

import com.example.satto.domain.users.dto.UsersResponseDTO;
import com.example.satto.domain.users.entity.Users;
import org.springframework.security.core.userdetails.User;

public class UsersConverter {

    public static UsersResponseDTO.UserPreviewDTO toUserPreviewDTO(Users user) {
        return UsersResponseDTO.UserPreviewDTO.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .profileImg(user.getProfileImg())
                .updateAt(user.getUpdatedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
