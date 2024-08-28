package com.example.satto.domain.users.service;

import com.example.satto.domain.mail.dto.EmailRequestDTO;
import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UsersService {

    UserDetailsService userDetailsService();

    Object viewFollowerList(String studentId);

    Object viewFollowingList(String studentId);

    Object followerListNum(String studentId);

    Object followingListNum(String studentId);


    Users userProfile(Long user);

    void privateAccount(Long userId);

    void publicAccount(Long userId);

    Users updateAccount(UsersRequestDTO.UpdateUserDTO updateUserDTO, Long userId);

    boolean emailDuplicate(String email);

    Users uploadProfileImg(String url, String email);

    boolean nicknameDuplicate(String nickname);

    void withdrawal(Users user);

    Users beforeUpdateInformation(Long userId);

    boolean studentIdDuplicate(EmailRequestDTO.EmailCheckRequest emailCheckRequest);

    Users userProfile(String studentId);

    void resetPassword(UsersRequestDTO.UpdateUserPasswordDTO updateUserPasswordDTO, Long userId);


    List<Map<String, String>> searchUserByStudentId(String query);

    List<Map<String, String>> searchUserByName(String query);


    Long findId(String email);

    void saveProfile(MultipartFile multipartFile, String email);

    void deleteProfileImage(String studentId);

}
