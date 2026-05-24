package org.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.models.User;
import org.models.enums.Rol;
import org.repositories.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) return;

        User admin = new User();
        admin.setName("Administrador");
        admin.setEmail("admin@bqrines.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol(Rol.GERENTE);
        admin.setActive(true);
        userRepository.save(admin);

        log.warn("=======================================================");
        log.warn("  Usuario inicial creado:");
        log.warn("  Email:      admin@bqrines.com");
        log.warn("  Contraseña: admin123");
        log.warn("  Cambia la contraseña después del primer acceso.");
        log.warn("=======================================================");
    }
}
