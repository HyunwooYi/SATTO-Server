package com.example.satto.domain.timeTable.repository;


import com.example.satto.domain.timeTable.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query("select t from TimeTable t where t.users.userId = :userId")
    List<TimeTable> findAllByUserId(@Param("userId") Long userId);

    @Query("select t from TimeTable t where t.users.studentId = :studentId and t.isPublic = true ")
    List<TimeTable> findPublicTimeTableByStudentId(@Param("studentId") String studentId);

    @Query("SELECT t FROM TimeTable t WHERE t.users.userId = :userId AND t.isRepresented = true ORDER BY t.createdAt DESC")
    TimeTable findLatestRepresentedTimeTableByUserId(@Param("userId") Long userId);
}
