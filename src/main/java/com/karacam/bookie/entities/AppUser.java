package com.karacam.bookie.entities;

import com.karacam.bookie.core.BaseEntity;
import com.karacam.bookie.core.Tables;
import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import com.karacam.bookie.core.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = Tables.USERS_TABLE)
public class AppUser extends BaseEntity {
    @Column(updatable = false, unique = true, nullable = false)
    @ValidField(category = PatternCategory.ENTITY, patternKey = "bookie_email")
    private String email;

    @Column(nullable = false, length = 60)
    @ValidField(category = PatternCategory.ENTITY, patternKey = "entity_password")
    private String password;

    @Column(nullable = false, updatable = false, length = 50)
    @ValidField(category = PatternCategory.ENTITY, patternKey = "alpha_numeric")
    private String firstName;

    @Column(nullable = false, length = 50)
    @ValidField(category = PatternCategory.ENTITY, patternKey = "alpha_numeric")
    private String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role authority;

    @Column(nullable = false)
    private boolean isLocked;

    @Column(nullable = false)
    private boolean isPasswordExpired;
}
