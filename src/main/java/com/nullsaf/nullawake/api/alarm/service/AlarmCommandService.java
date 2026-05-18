package com.nullsaf.nullawake.api.alarm.service;

import com.nullsaf.nullawake.api.alarm.dto.request.*;

import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import com.nullsaf.nullawake.api.alarm.repository.AlarmRepository;
import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.tech.repository.UserTechStackRepository;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCommandService {

    private final AlarmRepository alarmRepository;
    private final EntityManager entityManager;
    private final UserTechStackRepository userTechStackRepository;

    public Long createAlarm(Long userId, AlarmRequest request) {
        validateAlarmRequest(request);
        validateUserSelectedStacks(userId, request.techStackIds());

        Long alarmGroupId = alarmRepository.findNextAlarmGroupId();
        Users user = entityManager.getReference(Users.class, userId);

        List<Alarm> alarms = createAlarmRows(
                user,
                alarmGroupId,
                request.dayOfWeeks(),
                request.techStackIds(),
                request.alarmTime()
        );

        alarmRepository.saveAll(alarms);

        return alarmGroupId;
    }

    public void updateAlarm(Long userId, Long alarmGroupId, AlarmRequest request) {
        validateAlarmRequest(request);
        validateUserSelectedStacks(userId, request.techStackIds());

        List<Alarm> existingAlarms = getUserAlarmGroup(userId, alarmGroupId);

        existingAlarms.forEach(Alarm::softDelete);

        Users user = entityManager.getReference(Users.class, userId);

        List<Alarm> newAlarms = createAlarmRows(
                user,
                alarmGroupId,
                request.dayOfWeeks(),
                request.techStackIds(),
                request.alarmTime()
        );

        alarmRepository.saveAll(newAlarms);
    }

    public void deleteAlarm(Long userId, Long alarmGroupId) {
        List<Alarm> alarms = getUserAlarmGroup(userId, alarmGroupId);

        alarms.forEach(Alarm::softDelete);
    }

    public Boolean updateSelected(
            Long userId,
            Long alarmGroupId,
            AlarmSelectedRequest request
    ) {
        List<Alarm> alarms = getUserAlarmGroup(userId, alarmGroupId);

        alarms.forEach(alarm -> alarm.updateSelected(request.selected()));
        return request.selected();
    }

    private List<Alarm> getUserAlarmGroup(Long userId, Long alarmGroupId) {
        List<Alarm> alarms = alarmRepository
                .findByUserUserIdAndAlarmGroupIdAndDeletedAtIsNull(userId, alarmGroupId);

        if (!alarms.isEmpty()) {
            return alarms;
        }

        List<Alarm> existingAlarms = alarmRepository
                .findByAlarmGroupIdAndDeletedAtIsNull(alarmGroupId);

        if (!existingAlarms.isEmpty()) {
            throw new CustomException(ErrorCode.ALARM_ACCESS_DENIED);
        }

        throw new CustomException(ErrorCode.ALARM_NOT_FOUND);
    }

    private List<Alarm> createAlarmRows(
            Users user,
            Long alarmGroupId,
            List<String> dayOfWeeks,
            List<Long> techStackIds,
            java.time.LocalTime alarmTime
    ) {
        List<Alarm> alarms = new ArrayList<>();

        for (String dayOfWeek : dayOfWeeks) {
            for (Long techStackId : techStackIds) {
                TechStack techStack = entityManager.getReference(TechStack.class, techStackId);

                alarms.add(Alarm.builder()
                        .user(user)
                        .techStack(techStack)
                        .alarmGroupId(alarmGroupId)
                        .dayOfWeek(dayOfWeek)
                        .alarmTime(alarmTime)
                        .selected(true)
                        .build());
            }
        }

        return alarms;
    }

    private void validateAlarmRequest(AlarmRequest request) {
        if (request.alarmTime() == null
                || request.dayOfWeeks() == null
                || request.dayOfWeeks().isEmpty()
                || request.techStackIds() == null
                || request.techStackIds().isEmpty()) {
            throw new CustomException(ErrorCode.ALARM_CREATE_BAD_REQUEST);
        }
    }

    private void validateUserSelectedStacks(Long userId, List<Long> techStackIds) {
        for (Long techStackId : techStackIds) {
            boolean selected = userTechStackRepository
                    .existsByUserIdAndTechStackIdAndSelected(userId, techStackId);

            if (!selected) {
                throw new CustomException(ErrorCode.ALARM_INACTIVE_STACK);
            }
        }
    }
}
