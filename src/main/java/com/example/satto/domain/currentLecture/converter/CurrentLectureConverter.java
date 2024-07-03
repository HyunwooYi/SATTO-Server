package com.example.satto.domain.currentLecture.converter;

import com.example.satto.domain.currentLecture.dto.CurrentLectureListResponseDTO;
import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import com.example.satto.domain.currentLecture.entity.CurrentLecture;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class CurrentLectureConverter {

    //현재 학기 강의 조회 응답 Dto 변환 메소드
    public static CurrentLectureResponseDTO toCurrentLectureResponseDto(CurrentLecture currentLecture) {
        return CurrentLectureResponseDTO.builder()
                .department(currentLecture.getDepartment())
                .lectName(currentLecture.getLectName())
                .code(currentLecture.getCode())
                .codeSection(currentLecture.getCodeSection())
                .professor(currentLecture.getProfessor())
                .lectTime(currentLecture.getLectTime())
                .cmpDiv(currentLecture.getCmpDiv())
                .subjectType(currentLecture.getSubjectType())
                .credit(currentLecture.getCredit())
                .build();
    }

    //현재 학기 강의 조회 리스트 응답 Dto 변환 메소드
    public static CurrentLectureListResponseDTO toCurrentLectureResponseDtoList(List<CurrentLecture> currentLectureList) {
        List<CurrentLectureResponseDTO> currentLectureResponseDTOList = currentLectureList.stream()
                .map(CurrentLectureConverter::toCurrentLectureResponseDto)
                .toList();
        return CurrentLectureListResponseDTO.builder()
                .currentLectureResponseDTOList(currentLectureResponseDTOList)
                .build();
    }

    // DTO 리스트 변환 메소드
    public static List<CurrentLectureResponseDTO> toCurrentLectureDtoList(List<CurrentLecture> currentLectureList) {
        return currentLectureList.stream()
                .map(CurrentLectureConverter::toCurrentLectureResponseDto)
                .toList();
    }

    public static CurrentLectureListResponseDTO toCurrentLectureDtoList(Page<CurrentLectureResponseDTO> currentLectureList) {
        return CurrentLectureListResponseDTO.builder()
                .currentLectureResponseDTOList(currentLectureList.stream().collect(Collectors.toList()))                .isLast(currentLectureList.isLast())
                .isFirst(currentLectureList.isFirst())
                .totalPage(currentLectureList.getTotalPages())
                .listSize(currentLectureList.getSize())
                .build();
    }

}
