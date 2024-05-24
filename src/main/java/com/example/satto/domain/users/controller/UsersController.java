package com.example.satto.domain.users.controller;

import com.example.satto.domain.users.converter.UsersConverter;
import com.example.satto.domain.users.dto.UsersRequestDTO;
import com.example.satto.domain.users.dto.UsersResponseDTO;
import com.example.satto.domain.users.entity.Users;
//import com.example.satto.domain.users.service.FileUploadService;
import com.example.satto.domain.users.service.UsersService;
import com.example.satto.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
//    private final FileUploadService fileUploadService;

    // 아이디 중복 확인
    @GetMapping("/id/{email}")
    public BaseResponse<?> emailDuplicate(@PathVariable("email") String email) {
        if (usersService.emailDuplicate(email)) {
            return BaseResponse.onSuccess("존재하는 아이디 입니다.");
        } else {
            return BaseResponse.onFailure("해당 아이디를 사용할 수 있습니다.");
        }
    }

    // 비밀번호 조건 확인
    @PostMapping("/id/password")
    public BaseResponse<?> passwordCheck(@RequestBody UsersRequestDTO.passwordDTO passwordDTO) {
        // 제약 검증
        if (!passwordDTO.getPassword().matches("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}")) {
            // 제약 조건을 만족 하지 않은 경우
            return BaseResponse.onFailure("8~16 자리의 영어(대/소문), 숫자, 특수문자를 포함해 주세요.");
        } else {
            return BaseResponse.onSuccess("해당 비밀번호를 사용할 수 있습니다.");
        }

    }

//    @PostMapping("/id/{email}/profile/image")
//    public BaseResponse<?> uploadProfileImg(@RequestParam("file") MultipartFile file, @PathVariable("email") String email) throws IOException {
//        String url = fileUploadService.uploadProfileImg(file);
//        usersService.uploadProfileImg(url, email);
//        return BaseResponse.onSuccess("프로필 사진 등록 완료");
//    }


    // 사용자 정보 조회
    @GetMapping("")
    public BaseResponse<?> userInformation(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        return BaseResponse.onSuccess(usersService.userInformation(userId));
    }

    // 개인정보 수정
    @PatchMapping("account/update")
    public BaseResponse<UsersResponseDTO.UserPreviewDTO> updateAccount(@RequestBody UsersRequestDTO.UpdateUserDTO updateUserDTO, @AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        Users users = usersService.updateAccount(updateUserDTO, userId);
        return BaseResponse.onSuccess(UsersConverter.toUserPreviewDTO(users));
    }

    // 계정 비공개 설정
    @PatchMapping("account/private")
    public BaseResponse<?> privateAccount(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        usersService.privateAccount(userId);
        return BaseResponse.onSuccess("비공개 설정 완료");
    }

    // 계정 공개 설정
    @PatchMapping("account/public")
    public BaseResponse<?> publicAccount(@AuthenticationPrincipal Users user) {
        Long userId = user.getUserId();
        usersService.publicAccount(userId);
        return BaseResponse.onSuccess("공개 설정 완료");
    }




}
