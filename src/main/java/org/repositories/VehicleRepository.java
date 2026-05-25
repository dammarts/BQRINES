package org.repositories;

import org.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Returns only active vehicles for catalog display
    List<Vehicle> findByActiveTrue();

    // Used by NotificationService to detect low stock
    List<Vehicle> findByActiveTrueAndStockLessThanEqual(int umbral);

    java.util.Optional<Vehicle> findByPlaca(String placa);
}
