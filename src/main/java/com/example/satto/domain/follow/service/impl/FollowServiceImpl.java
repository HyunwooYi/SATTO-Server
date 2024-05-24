package com.example.satto.domain.follow.service.impl;

import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.follow.repository.FollowRepository;
import com.example.satto.domain.follow.service.FollowService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    @Override
    public BaseResponse<String> followRequest(Long followingId, Long userId) {
        if (followRepository.existsByFollowerIdUserIdAndFollowingIdUserId(userId, followingId) &&
                followRepository.existsByFollowerIdUserIdAndFollowingIdUserIdAndRequest(userId, followingId, 1)) {

            return BaseResponse.onSuccess("이미 팔로우");
        } else {
            Users followerUser = new Users();
            followerUser.setUserId(userId);

            Users followingUser = new Users();
            followingUser.setUserId(followingId);

            Follow follow = new Follow();
            follow.setFollowerId(followerUser);
            follow.setFollowingId(followingUser);
            follow.setRequest(1);
            followRepository.save(follow);
        }
        return null;
    }

    @Transactional
    @Override
    public BaseResponse<?> acceptFollower(Long followerId, Long userId) {

        Follow followerRequest = followRepository.findByFollowerIdUserIdAndFollowingIdUserIdAndRequest(followerId, userId, 1);
        if (followerRequest != null){
            followerRequest.setRequest(2);
                followRepository.save(followerRequest);
        } else {
            return BaseResponse.onFailure("200", "요청 내역이 없습니다.", "");
        }
        return null;
    }

    @Override
    public void unFollower(Long followerId, Long userId) {

        Follow follow = followRepository.findByFollowerIdUserIdAndFollowingIdUserId(followerId, userId);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            System.out.println("유저가 없습니다.");
        }
    }

    @Override
    public void unFollowing(Long followingId, Long userId) {
        Follow follow = followRepository.findByFollowerIdUserIdAndFollowingIdUserId(userId, followingId);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            System.out.println("유저가 없습니다");
        }
    }


}
