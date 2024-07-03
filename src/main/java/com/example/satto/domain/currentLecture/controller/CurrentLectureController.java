package com.example.satto.domain.currentLecture.controller;

import com.example.satto.domain.currentLecture.dto.CurrentLectureRequestDto;
import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import com.example.satto.domain.currentLecture.service.CurrentLectureService;
import com.example.satto.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/current-lecture")
@CrossOrigin("*")
@Tag(name = "Current Lecture Domain Api", description = "현재 강의 조회 관련 Api입니다.")
public class CurrentLectureController {

    private final CurrentLectureService currentLectureService;

    @Operation(method = "POST", summary = "현재 강의 목록 조회",
            description = "시간표 자동 생성 과정에서 현재 강의 정보를 검색할때 사용됩니다. 요청한 현재 강의 정보 목록을 반환합니다.")
    @PostMapping("/search")
    public BaseResponse<List<CurrentLectureResponseDTO>> getCurrentLecture(
            @RequestBody CurrentLectureRequestDto currentLectureRequestDto
            ){
        List<CurrentLectureResponseDTO> result =  currentLectureService.getLectures(currentLectureRequestDto);
        return BaseResponse.onSuccess(result);
    }
}
