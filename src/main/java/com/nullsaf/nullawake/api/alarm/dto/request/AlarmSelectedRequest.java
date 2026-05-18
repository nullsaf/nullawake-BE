package com.nullsaf.nullawake.api.alarm.dto.request;

import jakarta.validation.constraints.NotNull;

public record AlarmSelectedRequest(
        @NotNull Boolean selected
) {
}
