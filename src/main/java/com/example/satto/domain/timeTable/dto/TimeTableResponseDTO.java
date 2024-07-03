package com.example.satto.domain.timeTable.dto;

import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import com.example.satto.domain.currentLecture.entity.CurrentLecture;
import com.example.satto.domain.timeTable.entity.TimeTable;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TimeTableResponseDTO(
) {

    @Builder
    public static record EntireTimeTableResponseDTO(
            List<TimeTableResponseDTO.TimeTableLectureDTO> timeTable,
            String totalTime
    ) {
        public static String calculateTotalTime(List<TimeTableResponseDTO.TimeTableLectureDTO> timetable) {
            String tt = "";
            for (TimeTableResponseDTO.TimeTableLectureDTO lecture : timetable) {
                tt += lecture.lectTime();
            }
            return tt;
        }


        public static TimeTableResponseDTO.EntireTimeTableResponseDTO from(List<TimeTableResponseDTO.TimeTableLectureDTO> timeTable){
            return EntireTimeTableResponseDTO.builder()
                    .timeTable(timeTable)
                    .totalTime(calculateTotalTime(timeTable))
                    .build();
        }

        public static List<TimeTableResponseDTO.EntireTimeTableResponseDTO> fromList(List<List<TimeTableResponseDTO.TimeTableLectureDTO>> timeTableList){
            return
                    timeTableList.stream()
                            .map(TimeTableResponseDTO.EntireTimeTableResponseDTO::from)
                            .collect(Collectors.toList());
        }
    }

    @Builder
    public static record timeTableListDTO(
            Long timeTableId,
            String semesterYear,
            String timeTableName,
            boolean isPublic,
            boolean isRepresent
    ){
        public static TimeTableResponseDTO.timeTableListDTO from(TimeTable timeTable){
            return TimeTableResponseDTO.timeTableListDTO.builder()
                    .timeTableId(timeTable.getTimetableId())
                    .semesterYear(timeTable.getSemesterYear())
                    .timeTableName(timeTable.getTimetableName())
                    .isPublic(timeTable.getIsPublic())
                    .isRepresent(timeTable.getIsRepresented())
                    .build();
        }

        public static List<TimeTableResponseDTO.timeTableListDTO> fromList(List<TimeTable> timeTableList){
            return
                    timeTableList.stream()
                            .map(TimeTableResponseDTO.timeTableListDTO::from)
                            .collect(Collectors.toList());
        }

    }

    public static record MajorCombinationResponseDTO(
            Set<LectureCombination> combination
    ) {
    }

    @Builder
    public static record SelectTimeTableResponseDTO(
            Long timeTableId,
            List<TimeTableResponseDTO.TimeTableLectureDTO> lects,
            String semesterYear,
            String timeTableName,
            boolean isPublic,
            boolean isRepresented

    ){
        public static TimeTableResponseDTO.SelectTimeTableResponseDTO from(List<TimeTableResponseDTO.TimeTableLectureDTO> lects, TimeTable timeTable){

            return SelectTimeTableResponseDTO.builder()
                    .timeTableId(timeTable.getTimetableId())
                    .lects(lects)
                    .semesterYear(timeTable.getSemesterYear())
                    .timeTableName(timeTable.getTimetableName())
                    .isPublic(timeTable.getIsPublic())
                    .isRepresented(timeTable.getIsRepresented())
                    .build();
        }

    }

    public static record LectureCombination(
            String lectName,
            String code
    ){}

    @Builder
    public static record TimeTableLectureDTO(
            String code,
            String codeSection,
            String lectName,
            String professor,
            String lectTime,
            String cmpDiv,
            int credit
    ){
        public static TimeTableResponseDTO.TimeTableLectureDTO from(CurrentLecture currentLecture){
            return TimeTableLectureDTO.builder()
                    .code(currentLecture.getCode())
                    .codeSection(currentLecture.getCodeSection())
                    .lectName(currentLecture.getLectName())
                    .professor(currentLecture.getProfessor())
                    .lectTime(currentLecture.getLectTime())
                    .cmpDiv(currentLecture.getCmpDiv())
                    .credit(currentLecture.getCredit())
                    .build();
        }

        public static List<TimeTableResponseDTO.TimeTableLectureDTO> fromList(List<CurrentLecture> currentLectures){
            return
                    currentLectures.stream()
                            .map(TimeTableResponseDTO.TimeTableLectureDTO::from)
                            .collect(Collectors.toList());
        }
    }


}
