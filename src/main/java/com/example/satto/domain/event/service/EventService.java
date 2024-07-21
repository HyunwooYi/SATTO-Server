package com.example.satto.domain.event.service;

import com.example.satto.domain.event.dto.*;
import com.example.satto.domain.users.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    EventCategoryListResponseDto getEventCategoryInfoList();

    PhotoContestListResponseDto getPhotoContestParticipants();

    String likePhotoContest(Long photoContestId, Users user);

    String dislikePhotoContest(Long photoContestId, Users user);

    String deletePhotoContest(Long photoContestId, Users user);

    TimetableContestListResponseDto getTimetableContestParticipants();

    String likeTimetableContest(Long timetableContestId, Users user);

    String dislikeTimetableContest(Long timetableContestId, Users user);

    String deleteTimetableContest(Long timetableContestId, Users user);

    TimetableContestResponseDto.SavedTimetableContest joinTimetableContest(MultipartFile multipartFile, Users user);

    PhotoContestResponseDto.SavedPhotoContest joinPhotoContest(MultipartFile multipartFile, Users user);
}
