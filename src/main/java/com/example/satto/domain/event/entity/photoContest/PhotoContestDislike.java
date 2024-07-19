package com.example.satto.domain.event.entity.photoContest;

import com.example.satto.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class PhotoContestDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dislikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_contest_id")
    private PhotoContest photoContest;

    public PhotoContestDislike(Users user, PhotoContest photoContest) {
        this.user = user;
        this.photoContest = photoContest;
    }
}
