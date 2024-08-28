package com.example.satto.domain.users.converter;

import com.example.satto.domain.users.dto.UsersResponseDTO;
import com.example.satto.domain.users.entity.Users;

import java.util.HashMap;
import java.util.Map;

public class UsersConverter {

    public static UsersResponseDTO.UserPreviewDTO toUserPreviewDTO(Users user) {
        return UsersResponseDTO.UserPreviewDTO.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .grade(user.getGrade())
//                .profileImg(user.getProfileImg())
                .updateAt(user.getUpdatedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UsersResponseDTO.ExistUserDTO toUserShowDTO(Users user) {
        return UsersResponseDTO.ExistUserDTO.builder()
                .profileImg(user.getProfileImg())
                .name(user.getName())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .build();

    }

    public static UsersResponseDTO.UserProfileDTO toUserProfileDTO(Users user, int followerNum, int followingNum) {
        return UsersResponseDTO.UserProfileDTO.builder()
                .profileImg(user.getProfileImg())
                .name(user.getName())
                .studentId(user.getStudentId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .followingNum(followingNum)
                .followerNum(followerNum)
                .build();
    }

    public static UsersResponseDTO.UserInformation2 toUserInformation2(Users user) {
        return UsersResponseDTO.UserInformation2.builder()
                .profileImg(user.getProfileImg())
                .studentId(user.getStudentId())
                .name(user.getName())
                .nickname(user.getNickname())
                .department(user.getDepartment())
                .password(user.getPassword())
                .email(user.getEmail())
                .grade(user.getGrade())
                .isPublic(user.isPublic())
                .build();
    }

//    public static Map<String, String> convertToMap(Users user) {
//        Map<String, String> userMap = new HashMap<>();
//        userMap.put("studentId", user.getStudentId());
//        userMap.put("name", user.getName());
//        userMap.put("nickname", user.getNickname());
//        userMap.put("email", user.getEmail());
//        userMap.put("department", user.getDepartment());
//        userMap.put("grade", String.valueOf(user.getGrade()));
//        userMap.put("isPublic", String.valueOf(user.isPublic()));
//        return userMap;
//    }

}
