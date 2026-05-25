package org.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.models.Spare;
import org.models.User;
import org.models.Vehicle;
import org.models.enums.Rol;
import org.repositories.SpareRepository;
import org.repositories.UserRepository;
import org.repositories.VehicleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final SpareRepository spareRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Sync active flag with actual stock on every startup
        spareRepository.findAll().forEach(s -> {
            boolean shouldBeActive = s.getStock() != null && s.getStock() > 0;
            if (s.isActive() != shouldBeActive) {
                s.setActive(shouldBeActive);
                if (!shouldBeActive) s.setStock(0);
                spareRepository.save(s);
            }
        });

        vehicleRepository.findAll().forEach(v -> {
            boolean shouldBeActive = v.getStock() != null && v.getStock() > 0;
            if (v.isActive() != shouldBeActive) {
                v.setActive(shouldBeActive);
                if (!shouldBeActive) v.setStock(0);
                vehicleRepository.save(v);
            }
        });

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
