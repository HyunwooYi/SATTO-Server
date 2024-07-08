package com.example.satto.domain.users.controller;

//import com.example.satto.s31.FileService;
import com.example.satto.domain.users.converter.UsersConverter;
import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.dto.UsersResponseDTO;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UsersController {

    private final UsersService usersService;
//    private final FileService fileService;

    // email 중복 확인
    @Operation(summary = "email 중복 확인")
    @GetMapping("/id/{email}")
    public BaseResponse<?> emailDuplicate(@PathVariable("email") String email) {
        if (usersService.emailDuplicate(email)) {
            return BaseResponse.onSuccess("존재하는 아이디 입니다.");
        } else {
            return BaseResponse.onFailure("해당 아이디를 사용할 수 있습니다.");
        }
    }

    // nickname 중복 확인
    @Operation(summary = "nickname 중복 확인")
    @GetMapping("/id/nickname/{nickname}")
    public BaseResponse<?> nicknameDuplicate(@PathVariable("nickname") String nickname) {
        if (usersService.nicknameDuplicate(nickname)) {
            return BaseResponse.onSuccess("존재하는 닉네임 입니다.");
        } else {
            return BaseResponse.onFailure("해당 닉네임을 사용할 수 있습니다.");
        }
    }

//     프로필 이미지 등록
//    @PostMapping("/id/{email}/profile/image")
//    public BaseResponse<?> uploadProfileImg(@RequestParam("file") MultipartFile file, @PathVariable("email") String email) throws IOException {
//        String url = fileService.uploadFile(file, FileFolder.profile_Image);
//        usersService.uploadProfileImg(url, email);
//        return BaseResponse.onSuccess("프로필 사진 등록 완료");
//    }

//    // 이미지 삭제
//    @DeleteMapping("/id/{email}/profile/image")
//    public BaseResponse deleteProfileImg(@AuthenticationPrincipal Users user) {
//        String profileImg = user.getProfileImg();
//        fileService.deleteFile(profileImg);
//        return BaseResponse.onSuccess("삭제 완료");
//    }

    // 유저 프로필 페이지
    @Operation(summary = "유저 프로필 페이지", description = "유저의 이름, 학번, 팔로워 수, 팔로잉 수")
    @GetMapping("")
    public BaseResponse<?> userInformation(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        String userStudentId = user.getStudentId();
        Users users = usersService.userProfile(userId);
        List follower = (List) usersService.followerListNum(userStudentId); // 팔로우 목록
        List following = (List) usersService.followingListNum(userStudentId); // 팔로잉 목록

        return BaseResponse.onSuccess(UsersConverter.toUserProfileDTO(users, follower.size(), following.size()));
    }

    // 유저 정보 반환
    @Operation(summary = "유저 정보 반환", description = "유저의 이름, 닉네임, 학과, 비번, 이메일, 학년, 공개/비공개")
    @GetMapping("/inform")
    public BaseResponse<?> userInformation2(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        Users users = usersService.userProfile(userId);

        return BaseResponse.onSuccess(UsersConverter.toUserInformation2(users));
    }

    // 개인정보 수정 클릭하면 수정가능한 정보들이 보임
    @Operation(summary = "수정 가능한 개인정보 조회", description = "개인정보 수정으로 진입하면 수정가능한 정보들을 우선 보여준다.")
    @GetMapping("account/present")
    public BaseResponse<UsersResponseDTO.ExistUserDTO> updateInformation(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        Users information = usersService.beforeUpdateInformation(userId);
        return BaseResponse.onSuccess(UsersConverter.toUserShowDTO(information));
    }

    // 개인정보 수정
    @Operation(summary = "개인정보 수정 요청", description = "개인정보 수정 요청을 보낸다.")
    @PatchMapping("account/update")
    public BaseResponse<UsersResponseDTO.UserPreviewDTO> updateAccount(@RequestBody UsersRequestDTO.UpdateUserDTO updateUserDTO, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        Users users = usersService.updateAccount(updateUserDTO, userId);
        return BaseResponse.onSuccess(UsersConverter.toUserPreviewDTO(users));
    }

    // 계정 비공개 설정
    @Operation(summary = "계정 비공개 설정", description = "true는 공개, false는 비공개")
    @PatchMapping("account/private")
    public BaseResponse<?> privateAccount(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        usersService.privateAccount(userId);
        return BaseResponse.onSuccess("비공개 설정 완료");
    }

    // 계정 공개 설정
    @Operation(summary = "계정 공개 설정", description = "true는 공개, false는 비공개")
    @PatchMapping("account/public")
    public BaseResponse<?> publicAccount(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        usersService.publicAccount(userId);
        return BaseResponse.onSuccess("공개 설정 완료");
    }

    @Operation(summary = "비밀번호 재설정")
    @PatchMapping("account/pw")
    public BaseResponse<?> resetPassword(@RequestBody UsersRequestDTO.UpdateUserPasswordDTO updateUserPasswordDTO, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        usersService.resetPassword(updateUserPasswordDTO, userId);
        return BaseResponse.onSuccess("비밀번호 재설정 완료");
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("account/withdrawal")
    public BaseResponse<?> withdrawal(@AuthenticationPrincipal Users user) {
//        Long userId = user.getUserId();
        usersService.withdrawal(user);
        return BaseResponse.onSuccess("계정 탈퇴 완료");
    }

    @Operation(summary = "유저 검색창", description = "search?query=이현우 or /search?query=2018 과 같은 형식으로 검색")
    @GetMapping("/search")
    public BaseResponse<List<Map<String, String>>> searchUsers(@RequestParam String query) {
        // studentId와 name이 모두 제공되지 않았을 경우 예외 처리
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Required parameter 'query' is not present.");
        }

        List<Map<String, String>> users;

        // 입력 값이 숫자로 시작하면 studentId로 검색, 그렇지 않으면 name으로 검색
        if (query.matches("^\\d+.*")) {
            users = usersService.searchUserByStudentId(query);
        } else {
            users = usersService.searchUserByName(query);
        }

        return BaseResponse.onSuccess(users);
    }


}
