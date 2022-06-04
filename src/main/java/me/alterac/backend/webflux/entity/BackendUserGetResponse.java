package me.alterac.backend.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackendUserGetResponse {
    private Long id;
    private String username;
    private String description;
    private List<String> roles;
}
