package com.nullsaf.nullawake.api.user.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String nickname;

    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static Users create(String nickname, String email, String profileImage) {
        return Users.builder()
                .nickname(nickname)
                .email(email)
                .profileImage(profileImage)
                .build();
    }

    public void updateProfile(String nickname, String email) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }

        if (email != null && !email.isBlank()) {
            this.email = email;
        }
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
