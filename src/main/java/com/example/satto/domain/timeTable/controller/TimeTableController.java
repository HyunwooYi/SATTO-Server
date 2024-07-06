package com.example.satto.domain.timeTable.controller;

import com.example.satto.domain.timeTable.dto.*;
import com.example.satto.domain.timeTable.service.TimeTableService;
import com.example.satto.domain.timeTableLecture.service.TimeTableLectureService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/timetable")
public class TimeTableController {

    private final TimeTableService timeTableService;
    private final TimeTableLectureService timeTableLectureService;

    @PostMapping
    public BaseResponse<List<TimeTableResponseDTO.MajorCombinationResponseDTO>> createMajorTimeTable(@RequestBody MajorTimeTableRequestDTO createDTO, @AuthenticationPrincipal Users users){
        List<TimeTableResponseDTO.MajorCombinationResponseDTO> result = timeTableService.createMajorTimeTable(createDTO, users);
        return BaseResponse.onSuccess(result);
    }

    @PostMapping("/auto")
    public BaseResponse<List<TimeTableResponseDTO.EntireTimeTableResponseDTO>> createTimeTable(@RequestBody EntireTimeTableRequestDTO createDTO, @AuthenticationPrincipal Users users){
        List<TimeTableResponseDTO.EntireTimeTableResponseDTO> result = timeTableService.createTimeTable(createDTO);
        return BaseResponse.onSuccess(result);
    }

    @PostMapping("/select")
    public BaseResponse<String> selectTimeTable(@RequestBody TimeTableSelectRequestDTO selectDTO, @AuthenticationPrincipal Users users){
        Long timeTableId = timeTableService.createTimeTable(selectDTO,users);
        timeTableLectureService.addLect(selectDTO.codeSectionList(),timeTableId);
        return BaseResponse.onSuccess("성공");
    }

    @GetMapping
    public BaseResponse<TimeTableResponseDTO.SelectTimeTableResponseDTO> getRepresentTimeTable(@AuthenticationPrincipal Users users){
        return BaseResponse.onSuccess(timeTableService.getRepresentTimeTable(users));
    }

    @GetMapping("/list")
    public BaseResponse<List<TimeTableResponseDTO.timeTableListDTO>> getTimeTableList(@AuthenticationPrincipal Users users){
        return BaseResponse.onSuccess(timeTableService.getTimeTableList(users));
    }

    @GetMapping("/{timetableId}")
    public BaseResponse<TimeTableResponseDTO.SelectTimeTableResponseDTO> getTimeTable(@PathVariable(name = "timeTableId") Long timeTableId){
        return BaseResponse.onSuccess(timeTableService.getTimeTable(timeTableId));
    }


    @GetMapping("/{studentId}/timetable")
    public  BaseResponse<List<TimeTableResponseDTO.timeTableListDTO>> getOtherTimeTableList(@PathVariable(name = "studentId") String studentId, @AuthenticationPrincipal Users users) {
        return BaseResponse.onSuccess(timeTableService.getOtherTimeTableList(studentId, users));
    }

    @PatchMapping("/{timeTableId}")
    public BaseResponse<String> updateTimeTable(@PathVariable(name = "timeTableId") Long timeTableId,@RequestBody UpdateTimeTableLectRequestDTO updateDTO){
        timeTableLectureService.deleteAll(timeTableId);
        timeTableLectureService.addLect(updateDTO.codeSectionList(),timeTableId);
        return BaseResponse.onSuccess("수정되었습니다");

    }

    @PatchMapping("/{timeTableId}/private")
    public BaseResponse<String> updateTimeTableIsPublic(@PathVariable(name = "timeTableId") Long timeTableId,@RequestBody UpdateTimeTableRequestDTO isPublic){
        timeTableService.updateTimeTableIsPublic(timeTableId, isPublic);
        return BaseResponse.onSuccess("수정되었습니다");
    }
    @PatchMapping("/{timeTableId}/represent")
    public BaseResponse<String> updateTimeTableIsRepresent(@PathVariable(name = "timeTableId") Long timeTableId, @RequestBody UpdateTimeTableRequestDTO isRepresent, @AuthenticationPrincipal Users users){
        timeTableService.updateTimeTableIsRepresented(timeTableId, isRepresent, users);
        return BaseResponse.onSuccess("수정되었습니다");
    }

    @PatchMapping("/{timeTableId}/name")
    public BaseResponse<String> updateTimeTableName(@PathVariable(name = "timeTableId") Long timeTableId,@RequestBody UpdateTimeTableNameRequestDTO timeTableName){
        timeTableService.updateTimeTableName(timeTableId, timeTableName);
        return BaseResponse.onSuccess("수정되었습니다");
    }

    @DeleteMapping("/{timeTableId}")
    public BaseResponse<String> deleteTimeTable(@PathVariable(name = "timeTableId") Long timeTableId){
        timeTableService.deleteTimeTable(timeTableId);
        return BaseResponse.onSuccess("삭제되었습니다");
    }
}