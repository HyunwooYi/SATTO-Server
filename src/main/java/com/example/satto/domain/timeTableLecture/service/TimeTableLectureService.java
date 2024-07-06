package com.example.satto.domain.timeTableLecture.service;

import com.example.satto.domain.currentLecture.entity.CurrentLecture;
import com.example.satto.domain.currentLecture.repository.CurrentLectureRepository;
import com.example.satto.domain.timeTable.entity.TimeTable;
import com.example.satto.domain.timeTable.repository.TimeTableRepository;
import com.example.satto.domain.timeTableLecture.entity.TimeTableLecture;
import com.example.satto.domain.timeTableLecture.repository.TimeTableLectureRepository;
import com.example.satto.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableLectureService {

    private final CurrentLectureRepository currentLectureRepository;
    private final TimeTableRepository timeTableRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;

    public void addLect(List<String> codeSectionList, Long timeTableId){

        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        for(String codeSection : codeSectionList) {
            CurrentLecture lect = currentLectureRepository.findCurrentLectureByCodeSection(codeSection);
            timeTableLectureRepository.save(new TimeTableLecture(timeTable, lect));
        }
    }

    public void deleteLect(String codeSection, Long timeTableId){
        TimeTableLecture timeTableLecture = timeTableLectureRepository.findTimeTableLectureByTimeTableIdAndCodeSection(timeTableId, codeSection);
        timeTableLectureRepository.delete(timeTableLecture);
    }

    public void deleteAll(Long timeTableId){
        timeTableLectureRepository.deleteByTimeTableId(timeTableId);
    }
}
