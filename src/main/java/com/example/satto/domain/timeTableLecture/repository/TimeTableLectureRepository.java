package com.example.satto.domain.timeTableLecture.repository;

import com.example.satto.domain.currentLecture.entity.CurrentLecture;
import com.example.satto.domain.timeTable.entity.TimeTable;
import com.example.satto.domain.timeTableLecture.entity.TimeTableLecture;
import com.example.satto.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TimeTableLectureRepository extends JpaRepository<TimeTableLecture, Long> {

    @Query("select l " +
            "from TimeTableLecture l " +
            "where l.currentLecture.codeSection = :codeSection and l.timeTable.timetableId = :timeTableId")
    TimeTableLecture findTimeTableLectureByTimeTableIdAndCodeSection(@Param("timeTableId")Long timeTableId,
                                                                     @Param("codeSection") String codeSection);

    @Query("select l from TimeTableLecture l where l.timeTable.timetableId = :timeTableId")
    List<TimeTableLecture> findTimeTableLecturesByTimeTableId(@Param("timeTableId")Long timeTableId);

    @Query("select l.currentLecture from TimeTableLecture l where l.timeTable.timetableId = :timeTableId")
    List<CurrentLecture> findCurrentLecturesByTimeTableId(@Param("timeTableId")Long timeTableId);

    @Transactional
    @Modifying
    @Query("delete from TimeTableLecture l where l.timeTable.timetableId = :timeTableId")
    void deleteByTimeTableId(@Param("timeTableId") Long timeTableId);

    void deleteAllByTimeTable_Users(Users user);
}
