package com.nullsaf.nullawake.api.alarm.repository;

import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByUserUserIdAndDeletedAtIsNull(Long userId);

    List<Alarm> findByUserUserIdAndAlarmGroupIdAndDeletedAtIsNull(
            Long userId,
            Long alarmGroupId
    );

    List<Alarm> findByAlarmGroupIdAndDeletedAtIsNull(Long alarmGroupId);

    @Query("select coalesce(max(a.alarmGroupId), 0) + 1 from Alarm a")
    Long findNextAlarmGroupId();
}
