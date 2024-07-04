package com.example.satto.domain.users.service.Impl;

import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.follow.repository.FollowRepository;
import com.example.satto.domain.mail.dto.EmailRequestDTO;
import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.UsersRepository;
import com.example.satto.domain.users.service.UsersService;
import com.example.satto.global.common.code.status.ErrorStatus;
import com.example.satto.global.common.exception.handler.UsersHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsService userDetailsService() {
        return email -> usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    @Override
    public List<Map<String, String>> viewFollowerList(String studentId) {
        return getFollowList(studentId, true);
    }

    @Override
    public List<Map<String, String>> viewFollowingList(String studentId) {
        return getFollowList(studentId, false);
    }

    private List<Map<String, String>> getFollowList(String studentId, boolean isFollower) {
        Users user = usersRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
        List<Follow> followList = isFollower ? user.getFollowingList() : user.getFollowerList();
        // 내가 상대를 팔로잉 할때 DB에는 내가 follwer_id 이고 상대방이 following_id로 들어간다.
        // 그래서 팔로잉 리스트 조회시 user.getFollowerList(); 호출

        List<Map<String, String>> followMap = new ArrayList<>();
        for (Follow follow : followList) {
            Users relatedUser = isFollower ? follow.getFollowerId() : follow.getFollowingId();
            // isFollower가 true이면 나를 팔로우하는 사람들의 정보(followerId)
            // false이면 내가 팔로우하는 사람들의 정보(followingId)
            if (follow.getRequest() == 2 && !relatedUser.getStudentId().equals(studentId)) {
                followMap.add(convertFollowToMap(relatedUser));
            }
        }
        return followMap;
    }

    private Map<String, String> convertFollowToMap(Users user) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("studentId", user.getStudentId());
        userMap.put("name", user.getName());
        userMap.put("nickname", user.getNickname());
        userMap.put("email", user.getEmail());
        userMap.put("department", user.getDepartment());
        userMap.put("grade", String.valueOf(user.getGrade()));
        userMap.put("isPublic", String.valueOf(user.isPublic()));
        return userMap;    }

    @Override
    public List<String> followerListNum(String studentId) {
        return getFollowListEmails(studentId, true);
    }

    @Override
    public List<String> followingListNum(String studentId) {
        return getFollowListEmails(studentId, false);
    }

    private List<String> getFollowListEmails(String studentId, boolean isFollower) {
        Users user = usersRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
        List<Follow> followList = isFollower ? user.getFollowingList() : user.getFollowerList();

        List<String> emails = new ArrayList<>();
        for (Follow follow : followList) {
            Users relatedUser = isFollower ? follow.getFollowerId() : follow.getFollowingId();
            // isFollower가 true이면 나를 팔로우하는 사람들의 정보(followerId)
            // false이면 내가 팔로우하는 사람들의 정보(followingId)

            if (follow.getRequest() == 2 && !relatedUser.getStudentId().equals(studentId)) {
                emails.add(relatedUser.getEmail());
            }
        }
        return emails;
    }

    @Override
    public Users userProfile(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
    }

    @Override
    public void privateAccount(Long userId) {
        changeAccountVisibility(userId, false);
    }

    @Override
    public void publicAccount(Long userId) {
        changeAccountVisibility(userId, true);
    }

    private void changeAccountVisibility(Long userId, boolean isPublic) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
        user.setPublic(isPublic);
        usersRepository.save(user);
    }

    @Override
    public Users updateAccount(UsersRequestDTO.UpdateUserDTO updateUserDTO, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));

        user.setName(updateUserDTO.getName());
        user.setNickname(updateUserDTO.getNickname());
        user.setDepartment(updateUserDTO.getDepartment());
        user.setGrade(updateUserDTO.getGrade());

        usersRepository.save(user);
        return user;
    }

    @Override
    public boolean emailDuplicate(String email) {
        return usersRepository.existsByEmail(email);
    }

    @Override
    public Users uploadProfileImg(String url, String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
        user.setProfileImg(url);
        return usersRepository.save(user);
    }

    @Override
    public boolean nicknameDuplicate(String nickname) {
        return usersRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public void withdrawal(Users user) {
        followRepository.deleteByFollowingId(user);
        followRepository.deleteByFollowerId(user);
        usersRepository.deleteById(user.getUserId());
    }

    @Override
    public Users beforeUpdateInformation(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));

        Users information = new Users();
        information.setName(user.getName());
        information.setNickname(user.getNickname());
        information.setDepartment(user.getDepartment());
        information.setGrade(user.getGrade());

        return information;
    }

    @Override
    public boolean studentIdDuplicate(EmailRequestDTO.EmailCheckRequest emailCheckRequest) {
        return usersRepository.existsByStudentId(emailCheckRequest.getStudentId());
    }

    @Override
    public Users userProfile(String studentId) {
        return usersRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
    }

    @Override
    public void resetPassword(UsersRequestDTO.UpdateUserPasswordDTO updateUserPasswordDTO, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
        user.setPassword(passwordEncoder.encode(updateUserPasswordDTO.getPassword()));
        usersRepository.save(user);
    }
}