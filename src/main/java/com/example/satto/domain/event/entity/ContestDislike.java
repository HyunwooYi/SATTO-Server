package com.example.satto.domain.event.entity;

import com.example.satto.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class ContestDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dislikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    public ContestDislike(Users user, Contest contest) {
        this.user = user;
        this.contest = contest;
    }
}
