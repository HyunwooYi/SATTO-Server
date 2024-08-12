package com.example.satto.domain.event.controller;

import com.example.satto.domain.event.dto.PhotoContestResponseDto;
import com.example.satto.domain.event.service.EventService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event/photo-contest")
@Tag(name = "Event Domain Api", description = "사진 콘테스트 관련 Api입니다.")
public class PhotoContestController {

    private final EventService eventService;

    @Operation(method = "GET", summary = "사진 콘테스트: 사진 목록 조회", description = "사진 콘테스트 참여 사진 목록 조회")
    @GetMapping("")
    public BaseResponse<List<PhotoContestResponseDto>> getPhotoContestParticipants(
            @AuthenticationPrincipal Users user
            ) {
        return BaseResponse.onSuccess(eventService.getPhotoContestParticipants());
    }

    @Operation(method = "POST", summary = "사진 콘테스트: 좋아요 누르기", description = "좋아요 상태면 좋아요 취소, 좋아요 상태가 아니면 좋아요 부여")
    @PostMapping("/{photoContestId}/like")
    public BaseResponse<String> changeLikeStatus(
            @PathVariable("photoContestId") Long photoContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.likePhotoContest(photoContestId, user));
    }

    @Operation(method = "POST", summary = "사진 콘테스트: 싫어요 누르기", description = "싫어요 상태면 싫어요 취소, 싫어요 상태가 아니면 싫어요 부여")
    @PostMapping("/{photoContestId}/dislike")
    public BaseResponse<String> changeDislikeStatus(
            @PathVariable("photoContestId") Long photoContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.dislikePhotoContest(photoContestId, user));
    }

    @Operation(method = "DELETE", summary = "사진 콘테스트: 참여 취소", description = "사진 콘테스트 업로드한 사진 삭제")
    @DeleteMapping("/{photoContestId}")
    public BaseResponse<String> deletePhotoContest(
            @PathVariable("photoContestId") Long photoContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.deletePhotoContest(photoContestId, user));
    }

    @Operation(method = "POST", summary = "사진 콘테스트: 사진 업로드", description = "사진 콘테스트 참여")
    @PostMapping(consumes = "multipart/form-data")
    public BaseResponse<PhotoContestResponseDto.SavedPhotoContest> joinPhotoContest(
            @RequestParam("file")MultipartFile multipartFile,
            @AuthenticationPrincipal Users user
            ) throws IOException {
        return BaseResponse.onSuccess(eventService.joinPhotoContest(multipartFile, user));
    }
}
