package org.repositories;

import org.models.ServiceVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceVehicleRepository extends JpaRepository<ServiceVehicle, Long> {
    Optional<ServiceVehicle> findByPlacaIgnoreCase(String placa);
    boolean existsByPlacaIgnoreCase(String placa);
}
