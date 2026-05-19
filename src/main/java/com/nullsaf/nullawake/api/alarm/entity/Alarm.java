package com.nullsaf.nullawake.api.alarm.entity;

import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "USER_ALARM_SETTING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id", nullable = false)
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_group_id", nullable = false)
    private AlarmGroup alarmGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id", nullable = false)
    private TechStack techStack;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    @Schema(description = "알람 요일", example = "MONDAY")
    private DayOfWeek dayOfWeeks;

    @Column(name = "alarm_time", nullable = false)
    @Schema(description = "알람 시간", example = "08:00:00")
    private LocalTime alarmTime;

    @Column(name = "selected")
    @Schema(description = "알람 활성화 여부", example = "true")
    private Boolean selected;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Schema(description = "알람 생성 시간")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "알람 수정 시간")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @Schema(description = "알람 삭제 시간")
    private LocalDateTime deletedAt;

    public Long getAlarmGroupId() {
        return alarmGroup.getAlarmGroupId();
    }

    /**
     * 알람 활성화 여부 변경
     */
    public void updateSelected(Boolean selected) {
        this.selected = selected;
    }
    /**
     * 알람 삭제 처리
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
