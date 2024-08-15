package com.example.satto.domain.event.entity.photoContest;

import com.example.satto.domain.event.entity.Event;
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
public class PhotoContest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoContestId;

    private String photoImg;

    @OneToMany(mappedBy = "photoContest", cascade = CascadeType.ALL)
    List<PhotoContestLike> photoContestLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "photoContest", cascade = CascadeType.ALL)
    List<PhotoContestDislike> photoContestDislikeList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
