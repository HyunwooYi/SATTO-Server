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
    public EventCategoryListResponseDto getEventCategoryInfoList() {
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
                    .build();
            eventCategoryResponseDtoList.add(eventCategoryResponseDto);
        }
        return EventCategoryListResponseDto.builder()
                .eventCategoryResponseDtoList(eventCategoryResponseDtoList)
                .build();
    }

    //사진 콘테스트 참여 목록 조회
    @Transactional(readOnly = true)
    public PhotoContestListResponseDto getPhotoContestParticipants() {
        List<PhotoContest> photoContestList = photoContestRepository.findAll();
        List<PhotoContestResponseDto> photoContestResponseDtoList = new ArrayList<>();
        for (PhotoContest photoContest : photoContestList) {
            Long likeCount = photoContestLikeRepository.countByPhotoContest(photoContest);
            Long dislikeCount = photoContestDislikeRepository.countByPhotoContest(photoContest);
            String name = photoContest.getUser().getName();
            PhotoContestResponseDto.builder()
                    .name(name)
                    .likeCount(likeCount)
                    .dislikeCount(dislikeCount)
                    .photo(photoContest.getPhotoImg())
                    .createdAt(photoContest.getCreatedAt())
                    .updatedAt(photoContest.getUpdatedAt())
                    .build();
        }
        return PhotoContestListResponseDto.builder()
                .photoContestResponseDtoList(photoContestResponseDtoList)
                .build();
    }

    // 사진 콘테스트 좋아요 상태 변경
    // TODO BaseResponseStatus로 응답값 반환
    public String likePhotoContest(Long photoContestId, Users user) {
        PhotoContest photoContest = photoContestRepository.findById(photoContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<PhotoContestLike> photoContestLike = photoContestLikeRepository.findByUserAndPhotoContest(user, photoContest);

        if(photoContestLike.isPresent()) {
            photoContestLikeRepository.delete(photoContestLike.get());
            return "좋아요 취소";
        }
        else {
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

        if(photoContestDislike.isPresent()) {
            photoContestDislikeRepository.delete(photoContestDislike.get());
            return "싫어요 취소";
        }
        else {
            photoContestDislikeRepository.saveAndFlush(new PhotoContestDislike(user, photoContest));
            return "싫어요 부여";
        }
    }

    @Override
    public String deletePhotoContest(Long photoContestId, Users user) {
        PhotoContest photoContest = photoContestRepository.findById(photoContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PHOTO));
        if (photoContest.getUser().equals(user)) {
            photoContestRepository.delete(photoContest);
            return "삭제되었습니다.";
        } else {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    @Override
    public TimetableContestListResponseDto getTimetableContestParticipants() {
        List<TimetableContestResponseDto> timetableContestResponseDtoList = eventRepository.findAllTimetableContest();

        return TimetableContestListResponseDto.builder()
                .timetableContestResponseDtoList(timetableContestResponseDtoList)
                .build();
    }

    @Override
    public String likeTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<TimetableContestLike> timetableContestLike = timetableContestLikeRepository.findByUserAndTimetableContest(user, timetableContest);

        if(timetableContestLike.isPresent()) {
            timetableContestLikeRepository.delete(timetableContestLike.get());
            return "좋아요 취소";
        }
        else {
            timetableContestLikeRepository.saveAndFlush(new TimetableContestLike(user, timetableContest));
            return "좋아요 부여";
        }
    }

    @Override
    public String dislikeTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

        Optional<TimetableContestDislike> timetableContestDislike = timetableContestDislikeRepository.findByUserAndTimetableContest(user, timetableContest);

        if(timetableContestDislike.isPresent()) {
            timetableContestDislikeRepository.delete(timetableContestDislike.get());
            return "싫어요 취소";
        }
        else {
            timetableContestDislikeRepository.saveAndFlush(new TimetableContestDislike(user, timetableContest));
            return "싫어요 부여";
        }
    }

    @Override
    public String deleteTimetableContest(Long timetableContestId, Users user) {
        TimetableContest timetableContest = timetableContestRepository.findById(timetableContestId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_PHOTO));
        if (timetableContest.getUser().equals(user)) {
            timetableContestRepository.delete(timetableContest);
            return "삭제되었습니다.";
        } else {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
    }

    @Override
    public TimetableContestResponseDto.SavedTimetableContest joinTimetableContest(MultipartFile file, Users user) {
        String url = null;
        Event event = eventRepository.findByCategory("TimetableContest")
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND_EVENT));

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
