package com.example.satto.domain.event.service.impl;

import com.example.satto.domain.event.dto.*;
import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.event.entity.photoContest.PhotoContest;
import com.example.satto.domain.event.entity.photoContest.PhotoContestDislike;
import com.example.satto.domain.event.entity.photoContest.PhotoContestLike;
import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import com.example.satto.domain.event.entity.timetableContest.TimetableContestDislike;
import com.example.satto.domain.event.entity.timetableContest.TimetableContestLike;
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
    private final PhotoContestRepository photoContestRepository;
    private final PhotoContestLikeRepository photoContestLikeRepository;
    private final PhotoContestDislikeRepository photoContestDislikeRepository;
    private final TimetableContestRepository timetableContestRepository;
    private final TimetableContestLikeRepository timetableContestLikeRepository;
    private final TimetableContestDislikeRepository timetableContestDislikeRepository;
    private final UuidRepository uuidRepository;
    private final S3Manager s3Manager;

    // 이벤트 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<EventCategoryResponseDto> getEventCategoryInfoList() {
        List<Event> eventList = eventRepository.findAll();
        List<EventCategoryResponseDto> eventCategoryResponseDtoList = new ArrayList<>();
        for (Event event : eventList) {
            Long participantsCount = photoContestRepository.countByEvent(event);
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

    //사진 콘테스트 참여 목록 조회
    @Transactional(readOnly = true)
    public List<PhotoContestResponseDto> getPhotoContestParticipants() {
        List<PhotoContest> photoContestList = photoContestRepository.findAll();
        List<PhotoContestResponseDto> photoContestResponseDtoList = new ArrayList<>();
        for (PhotoContest photoContest : photoContestList) {
            Long likeCount = photoContestLikeRepository.countByPhotoContest(photoContest);
            Long dislikeCount = photoContestDislikeRepository.countByPhotoContest(photoContest);
            String name = photoContest.getUser().getName();
            photoContestResponseDtoList.add(PhotoContestResponseDto.builder()
                    .photoContestId(photoContest.getPhotoContestId())
                    .name(name)
                    .likeCount(likeCount)
                    .dislikeCount(dislikeCount)
                    .photo(photoContest.getPhotoImg())
                    .createdAt(photoContest.getCreatedAt())
                    .updatedAt(photoContest.getUpdatedAt())
                    .build());
        }
        return photoContestResponseDtoList;
    }

    // 사진 콘테스트 좋아요 상태 변경
    // TODO BaseResponseStatus로 응답값 반환
    public String likePhotoContest(Long photoContestId, Users user) {
        PhotoContest photoContest = photoContestRepository.findById(photoContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<PhotoContestLike> photoContestLike = photoContestLikeRepository.findByUserAndPhotoContest(user, photoContest);

        if (photoContestLike.isPresent()) {
            photoContestLikeRepository.delete(photoContestLike.get());
            return "좋아요 취소";
        } else {
            photoContestLikeRepository.saveAndFlush(new PhotoContestLike(user, photoContest));
            return "좋아요 부여";
        }
    }

    // 사진 콘테스트 싫어요 상태 변경
    // TODO BaseResponseStatus로 응답값 반환
    public String dislikePhotoContest(Long photoContestId, Users user) {
        PhotoContest photoContest = photoContestRepository.findById(photoContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<PhotoContestDislike> photoContestDislike = photoContestDislikeRepository.findByUserAndPhotoContest(user, photoContest);

        if (photoContestDislike.isPresent()) {
            photoContestDislikeRepository.delete(photoContestDislike.get());
            return "싫어요 취소";
        } else {
            photoContestDislikeRepository.saveAndFlush(new PhotoContestDislike(user, photoContest));
            return "싫어요 부여";
        }
    }

    @Override
    public String deletePhotoContest(Long photoContestId, Users user) {
        PhotoContest photoContest = photoContestRepository.findById(photoContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PHOTO));
        if (photoContest.getUser().getUserId().equals(user.getUserId())) {
            photoContestRepository.delete(photoContest);
            return "삭제되었습니다.";
        } else {
            return "사용자 본인이 참여한 이벤트만 삭제할 수 있습니다.";
        }
    }

    @Override
    public List<TimetableContestResponseDto> getTimetableContestParticipants() {
        List<TimetableContest> timetableContestList = timetableContestRepository.findAll();
        List<TimetableContestResponseDto> timetableContestResponseDtoList = new ArrayList<>();
        for (TimetableContest timetableContest : timetableContestList) {
            Long likeCount = timetableContestLikeRepository.countByTimetableContest(timetableContest);
            Long dislikeCount = timetableContestDislikeRepository.countByTimetableContest(timetableContest);
            String name = timetableContest.getUser().getName();
            timetableContestResponseDtoList.add(TimetableContestResponseDto.builder()
                    .timetableContestId(timetableContest.getTimetableContestId())
                    .name(name)
                    .likeCount(likeCount)
                    .dislikeCount(dislikeCount)
                    .photo(timetableContest.getPhotoImg())
                    .createdAt(timetableContest.getCreatedAt())
                    .updatedAt(timetableContest.getUpdatedAt())
                    .build());
        }
        return timetableContestResponseDtoList;
    }

    @Override
    public String likeTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<TimetableContestLike> timetableContestLike = timetableContestLikeRepository.findByUserAndTimetableContest(user, timetableContest);

        if (timetableContestLike.isPresent()) {
            timetableContestLikeRepository.delete(timetableContestLike.get());
            return "좋아요 취소";
        } else {
            timetableContestLikeRepository.saveAndFlush(new TimetableContestLike(user, timetableContest));
            return "좋아요 부여";
        }
    }

    @Override
    public String dislikeTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<TimetableContestDislike> timetableContestDislike = timetableContestDislikeRepository.findByUserAndTimetableContest(user, timetableContest);

        if (timetableContestDislike.isPresent()) {
            timetableContestDislikeRepository.delete(timetableContestDislike.get());
            return "싫어요 취소";
        } else {
            timetableContestDislikeRepository.saveAndFlush(new TimetableContestDislike(user, timetableContest));
            return "싫어요 부여";
        }
    }

    @Override
    public String deleteTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PHOTO));
        if (timetableContest.getUser().getUserId().equals(user.getUserId())) {
            timetableContestRepository.delete(timetableContest);
            return "삭제되었습니다.";
        } else {
            return "사용자 본인이 참여한 이벤트만 삭제할 수 있습니다.";
        }
    }

    @Override
    public TimetableContestResponseDto.SavedTimetableContest joinTimetableContest(MultipartFile file, Users user) {
        String url = null;
        Event event = eventRepository.findByCategory("TimetableContest")
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        // 중복 참여 체크
        if (timetableContestRepository.existsByUserAndEvent(user, event)) {
            throw new GeneralException(ErrorStatus._DUPLICATE_CONTEST_ENTRY);
        }

        if (file != null && !file.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            url = s3Manager.uploadFile(s3Manager.generateImage(savedUuid), file);
            TimetableContest newTimetableContest = TimetableContest.builder()
                    .photoImg(url)
                    .user(user)
                    .event(event)
                    .build();
            timetableContestRepository.save(newTimetableContest);
        }
        return TimetableContestResponseDto.SavedTimetableContest.builder()
                .name(user.getName())
                .photo(url)
                .likeCount(0L)
                .dislikeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public PhotoContestResponseDto.SavedPhotoContest joinPhotoContest(MultipartFile file, Users user) {
        String url = null;
        Event event = eventRepository.findByCategory("PhotoContest")
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        // 중복 참여 체크
        if (photoContestRepository.existsByUserAndEvent(user, event)) {
            throw new GeneralException(ErrorStatus._DUPLICATE_CONTEST_ENTRY);
        }

        if (file != null && !file.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            url = s3Manager.uploadFile(s3Manager.generateImage(savedUuid), file);
            PhotoContest newPhotoContest = PhotoContest.builder()
                    .photoImg(url)
                    .user(user)
                    .event(event)
                    .build();
            photoContestRepository.save(newPhotoContest);
        }
        return PhotoContestResponseDto.SavedPhotoContest.builder()
                .name(user.getName())
                .photo(url)
                .likeCount(0L)
                .dislikeCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
