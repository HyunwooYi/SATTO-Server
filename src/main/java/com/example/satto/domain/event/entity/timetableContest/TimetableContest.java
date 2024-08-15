package com.example.satto.domain.event.entity.timetableContest;

import com.example.satto.domain.event.entity.Event;
import com.example.satto.domain.event.entity.photoContest.PhotoContestDislike;
import com.example.satto.domain.event.entity.photoContest.PhotoContestLike;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class TimetableContest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timetableContestId;

    private String photoImg;

    @OneToMany(mappedBy = "timetableContest", cascade = CascadeType.ALL)
    List<TimetableContestLike> timetableContestLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "timetableContest", cascade = CascadeType.ALL)
    List<TimetableContestDislike> timetableContestDislikeList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
