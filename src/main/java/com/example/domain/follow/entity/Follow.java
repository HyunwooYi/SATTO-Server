package com.example.domain.follow.entity;

import com.example.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id")
    private User followedId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User followerId;

    @Column(name = "follow_date")
    private LocalDate followDate;
}
