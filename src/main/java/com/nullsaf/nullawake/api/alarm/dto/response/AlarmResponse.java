package com.nullsaf.nullawake.api.alarm.dto.response;

import java.time.LocalTime;
import java.util.List;

public record AlarmResponse() {

    public record ListResponse(
            List<AlarmItem> alarmsList
    ) {
    }

    public record AlarmItem(
            Long alarmGroupId,
            List<Long> alarmIds,
            Long techStackId,
            String categoryName,
            String techStackName,
            List<String> dayOfWeek,
            LocalTime alarmTime,
            Boolean selected
    ) {
    }

    public record DetailResponse(
            Long alarmGroupId,
            List<StackItem> stackList,
            List<String> dayOfWeeks,
            LocalTime alarmTime,
            Boolean selected
    ) {
    }

    public record StackItem(
            Long techStackId,
            String techCategoryName,
            String techStackName
    ) {
    }

    public record CreateResponse(
            Long groupId
    ) {
    }

    public record SelectedResponse(
            Long alarmGroupId,
            Boolean selected
    ) {
    }
}
