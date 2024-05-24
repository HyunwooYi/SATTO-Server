package com.example.satto.domain.follow.controller;

import com.example.satto.domain.follow.service.FollowService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.service.UsersService;
import com.example.satto.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UsersService usersService;

    // 팔로우 요청
    @PostMapping("/request/{follwingId}")
    public BaseResponse<Object> followRequest(@PathVariable("follwingId") Long followingId, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        followService.followRequest(followingId, userId);
        return BaseResponse.onSuccess("팔로우 요청 성공");
    }

    // 팔로우 수락
    @PostMapping("/accept/{followerId}")
    public BaseResponse<Object> acceptFollower(@PathVariable("followerId") Long followerId, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        followService.acceptFollower(followerId, userId);
        return BaseResponse.onSuccess("팔로우 수락");
    }

    // 언팔로우
    @DeleteMapping("/unfollow/{followerId}")
    public BaseResponse<Object> unFollow(@PathVariable("followerId") Long followerId, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        followService.unFollower(followerId, userId);
        return BaseResponse.onSuccess("언팔로우");
    }

    // 언팔로잉
    @DeleteMapping("/unfollowing/{followingId}")
    public BaseResponse<Object> unFollowing(@PathVariable("followingId") Long followingId, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        followService.unFollowing(followingId, userId);
        return BaseResponse.onSuccess("언팔로잉");
    }


    // 팔로우 목록 조회
    @GetMapping("/followerList")
    public BaseResponse<Object> viewFollowerList(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        return BaseResponse.onSuccess(usersService.viewFollowerList(userId));
    }


    // 팔로잉 목록 조회
    @GetMapping("/followingList")
    public BaseResponse<Object> viewFollowingList(@AuthenticationPrincipal Users user)  {
        Long userId = user.getUserId();
        return BaseResponse.onSuccess(usersService.viewFollowingList(userId));
    }

}
