package com.example.satto.domain.follow.controller;

import com.example.satto.domain.follow.service.FollowService;
import com.example.satto.domain.users.converter.UsersConverter;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.service.UsersService;
import com.example.satto.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FollowController {

    private final FollowService followService;
    private final UsersService usersService;

    // 비공개 프로필 팔로우 요청
    @Operation(summary = "팔로우 요청", description = "상대방 userId를 입력하여 팔로우 요청을 보낸다.")
    @PostMapping("/request/{followingId}")
    public BaseResponse<Object> followRequest(@PathVariable("followingId") String followingId, @AuthenticationPrincipal Users user) {
        String studentId = user.getStudentId();
        return followService.followRequest(followingId, studentId);
    }



    // 팔로우 요청 수락 대기 목록
    @Operation(summary = "팔로우 요청 수락 대기 목록")
    @GetMapping("/request/list")
    public Map<String, String> followRequestList(@AuthenticationPrincipal Users user) {
        String studentId = user.getStudentId();
        return followService.followRequestList(studentId);
    }

    // 팔로우 수락
    @Operation(summary = "팔로우 수락",
            description = "상대방 userId를 입력하여 팔로우 수락")
    @PostMapping("/accept/{followerId}")
    public BaseResponse<Object> acceptFollower(@PathVariable("followerId") String followerId, @AuthenticationPrincipal Users user) {
        String studentId = user.getStudentId();
        return followService.acceptFollower(followerId, studentId);
    }

    // 언팔로우
    @Operation(summary = "언팔로우",
            description = "상대방 userId를 입력하여 언팔로우 한다..")
    @DeleteMapping("/unfollow/{followerId}")
    public BaseResponse<Object> unFollow(@PathVariable("followerId") String followerId, @AuthenticationPrincipal Users user) {
        String studentId = user.getStudentId();
        return followService.unFollower(followerId, studentId);

    }

    // 언팔로잉
    @Operation(summary = "언팔로잉",
            description = "상대방 userId를 입력하여 언팔로잉 한다.")
    @DeleteMapping("/unfollowing/{followingId}")
    public BaseResponse<Object> unFollowing(@PathVariable("followingId") String followingId, @AuthenticationPrincipal Users user) {
        String studentId = user.getStudentId();
        return followService.unFollowing(followingId, studentId);

    }

    // 상대방 프로필 페이지 방문
    @Operation(summary = "상대방 프로필 페이지", description = "PathVariable 에 상대방 학번 입력한다. / 상대방 유저의 이름, 학번, 팔로워 수, 팔로잉 수 를 볼수 있다.")
    @GetMapping("/view/{studentId}")
    public BaseResponse<?> userInformation(@PathVariable("studentId") String studentId) {
//        Long userId = user.getUserId();
//        String userStudentId = user.getStudentId();
        Users users = usersService.userProfile(studentId);
        List follower = (List) usersService.followerListNum(studentId); // 팔로우 목록
        List following = (List) usersService.followingListNum(studentId); // 팔로잉 목록

        return BaseResponse.onSuccess(UsersConverter.toUserProfileDTO(users, follower.size(), following.size()));
    }


    // 팔로우 목록 조회
    @Operation(summary = "팔로우 목록 조회",
            description = "팔로우 목록을 조회한다.")
    @GetMapping("/{studentId}/followerList")
    public BaseResponse<Object> viewFollowerList(@PathVariable("studentId") String studentId) {
        return BaseResponse.onSuccess(usersService.viewFollowerList(studentId));
    }

    // 팔로잉 목록 조회
    @Operation(summary = "팔로잉 목록 조회",
            description = "팔로잉 목록을 조회한다.")
    @GetMapping("{studentId}/followingList")
    public BaseResponse<Object> viewFollowingList(@PathVariable("studentId") String studentId)  {
        return BaseResponse.onSuccess(usersService.viewFollowingList(studentId));
    }

}
