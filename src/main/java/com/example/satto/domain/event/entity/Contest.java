package com.example.satto.domain.event.entity;

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
public class Contest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contestId;

    private String img;
    private String category;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    List<ContestLike> contestLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL)
    List<ContestDislike> contestDislikeList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
