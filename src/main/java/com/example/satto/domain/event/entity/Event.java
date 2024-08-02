package com.example.satto.domain.event.entity;

import com.example.satto.domain.event.entity.photoContest.PhotoContest;
import com.example.satto.domain.event.entity.timetableContest.TimetableContest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String category;
    private LocalDateTime startWhen;
    private LocalDateTime untilWhen;
    private String content;

    @OneToOne(mappedBy = "event")
    private PhotoContest photoContest;

    @OneToOne(mappedBy = "event")
    private TimetableContest timetableContest;

}
