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

    // TODO 사진 업로드 관련 기능
    //PhotoContestResponseDto joinPhotoContest(MultipartFile multipartFile, Users user);
    //TimetableContestResponseDto joinTimetableContest(MultipartFile multipartFile, Users user);
}
