package com.example.satto.domain.event.service.impl;

import com.example.satto.domain.event.dto.*;
import com.example.satto.domain.event.entity.Contest;
import com.example.satto.domain.event.entity.ContestDislike;
import com.example.satto.domain.event.entity.ContestLike;
import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.event.repository.*;
import com.example.satto.domain.event.service.EventService;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.code.status.ErrorStatus;
import com.example.satto.global.common.exception.GeneralException;
import com.example.satto.s3.S3Manager;
import com.example.satto.s3.uuid.Uuid;
import com.example.satto.s3.uuid.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ContestRepository contestRepository;
    private final ContestLikeRepository contestLikeRepository;
    private final ContestDislikeRepository contestDislikeRepository;
    private final UuidRepository uuidRepository;
    private final S3Manager s3Manager;

    // 이벤트 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<EventCategoryResponseDto> getEventCategoryInfoList() {
        List<Event> eventList = eventRepository.findAll();
        List<EventCategoryResponseDto> eventCategoryResponseDtoList = new ArrayList<>();
        for (Event event : eventList) {
            Long participantsCount = contestRepository.countByEvent(event);
            EventCategoryResponseDto eventCategoryResponseDto = EventCategoryResponseDto.builder()
                    .eventId(event.getEventId())
                    .category(event.getCategory())
                    .participantsCount(participantsCount)
                    .startWhen(event.getStartWhen())
                    .untilWhen(event.getUntilWhen())
                    .content(event.getContent())
                    .build();
            eventCategoryResponseDtoList.add(eventCategoryResponseDto);
        }
        return eventCategoryResponseDtoList;
    }

    // Event API 하나로 통합
    @Override
    public List<ContestResponseDto> getContestParticipants(String category) {
        List<Contest> contestList = contestRepository.findAllByCategory(category);
        List<ContestResponseDto> contestResponseDtoList = new ArrayList<>();
        for (Contest contest : contestList) {
            Long likeCount = contestLikeRepository.countByContest(contest);
            Long dislikeCount = contestDislikeRepository.countByContest(contest);
            String name = contest.getUser().getName();
            contestResponseDtoList.add(ContestResponseDto.builder()
                    .contestId(contest.getContestId())
                    .name(name)
                    .likeCount(likeCount)
                    .dislikeCount(dislikeCount)
                    .photo(contest.getImg())
                    .createdAt(contest.getCreatedAt())
                    .updatedAt(contest.getUpdatedAt())
                    .build());
        }
        return contestResponseDtoList;
    }

    @Override
    public String likeContest(Long contestId, Users user) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<ContestLike> contestLike = contestLikeRepository.findByUserAndContest(user, contest);

        if (contestLike.isPresent()) {
            contestLikeRepository.delete(contestLike.get());
            return "좋아요 취소";
        } else {
            contestLikeRepository.saveAndFlush(new ContestLike(user, contest));
            return "좋아요 부여";
        }
    }

    @Override
    public String dislikeContest(Long contestId, Users user) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<ContestDislike> contestDislike = contestDislikeRepository.findByUserAndContest(user, contest);

        if (contestDislike.isPresent()) {
            contestDislikeRepository.delete(contestDislike.get());
            return "싫어요 취소";
        } else {
            contestDislikeRepository.saveAndFlush(new ContestDislike(user, contest));
            return "싫어요 부여";
        }
    }

    @Override
    public String deleteContest(Long contestId, Users user) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PHOTO));
        if (contest.getUser().getUserId().equals(user.getUserId())) {
            contestRepository.delete(contest);
            return "삭제되었습니다.";
        } else {
            return "사용자 본인이 참여한 이벤트만 삭제할 수 있습니다.";
        }
    }

    @Override
    public ContestResponseDto.SavedContest joinContest(String category, MultipartFile file, Users user) {
        String url = null;
        Event event = eventRepository.findByCategory(category)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        // 중복 참여 체크
        if (contestRepository.existsByUserAndEvent(user, event)) {
            throw new GeneralException(ErrorStatus._DUPLICATE_CONTEST_ENTRY);
        }

        if (file != null && !file.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            url = s3Manager.uploadFile(s3Manager.generateImage(savedUuid), file);
            Contest newContest = Contest.builder()
                    .img(url)
                    .user(user)
                    .event(event)
                    .category(category)
                    .build();
            contestRepository.save(newContest);
        }
        return ContestResponseDto.SavedContest.builder()
                .name(user.getName())
                .photo(url)
                .likeCount(0L)
                .dislikeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
