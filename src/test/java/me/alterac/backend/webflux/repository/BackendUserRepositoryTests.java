package me.alterac.backend.webflux.repository;

import lombok.AllArgsConstructor;
import me.alterac.backend.webflux.entity.BackendUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@SpringBootTest
public class BackendUserRepositoryTests {

    @Autowired
    private BackendUserRepository repository;

    @Test
    public void addAdmin() {
        repository.save(createAdmin());
    }

    static BackendUser createAdmin() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return BackendUser.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                // .roles(new ArrayList<>())
                .roles(Collections.singletonList("ADMIN"))
                .build();
    }
}
