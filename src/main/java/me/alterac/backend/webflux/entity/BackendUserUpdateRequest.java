package me.alterac.backend.webflux.entity;

import lombok.Data;

@Data
public class BackendUserUpdateRequest {
    private String username;
    private String description;
}
