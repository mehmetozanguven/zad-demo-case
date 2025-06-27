package com.mehmetozanguven.zad_demo_case.user.internal;

import com.mehmetozanguven.zad_demo_case.core.entity.ApiBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@With
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends ApiBaseEntity {
    @NotNull
    @Column(name = "email", unique = true)
    private String email;
}
