package com.example.satto.domain.currentLecture.service;

import com.example.satto.domain.currentLecture.dto.CurrentLectureRequestDto;
import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import com.example.satto.domain.currentLecture.repository.CurrentLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrentLectureService {

    private final CurrentLectureRepository currentLectureRepository;

    public Page<CurrentLectureResponseDTO> getLectures(CurrentLectureRequestDto currentLectureRequestDto, Integer page) {
        return currentLectureRepository.findLectures(
                currentLectureRequestDto.lectName(),
                currentLectureRequestDto.grade(),
                currentLectureRequestDto.elective(),
                currentLectureRequestDto.normal(),
                currentLectureRequestDto.essential(),
                currentLectureRequestDto.humanity(),
                currentLectureRequestDto.society(),
                currentLectureRequestDto.nature(),
                currentLectureRequestDto.engineering(),
                currentLectureRequestDto.art(),
                currentLectureRequestDto.isCyber(),
                List.of(currentLectureRequestDto.timeZone().split(" ")),
                PageRequest.of(page, 10)
        );
    }
}
