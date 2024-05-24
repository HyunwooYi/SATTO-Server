package com.example.satto.domain.follow.service;

import com.example.satto.global.common.BaseResponse;

public interface FollowService {


    BaseResponse<String> followRequest(Long followingId, Long userId);

    BaseResponse<?> acceptFollower(Long followerId, Long userId);


    void unFollower(Long followerId, Long userId);

    void unFollowing(Long followingId, Long userId);
}
