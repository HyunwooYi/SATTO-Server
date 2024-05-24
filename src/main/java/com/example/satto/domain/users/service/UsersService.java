package com.example.satto.domain.users.service;

import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService {

    UserDetailsService userDetailsService();

    Object viewFollowerList(Long userId);

    Object viewFollowingList(Long userId);

    Users userInformation(Long user);

    void privateAccount(Long userId);

    void publicAccount(Long userId);

    Users updateAccount(UsersRequestDTO.UpdateUserDTO updateUserDTO, Long userId);

    boolean emailDuplicate(String email);

    Users uploadProfileImg(String url, String email);
}
