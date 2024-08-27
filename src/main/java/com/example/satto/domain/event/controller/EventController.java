package com.example.satto.domain.event.controller;

import com.example.satto.domain.event.dto.ContestResponseDto;
import com.example.satto.domain.event.dto.EventCategoryResponseDto;
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
@RequestMapping("/api/v1/event")
@Tag(name = "Event Domain Api", description = "이벤트 관련 Api입니다.")
public class EventController {

    private final EventService eventService;

    @Operation(method = "GET", summary = "이벤트 목록 조회", description = "현재 진행중인 이벤트 목록 조회")
    @GetMapping("")
    public BaseResponse<List<EventCategoryResponseDto>> getEventList() {
        return BaseResponse.onSuccess(eventService.getEventCategoryInfoList());
    }

    @Operation(method = "GET", summary = "이벤트 사진 목록 조회", description = "해당 이벤트 참여 목록 조회, 원하는 이벤트 쿼리스트링으로 요청\ncategory = PhotoContest, TimetableContest")
    @GetMapping("/{category}")
    public BaseResponse<List<ContestResponseDto>> getContestParticipants(
            @AuthenticationPrincipal Users user,
            @PathVariable(name = "category") String category
    ) {
        return BaseResponse.onSuccess(eventService.getContestParticipants(category));
    }

    @Operation(method = "POST", summary = "이벤트 좋아요 누르기", description = "좋아요 상태면 좋아요 취소, 좋아요 상태가 아니면 좋아요 부여")
    @PostMapping("/{contestId}/like")
    public BaseResponse<String> changeLikeStatus(
            @PathVariable("contestId") Long contestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.likeContest(contestId, user));
    }

    @Operation(method = "POST", summary = "이벤트 싫어요 누르기", description = "싫어요 상태면 싫어요 취소, 싫어요 상태가 아니면 싫어요 부여")
    @PostMapping("/{contestId}/dislike")
    public BaseResponse<String> changeDislikeStatus(
            @PathVariable("contestId") Long contestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.dislikeContest(contestId, user));
    }

    @Operation(method = "DELETE", summary = "이벤트 참여 취소", description = "이벤트 업로드한 사진 삭제")
    @DeleteMapping("/{contestId}")
    public BaseResponse<String> deletePhotoContest(
            @PathVariable("contestId") Long contestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.deleteContest(contestId, user));
    }

    @Operation(method = "POST", summary = "이벤트 사진 업로드", description = "이벤트 참여")
    @PostMapping(consumes = "multipart/form-data")
    public BaseResponse<ContestResponseDto.SavedContest> joinContest(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam(name = "category") String category,
            @AuthenticationPrincipal Users user
    ) throws IOException {
        return BaseResponse.onSuccess(eventService.joinContest(category, multipartFile, user));
    }
}
