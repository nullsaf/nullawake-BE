package com.nullsaf.nullawake.api.tech.entity;

import com.nullsaf.nullawake.api.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "USER_TECH_STACK",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_tech_stack_user_stack",
            columnNames = {"user_id", "tech_stack_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserTechStack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tech_stack_id")
    private Long userTechStackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "기술 스택을 선택한 사용자")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_stack_id", nullable = false)
    @Schema(description = "사용자가 선택한 기술 스택")
    private TechStack techStack;

    @Column(name = "selected", nullable = false)
    @Schema(description = "사용자의 해당 스택 선택 여부")
    private Boolean selected;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "생성 일시")
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    private UserTechStack(
        Users user,
        TechStack techStack,
        Boolean selected
    ) {
        this.user = user;
        this.techStack = techStack;
        this.selected = selected != null ? selected : true;
    }

    /**
     * 선택 여부 수정
     */
    public void updateSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * 선택 상태 여부 반환
     */
    public boolean isSelected() {
        return Boolean.TRUE.equals(this.selected);
    }

    /**
     * 선택 상태 변경
     * @param selected
     */
    public void updateSelected(boolean selected) {
        this.selected = selected;
    }
}