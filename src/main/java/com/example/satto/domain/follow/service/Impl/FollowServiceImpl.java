package com.example.satto.domain.follow.service.Impl;

import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.follow.repository.FollowRepository;
import com.example.satto.domain.follow.service.FollowService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.UsersRepository;
import com.example.satto.global.common.BaseResponse;
import com.example.satto.global.common.code.status.ErrorStatus;
import com.example.satto.global.common.exception.handler.UsersHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UsersRepository usersRepository;

    @Override
    public BaseResponse<Object> followRequest(String followingId, String studentId) {
        if (isFollowRequestAlreadySent(studentId, followingId)) {
            return BaseResponse.onSuccess("이미 Follow 요청을 보냈습니다.");
        }
        // 팔로워 조회
        Users followerUser = findUserByStudentId(studentId);
        // 팔로잉 조회
        Users followingUser = findUserByStudentId(followingId);
        Follow follow = createFollow(followerUser, followingUser);
        followRepository.save(follow);

        return BaseResponse.onSuccess("Follow 요청을 보냈습니다.");
    }

    @Override
    public Map<String, String> followRequestList(String studentId) {
        // 팔로우 요청을 보낸 사람들의 목록을 가져온다.
        List<Follow> followerRequests = followRepository.findByFollowingIdStudentIdAndRequest(studentId, 1);
        Map<String, String> followRequesters = new HashMap<>();

        for (Follow follow : followerRequests) {
            Users user = follow.getFollowerId();
            followRequesters.put(user.getStudentId(), user.getName());
        }

        return followRequesters;
    }

    @Override
    public BaseResponse<String> findFollowRequestToChangePublic(String studentId) {
        List<Follow> followerRequests = followRepository.findByFollowingIdStudentIdAndRequest(studentId, 1);
        if (followerRequests.isEmpty()) {
            return BaseResponse.onSuccess("Follow 요청 목록이 비어있습니다.");
        }
        else {
            for (Follow user: followerRequests) {
                user.setRequest(2);
                followRepository.save(user);
            }
            return BaseResponse.onSuccess("Follow 요청 모록 유저들을 수락했습니다.");
        }
    }

    @Transactional
    @Override
    public BaseResponse<Object> acceptFollower(String followerId, String studentId) {
        Follow followRequest = findFollowRequest(followerId, studentId);

        if (followRequest != null) {
            followRequest.setRequest(2);
            followRepository.save(followRequest);
            return BaseResponse.onSuccess("Follow 요청을 수락하였습니다.");
        } else {
            return BaseResponse.onFailure("Follow 요청 내역이 없습니다.");
        }
    }

    @Override
    public BaseResponse<Object> unFollower(String followerId, String studentId) {
        Follow follow = findFollow(followerId, studentId);

        if (follow != null) {
            followRepository.delete(follow);
            return BaseResponse.onSuccess("해당 Follower를 삭제하였습니다.");
        } else {
            return BaseResponse.onFailure("해당 Follower가 존재하지 않습니다.");
        }
    }

    @Override
    public BaseResponse<Object> unFollowing(String followingId, String studentId) {
        Follow follow = findFollow(studentId, followingId);

        if (follow != null) {
            followRepository.delete(follow);
            return BaseResponse.onSuccess("해당 Following을 삭제하였습니다.");
        } else {
            return BaseResponse.onFailure("해당 Following이 존재하지 않습니다.");
        }
    }

    private boolean isFollowRequestAlreadySent(String studentId, String followingId) {
        return followRepository.existsByFollowerIdStudentIdAndFollowingIdStudentId(studentId, followingId) &&
                followRepository.existsByFollowerIdStudentIdAndFollowingIdStudentIdAndRequest(studentId, followingId, 1);
    }

    private Users findUserByStudentId(String studentId) {
        return usersRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsersHandler(ErrorStatus._NOT_FOUND_USER));
    }

    private Follow createFollow(Users followerUser, Users followingUser) {
        boolean userPresent = followingUser.isPublic();
        Follow follow = new Follow();
        follow.setFollowerId(followerUser);
        follow.setFollowingId(followingUser);

        if (!userPresent) {     // 팔로잉 대상인 유저가 비공개 계정일 때
            follow.setRequest(1);
        } else {                // 팔로잉 대상인 유저가 공개 계정일 때
            follow.setRequest(2);
        }
        return follow;
    }

    private Follow findFollowRequest(String followerId, String studentId) {
        return followRepository.findByFollowerIdStudentIdAndFollowingIdStudentIdAndRequest(followerId, studentId, 1);
    }

    private Follow findFollow(String followerId, String followingId) {
        return followRepository.findByFollowerIdStudentIdAndFollowingIdStudentId(followerId, followingId);
    }
}