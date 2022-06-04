package me.alterac.backend.webflux.entity;

import lombok.Data;

@Data
public class BackendUserPasswordUpdateRequest {
    private String username;
    private String password;
}
