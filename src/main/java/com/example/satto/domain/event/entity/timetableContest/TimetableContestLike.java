package com.example.satto.domain.event.entity.timetableContest;

import com.example.satto.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class TimetableContestLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_contest_id")
    private TimetableContest timetableContest;

    public TimetableContestLike(Users user, TimetableContest timetableContest) {
        this.user = user;
        this.timetableContest = timetableContest;
    }
}
