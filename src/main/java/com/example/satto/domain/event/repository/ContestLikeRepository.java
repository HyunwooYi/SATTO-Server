package com.example.satto.domain.event.repository;

import com.example.satto.domain.event.entity.Contest;
import com.example.satto.domain.event.entity.ContestLike;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContestLikeRepository extends JpaRepository<ContestLike, Long> {
    Optional<ContestLike> findByUserAndContest(Users user, Contest contest);

    Long countByContest(Contest photoContest);

    boolean existsByUserAndContest(Users user, Contest contest);
}
