package com.example.satto.domain.users.service.Impl;

import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.follow.repository.FollowRepository;
import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.UsersRepository;
import com.example.satto.domain.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final FollowRepository followRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) {
                return usersRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };

    }

    @Override
    public Object viewFollowerList(Long userId) {
        List<String> followerList = new ArrayList<>();
        Optional<Users> optionalUser =usersRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            for (Follow followerId: user.getFollowingList()) {
                if ((followerId.getRequest() == 2) && (!followerId.getFollowingId().equals(user.getUserId()) )) {
                    followerList.add(followerId.getFollowerId().getEmail());
                }
            }


        }
        return followerList;

    }


    @Override
    public Object viewFollowingList(Long userId) {
        List<String> followingList = new ArrayList<>();
        Optional<Users> optionalUser =usersRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            for (Follow followingId: user.getFollowerList()) {
                if ((followingId.getRequest() == 2) && (!followingId.getFollowerId().equals(user.getUserId()))) {
                    followingList.add(followingId.getFollowingId().getEmail());
                }
            }
            return followingList;
        } else {
            System.out.println("사용자가 존재 x");
        }
        return null;
    }

    @Override
    public Users userInformation(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        return user;
    }

    @Override
    public void privateAccount(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setIsPublic((byte) 0);
        usersRepository.save(user);
    }

    @Override
    public void publicAccount(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setIsPublic((byte) 1);
        usersRepository.save(user);
    }

    @Override
    public Users updateAccount(UsersRequestDTO.UpdateUserDTO updateUserDTO, Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        user.setName(updateUserDTO.getName());
        user.setNickname(updateUserDTO.getNickname());
        user.setProfileImg(updateUserDTO.getProfileImg());
        user.setDepartment(updateUserDTO.getDepartment());
        usersRepository.save(user);
        return user;
    }

    @Override
    public boolean emailDuplicate(String email) {
        return (usersRepository.existsByEmail(email));

    }

    @Override
    public Users uploadProfileImg(String url, String email) {
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setProfileImg(url);

            return usersRepository.save(user);
        } else {
            return null;
        }
    }

}
