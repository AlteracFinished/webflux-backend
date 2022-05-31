package me.alterac.backend.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class BackendUser {
    @Id
    private Long id;
    private String username;
    private String salt;
    private String password;
    @Column("login_at")
    private Instant loginAt;

    private String roles;
}
