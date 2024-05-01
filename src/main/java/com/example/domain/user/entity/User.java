package com.example.domain.user.entity;

import com.example.domain.follow.entity.Follow;
import jakarta.persistence.*;
import org.springframework.data.relational.core.sql.Like;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String department;

    @Column(name = "student_id", nullable = false)
    private int studentId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_public", nullable = false)
    private Byte isPublic;

//    // 팔로우
//    @OneToMany(mappedBy = "followedId")
//    private List<Follow> followedList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "followerId")
//    private List<Follow> followerList = new ArrayList<>();
//
//    // 시간표
//    @OneToMany(mappedBy = "userId")
//    private List<Timetable> timetableList = new ArrayList<>();
//
//    // 이벤트_학교사진콘테스트
//    @OneToOne(mappedBy = "userId")
//    private List<Event> eventList = new ArrayList<>();
//
//    // 시간표 좋아요
//    @OneToMany(mappedBy = "userId")
//    private List<Like> dislikeList = new ArrayList<>();
//
//    // 시간표 싫어요
//    @OneToMany(mappedBy = "userId")
//    private List<Dislike> dislikeList = new ArrayList<>();
//
//    // 수강한 강의 목록
//    @OneToMany(mappedBy = "userId")
//    private List<Course> courseList = new ArrayList<>();

}
