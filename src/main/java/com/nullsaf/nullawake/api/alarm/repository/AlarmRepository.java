package com.nullsaf.nullawake.api.alarm.repository;

import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("""
            select a
            from Alarm a
            join fetch a.alarmGroup ag
            join fetch a.techStack ts
            join fetch ts.techCategory
            where a.user.userId = :userId
              and a.deletedAt is null
              and ag.deletedAt is null
            """)
    List<Alarm> findByUserUserIdAndDeletedAtIsNull(Long userId);

    @Query("""
            select a
            from Alarm a
            join fetch a.alarmGroup ag
            join fetch a.techStack ts
            join fetch ts.techCategory
            where a.user.userId = :userId
              and ag.alarmGroupId = :alarmGroupId
              and a.deletedAt is null
              and ag.deletedAt is null
            """)
    List<Alarm> findByUserUserIdAndAlarmGroupAlarmGroupIdAndDeletedAtIsNull(
            Long userId,
            Long alarmGroupId
    );
}
