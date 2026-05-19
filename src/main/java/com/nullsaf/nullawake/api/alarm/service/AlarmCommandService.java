package com.nullsaf.nullawake.api.alarm.service;

import com.nullsaf.nullawake.api.alarm.dto.request.AlarmRequest;
import com.nullsaf.nullawake.api.alarm.dto.request.AlarmSelectedRequest;
import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import com.nullsaf.nullawake.api.alarm.entity.AlarmGroup;
import com.nullsaf.nullawake.api.alarm.repository.AlarmGroupRepository;
import com.nullsaf.nullawake.api.alarm.repository.AlarmRepository;
import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.tech.repository.usertechstack.UserTechStackRepository;
import com.nullsaf.nullawake.api.user.entity.Users;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmCommandService {

    private final AlarmRepository alarmRepository;
    private final AlarmGroupRepository alarmGroupRepository;
    private final EntityManager entityManager;
    private final UserTechStackRepository userTechStackRepository;

    /**
     * 유저의 알람을 생성하는 메서드
     * @param userId
     * @param request
     * @return
     */
    public Long createAlarm(Long userId, AlarmRequest request) {
        validateUserSelectedStacks(userId, request.techStackIds());

        Users user = entityManager.getReference(Users.class, userId);

        AlarmGroup alarmGroup = alarmGroupRepository.save(
                AlarmGroup.builder().build()
        );

        List<Alarm> alarms = createAlarmRows(
                user,
                alarmGroup,
                request.dayOfWeeks(),
                request.techStackIds(),
                request.alarmTime()
        );

        alarmRepository.saveAll(alarms);

        return alarmGroup.getAlarmGroupId();
    }

    /** 유저의 알람을 수정하는 메서드
     *
     * @param userId
     * @param alarmGroupId
     * @param request
     */
    public void updateAlarm(Long userId, Long alarmGroupId, AlarmRequest request) {
        validateUserSelectedStacks(userId, request.techStackIds());

        AlarmGroup alarmGroup = getAlarmGroup(alarmGroupId);
        List<Alarm> existingAlarms = getUserAlarmGroup(userId, alarmGroupId);

        existingAlarms.forEach(Alarm::softDelete);

        Users user = entityManager.getReference(Users.class, userId);

        List<Alarm> newAlarms = createAlarmRows(
                user,
                alarmGroup,
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
        AlarmGroup alarmGroup = getAlarmGroup(alarmGroupId);
        List<Alarm> alarms = getUserAlarmGroup(userId, alarmGroupId);

        alarms.forEach(Alarm::softDelete);
        alarmGroup.softDelete();
    }

    /**
     * 알람 활성화/비활성화를 위한 메서드
     * @param userId
     * @param alarmGroupId
     * @param request
     * @return 활성화 여부
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
     * 알람 그룹을 조회하는 메서드
     * @param alarmGroupId 알람그룹 ID
     * @return 알람 그룹
     */
    private AlarmGroup getAlarmGroup(Long alarmGroupId) {
        return alarmGroupRepository.findByAlarmGroupIdAndDeletedAtIsNull(alarmGroupId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));
    }

    /**
     * 유저가 선택한 기술스택을 확인하는 메서드
     * @param userId
     * @param alarmGroupId
     * @return 유저의 기술 스택
     */
    private List<Alarm> getUserAlarmGroup(Long userId, Long alarmGroupId) {
        List<Alarm> alarms = alarmRepository
                .findByUserUserIdAndAlarmGroupAlarmGroupIdAndDeletedAtIsNull(userId, alarmGroupId);

        if (!alarms.isEmpty()) {
            return alarms;
        }

        boolean exists = alarmGroupRepository.existsByAlarmGroupIdAndDeletedAtIsNull(alarmGroupId);

        if (exists) {
            throw new CustomException(ErrorCode.ALARM_ACCESS_DENIED);
        }

        throw new CustomException(ErrorCode.ALARM_NOT_FOUND);
    }

    /**
     * 알람 요청 중복 데이터 제거 처리
     * @param user
     * @param dayOfWeeks
     * @param techStackIds
     * @param alarmTime
     */
    private List<Alarm> createAlarmRows(
            Users user,
            AlarmGroup alarmGroup,
            List<DayOfWeek> dayOfWeeks,
            List<Long> techStackIds,
            LocalTime alarmTime
    ) {
        List<Alarm> alarms = new ArrayList<>();

        List<DayOfWeek> distinctDayOfWeeks = dayOfWeeks.stream()
                .distinct()
                .toList();

        List<Long> distinctTechStackIds = techStackIds.stream()
                .distinct()
                .toList();

        for (DayOfWeek dayOfWeek : distinctDayOfWeeks) {
            for (Long techStackId : distinctTechStackIds) {
                TechStack techStack = entityManager.getReference(TechStack.class, techStackId);

                alarms.add(Alarm.builder()
                        .alarmGroup(alarmGroup)
                        .user(user)
                        .techStack(techStack)
                        .dayOfWeeks(dayOfWeek)
                        .alarmTime(alarmTime)
                        .selected(true)
                        .build());
            }
        }

        return alarms;
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
