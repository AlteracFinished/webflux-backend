package me.alterac.backend.webflux.service;

import me.alterac.backend.webflux.entity.BackendUser;
import me.alterac.backend.webflux.repository.BackendUserRepository;
import me.alterac.backend.webflux.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

//@RunWith(MockitoJUnitRunner.class)
public class BackendUserServiceTests {

    @Test
    public void queryAdminAccount() {
        String username = "admin";

        BackendUserRepository repository = mock(BackendUserRepository.class);
        when(repository.findByUsername(anyString())).thenReturn(Mono.just(createAdmin()));

        BackendUserServiceImpl service = new BackendUserServiceImpl(repository, new BCryptPasswordEncoder());
        UserDetails result = service.findByUsername(username).block();
        assertNotNull("User", result);
        assertTrue("Enabled", result.isEnabled());
    }

    static BackendUser createAdmin() {
        return BackendUser.builder()
                .id(1L)
                .username("admin")
                .password("5e5dd742ebb57e333ace01cf27308acc")
                // .roles(new ArrayList<>())
                .roles(Collections.singletonList(Roles.ADMIN))
                .build();
    }
}
