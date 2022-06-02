package me.alterac.backend.webflux.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.alterac.backend.webflux.entity.BackendUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Slf4j
@SpringBootTest
public class BackendUserRepositoryTests {

    @Autowired
    private BackendUserRepository repository;

    @Test
    public void addAdmin() {
        repository.save(createAdmin()).block();
    }

    @Test
    public void getAdmin() {
        BackendUser user = repository.findByUsername("admin").block();
        log.info("admin: {}", user);
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
