package com.example.satto.domain.users.entity;

import com.example.satto.domain.course.entity.Course;
import com.example.satto.domain.event.entity.PhotoContest;
import com.example.satto.domain.event.entity.PhotoContestDislike;
import com.example.satto.domain.event.entity.PhotoContestLike;
import com.example.satto.domain.follow.entity.Follow;
import com.example.satto.domain.users.Role;
import com.example.satto.global.common.BaseEntity;
import com.example.satto.config.security.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class Users extends BaseEntity implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Byte isPublic;  // 0 이면 비공개  1 이면 공개

    private Role role;


    // 팔로잉
    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "followingId")
    private List<Follow> followingList = new ArrayList<>();

    @Getter
    @JsonIgnore
    @OneToMany(mappedBy = "followerId")
    private List<Follow> followerList = new ArrayList<>();

//    // 시간표
//    @OneToMany(mappedBy = "userId")
//    private List<Timetable> timetableList = new ArrayList<>();

    // 이벤트 학교사진콘테스트
    @OneToOne(mappedBy = "user")
    private PhotoContest photoContest;

    // 시간표 좋아요
    @OneToMany(mappedBy = "user")
    private List<PhotoContestLike> likeList = new ArrayList<>();

    // 시간표 싫어요
    @OneToMany(mappedBy = "user")
    private List<PhotoContestDislike> dislikeList = new ArrayList<>();

    // 수강한 강의 목록
    @OneToMany(mappedBy = "user")
    private List<Course> courseList = new ArrayList<>();

    // Token
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "users")
    private List<Token> tokens;

    // UserDeatails 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { // name이 아닌 email 반환
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
