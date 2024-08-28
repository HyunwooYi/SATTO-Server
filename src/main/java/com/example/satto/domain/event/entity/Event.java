package com.example.satto.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "event")
    private List<Contest> contestList;
}
