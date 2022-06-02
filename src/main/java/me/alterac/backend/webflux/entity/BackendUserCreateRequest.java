package me.alterac.backend.webflux.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BackendUserCreateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String description;
}
