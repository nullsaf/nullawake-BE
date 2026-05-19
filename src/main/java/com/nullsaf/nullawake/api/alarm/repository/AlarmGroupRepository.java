package com.nullsaf.nullawake.api.alarm.repository;

import com.nullsaf.nullawake.api.alarm.entity.AlarmGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmGroupRepository extends JpaRepository<AlarmGroup, Long> {

    Optional<AlarmGroup> findByAlarmGroupIdAndDeletedAtIsNull(Long alarmGroupId);

    boolean existsByAlarmGroupIdAndDeletedAtIsNull(Long alarmGroupId);
}
