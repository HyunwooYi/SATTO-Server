package com.example.satto.domain.event.controller;

import com.example.satto.domain.event.dto.EventCategoryResponseDto;
import com.example.satto.domain.event.service.EventService;
import com.example.satto.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@Tag(name = "Event Domain Api", description = "이벤트 관련 Api입니다.")
public class EventController {

    private final EventService eventService;

    @Operation(method = "GET", summary = "이벤트 목록 조회", description = "현재 진행중인 이벤트 목록 조회")
    @GetMapping("")
    public BaseResponse<List<EventCategoryResponseDto>> getEventList() {
        return BaseResponse.onSuccess(eventService.getEventCategoryInfoList());
    }
}
