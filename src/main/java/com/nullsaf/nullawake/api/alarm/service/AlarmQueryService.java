package com.nullsaf.nullawake.api.alarm.service;

import com.nullsaf.nullawake.api.alarm.dto.response.AlarmCategoryStackResponse;
import com.nullsaf.nullawake.api.alarm.dto.response.AlarmResponse;
import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import com.nullsaf.nullawake.api.alarm.Mapper.AlarmMapper;
import com.nullsaf.nullawake.api.alarm.repository.AlarmRepository;
import com.nullsaf.nullawake.api.tech.dto.TechCategoryResponse.TechCategoryDto;
import com.nullsaf.nullawake.api.tech.dto.TechStackListResponse;
import com.nullsaf.nullawake.api.tech.repository.TechCategoryRepository;
import com.nullsaf.nullawake.common.exception.CustomException;
import com.nullsaf.nullawake.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmQueryService {

    private final AlarmRepository alarmRepository;
    private final TechCategoryRepository techCategoryRepository;
    private final AlarmMapper alarmMapper;

    public AlarmResponse.ListResponse getAlarms(Long userId) {

        List<Alarm> alarms =
                alarmRepository.findByUserUserIdAndDeletedAtIsNull(userId);

        return alarmMapper.toListResponse(alarms);
    }

    public AlarmResponse.DetailResponse getAlarmDetail(Long userId, Long alarmGroupId) {
        List<Alarm> alarms = alarmRepository
                .findByUserUserIdAndAlarmGroupIdAndDeletedAtIsNull(userId, alarmGroupId);

        if (alarms.isEmpty()) {
            throw new CustomException(ErrorCode.ALARM_NOT_FOUND);
        }

        return alarmMapper.toDetailResponse(alarmGroupId, alarms);
    }

    public AlarmCategoryStackResponse getCategoryStacks(Long userId) {
        List<TechCategoryDto> activeCategories =
                techCategoryRepository.findActiveCategoriesWithStackCount();

        List<AlarmCategoryStackResponse.CategoryResponse> categoryList = activeCategories.stream()
                .map(category -> toAlarmCategoryResponse(category, userId))
                .filter(category -> category.selectedStackCount() > 0)
                .toList();

        if (categoryList.isEmpty()) {
            throw new CustomException(ErrorCode.ALARM_CATEGORY_STACK_NOT_FOUND);
        }

        return new AlarmCategoryStackResponse(categoryList);
    }

    private AlarmCategoryStackResponse.CategoryResponse toAlarmCategoryResponse(
            TechCategoryDto category,
            Long userId
    ) {
        TechStackListResponse techStackListResponse =
                techCategoryRepository
                        .findTechStacksByCategoryIdAndUserId(category.getTechCategoryId(), userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.ALARM_CATEGORY_STACK_NOT_FOUND));

        List<AlarmCategoryStackResponse.StackResponse> selectedStackList =
                techStackListResponse.techStackList().stream()
                        .filter(TechStackListResponse.TechStack::selected)
                        .map(stack -> new AlarmCategoryStackResponse.StackResponse(
                                stack.techStackId(),
                                stack.techStackName()
                        ))
                        .toList();

        return new AlarmCategoryStackResponse.CategoryResponse(
                techStackListResponse.techCategoryId(),
                techStackListResponse.techCategoryName(),
                selectedStackList.size(),
                selectedStackList
        );
    }
}
