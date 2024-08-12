package com.example.satto.domain.event.controller;

import com.example.satto.domain.event.dto.TimetableContestResponseDto;
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
@RequestMapping("/api/v1/event/timetable-contest")
@Tag(name = "Event Domain Api", description = "시간표 경진 대회 관련 Api입니다.")
public class TimetableContestController {

    private final EventService eventService;

    @Operation(method = "GET", summary = "시간표 경진 대회: 시간표 사진 목록 조회", description = "시간표 경진 대회 참여 사진 목록 조회")
    @GetMapping("")
    public BaseResponse<List<TimetableContestResponseDto>> getTimetableContestParticipants(
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.getTimetableContestParticipants());
    }

    @Operation(method = "POST", summary = "시간표 경진 대회: 좋아요 누르기", description = "좋아요 상태면 좋아요 취소, 좋아요 상태가 아니면 좋아요 부여")
    @PostMapping("/{timetableContestId}/like")
    public BaseResponse<String> changeLikeStatus(
            @PathVariable("timetableContestId") Long timetableContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.likeTimetableContest(timetableContestId, user));
    }

    @Operation(method = "POST", summary = "시간표 경진 대회: 싫어요 누르기", description = "싫어요 상태면 싫어요 취소, 싫어요 상태가 아니면 싫어요 부여")
    @PostMapping("/{timetableContestId}/dislike")
    public BaseResponse<String> changeDislikeStatus(
            @PathVariable("timetableContestId") Long timetableContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.dislikeTimetableContest(timetableContestId, user));
    }

    @Operation(method = "DELETE", summary = "시간표 경진 대회: 참여 취소", description = "시간표 경진 대회 업로드한 사진 삭제")
    @DeleteMapping("/{timetableContestId}")
    public BaseResponse<String> deletePhotoContest(
            @PathVariable("timetableContestId") Long timetableContestId,
            @AuthenticationPrincipal Users user
    ) {
        return BaseResponse.onSuccess(eventService.deleteTimetableContest(timetableContestId, user));
    }
    @Operation(method = "POST", summary = "시간표 경진 대회: 사진 업로드", description = "시간표 경진 대회 참여")
    @PostMapping(consumes = "multipart/form-data")
    public BaseResponse<TimetableContestResponseDto.SavedTimetableContest> joinTimetableContest(
            @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal Users user
    ) throws IOException {
        return BaseResponse.onSuccess(eventService.joinTimetableContest(multipartFile, user));
    }
}
