package com.nullsaf.nullawake.api.alarm.entity;

import com.nullsaf.nullawake.api.tech.entity.TechStack;
import com.nullsaf.nullawake.api.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Schema(description = "알람 ID", example = "1")
    private Long alarmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(hidden = true)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id", nullable = false)
    @Schema(hidden = true)
    private TechStack techStack;

    @Column(name = "alarm_group_id")
    @Schema(description = "알람 그룹 ID", example = "1")
    private Long alarmGroupId;

    @Column(name = "day_of_week", nullable = false)
    @Schema(description = "알람 요일", example = "MONDAY")
    private String dayOfWeek;

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

    /**
     * 알람 활성화 여부 변경
     */
    public void updateSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * 알람 시간 변경
     */
    public void updateAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * 알람 삭제 처리
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
