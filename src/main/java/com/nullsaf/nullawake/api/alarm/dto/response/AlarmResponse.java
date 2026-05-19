package com.nullsaf.nullawake.api.alarm.dto.response;

import java.time.DayOfWeek;
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
            List<Long> techStackIds,
            List<String> categoryNames,
            List<String> techStackNames,
            List<DayOfWeek> dayOfWeek,
            LocalTime alarmTime,
            Boolean selected
    ) {
    }

    public record DetailResponse(
            Long alarmGroupId,
            List<StackItem> stackList,
            List<DayOfWeek> dayOfWeeks,
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
