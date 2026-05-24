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
        // Fix stale records: stock=0 but still marked active (data from before the auto-deactivation fix)
        spareRepository.findAll().stream()
                .filter(s -> s.getStock() <= 0 && s.isActive())
                .forEach(s -> { s.setStock(0); s.setActive(false); spareRepository.save(s); });

        vehicleRepository.findAll().stream()
                .filter(v -> v.getStock() <= 0 && v.isActive())
                .forEach(v -> { v.setStock(0); v.setActive(false); vehicleRepository.save(v); });

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
