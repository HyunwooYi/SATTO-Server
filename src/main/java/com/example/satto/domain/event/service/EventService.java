package com.example.satto.domain.event.service;

import com.example.satto.domain.event.dto.*;
import com.example.satto.domain.users.entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    List<EventCategoryResponseDto> getEventCategoryInfoList();

    // Event API 하나로 통합
    List<ContestResponseDto> getContestParticipants(String category);

    String likeContest(Long contestId, Users user);

    String dislikeContest(Long contestId, Users user);

    String deleteContest(Long contestId, Users user);

    ContestResponseDto.SavedContest joinContest(String category, MultipartFile multipartFile, Users user);
}
