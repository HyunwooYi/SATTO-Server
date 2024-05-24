package com.example.satto.domain.follow.repository;

import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdUserIdAndFollowingIdUserId(Long userId, Long followingId);

    boolean existsByFollowerIdUserIdAndFollowingIdUserIdAndRequest(Long userId, Long followingId, int i);

    Follow findByFollowerIdUserIdAndFollowingIdUserIdAndRequest(Long followerId, Long userId, int i);

    Follow findByFollowerIdUserIdAndFollowingIdUserId(Long followerId, Long userId);
}
