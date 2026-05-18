package com.nullsaf.nullawake.api.alarm.mapper;

import com.nullsaf.nullawake.api.alarm.dto.response.AlarmResponse;
import com.nullsaf.nullawake.api.alarm.entity.Alarm;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AlarmMapper {

    public AlarmResponse.ListResponse toListResponse(List<Alarm> alarms) {

        Map<Long, List<Alarm>> groupedAlarms = alarms.stream()
                .collect(Collectors.groupingBy(Alarm::getAlarmGroupId));

        List<AlarmResponse.AlarmItem> alarmItems =
                groupedAlarms.entrySet().stream()
                        .map(entry -> {

                            Long alarmGroupId = entry.getKey();
                            List<Alarm> groupAlarms = entry.getValue();

                            Alarm first = groupAlarms.get(0);

                            List<Long> alarmIds = groupAlarms.stream()
                                    .map(Alarm::getAlarmId)
                                    .toList();

                            List<String> dayOfWeeks = groupAlarms.stream()
                                    .map(Alarm::getDayOfWeeks)
                                    .distinct()
                                    .sorted()
                                    .toList();

                            return new AlarmResponse.AlarmItem(
                                    alarmGroupId,
                                    alarmIds,
                                    first.getTechStack().getTechStackId(),
                                    first.getTechStack()
                                            .getTechCategory()
                                            .getTechCategoryName(),
                                    first.getTechStack().getTechStackName(),
                                    dayOfWeeks,
                                    first.getAlarmTime(),
                                    first.getSelected()
                            );
                        })
                        .sorted(Comparator.comparing(
                                AlarmResponse.AlarmItem::alarmGroupId
                        ))
                        .toList();

        return new AlarmResponse.ListResponse(alarmItems);
    }

    public AlarmResponse.DetailResponse toDetailResponse(
            Long alarmGroupId,
            List<Alarm> alarms
    ) {
        Alarm first = alarms.get(0);

        List<String> dayOfWeeks = alarms.stream()
                .map(Alarm::getDayOfWeeks)
                .distinct()
                .sorted()
                .toList();

        List<AlarmResponse.StackItem> stackList = alarms.stream()
                .map(alarm -> new AlarmResponse.StackItem(
                        alarm.getTechStack().getTechStackId(),
                        alarm.getTechStack().getTechCategory().getTechCategoryName(),
                        alarm.getTechStack().getTechStackName()
                ))
                .distinct()
                .toList();

        return new AlarmResponse.DetailResponse(
                alarmGroupId,
                stackList,
                dayOfWeeks,
                first.getAlarmTime(),
                first.getSelected()
        );
    }
}
