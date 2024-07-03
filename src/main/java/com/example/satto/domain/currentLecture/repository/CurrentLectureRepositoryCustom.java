package com.example.satto.domain.currentLecture.repository;

import com.example.satto.domain.currentLecture.dto.CurrentLectureResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CurrentLectureRepositoryCustom {
    Page<CurrentLectureResponseDTO> findLectures(
            String lectName, List<Integer> grade, int elective,
            int normal, int essential, byte humanity, byte society,
            byte nature, byte engineering, byte art, byte isCyber, List<String> timeZone, Pageable pageable);
}

