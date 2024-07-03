package com.example.satto.domain.timeTable.entity;

import com.example.satto.domain.users.entity.Users;
import com.example.satto.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeTable extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long timetableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @Column(name = "timetable_name")
    private String timetableName;
    @Column(name = "is_public")
    private Boolean isPublic = true;
    @Column(name = "is_represented")
    private Boolean isRepresented = false;
    @Column(name = "semester_year")
    private String semesterYear;

    public void updateIsPublic(boolean isPublic){
        this.isPublic = isPublic;
    }

    public void updateIsRepresented(boolean isRepresented){
        this.isRepresented = isRepresented;
    }
    public void updateName(String timetableName){
        this.timetableName = timetableName;
    }
}
