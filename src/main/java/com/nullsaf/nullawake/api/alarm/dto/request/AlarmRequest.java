package com.nullsaf.nullawake.api.alarm.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

public record AlarmRequest(
        @NotNull LocalTime alarmTime,
        @NotEmpty List<String> dayOfWeeks,
        @NotEmpty List<Long> techStackIds
) {
}
