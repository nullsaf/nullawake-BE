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

    /**
     * 유저의 알람을 생성하는 메서드
     * @param userId
     * @param request
     * @return
     */
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

    /** 유저의 알람을 수정하는 메서드
     *
     * @param userId
     * @param alarmGroupId
     * @param request
     */
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

    /**
     * 알람을 삭제하는 메서드
     * @param userId
     * @param alarmGroupId
     */
    public void deleteAlarm(Long userId, Long alarmGroupId) {
        List<Alarm> alarms = getUserAlarmGroup(userId, alarmGroupId);

        alarms.forEach(Alarm::softDelete);
    }

    /**
     * 알람 활성화/비활성화를 위한 메서드
     * @param userId
     * @param alarmGroupId
     * @param request
     * @return
     */
    public Boolean updateSelected(
            Long userId,
            Long alarmGroupId,
            AlarmSelectedRequest request
    ) {
        List<Alarm> alarms = getUserAlarmGroup(userId, alarmGroupId);

        alarms.forEach(alarm -> alarm.updateSelected(request.selected()));
        return request.selected();
    }

    /**
     * 유저가 선택한 기술스택을 확인하는 메서드
     * @param userId
     * @param alarmGroupId
     * @return
     */
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

    /**
     * 알람 요청 중복 데이터 제거 처리
     * @param user
     * @param alarmGroupId
     * @param dayOfWeeks
     * @param techStackIds
     * @param alarmTime
     * @return
     */
    private List<Alarm> createAlarmRows(
            Users user,
            Long alarmGroupId,
            List<String> dayOfWeeks,
            List<Long> techStackIds,
            java.time.LocalTime alarmTime
    ) {
        List<Alarm> alarms = new ArrayList<>();

        List<String> distinctDayOfWeeks = dayOfWeeks.stream()
                .distinct()
                .toList();

        List<Long> distinctTechStackIds = techStackIds.stream()
                .distinct()
                .toList();

        for (String dayOfWeek : distinctDayOfWeeks) {
            for (Long techStackId : distinctTechStackIds) {
                TechStack techStack = entityManager.getReference(TechStack.class, techStackId);

                alarms.add(Alarm.builder()
                        .user(user)
                        .techStack(techStack)
                        .alarmGroupId(alarmGroupId)
                        .dayOfWeeks(dayOfWeek)
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
        List<Long> distinctTechStackIds = techStackIds.stream()
                .distinct()
                .toList();

        for (Long techStackId : distinctTechStackIds) {
            boolean selected = userTechStackRepository
                    .existsByUserUserIdAndTechStackTechStackIdAndSelectedTrue(userId, techStackId);

            if (!selected) {
                throw new CustomException(ErrorCode.ALARM_INACTIVE_STACK);
            }
        }
    }
}
