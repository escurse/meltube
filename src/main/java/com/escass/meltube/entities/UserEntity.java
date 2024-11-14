package com.escass.meltube.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"email"})
public class UserEntity {
    private String email;
    private String password;
    private String nickname;
    private String contact;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
    private boolean is_admin;
    private boolean is_suspended;
    private boolean is_verified;
}
