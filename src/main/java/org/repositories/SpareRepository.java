package org.repositories;

import org.models.Spare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpareRepository extends JpaRepository<Spare, Long> {

    // Returns only active spare parts for catalog display
    List<Spare> findByActiveTrue();

    // Filter by category for inventory views
    List<Spare> findByCategory(String category);

    // Used by NotificationService to detect low stock (includes inactive/sold-out spares)
    List<Spare> findByStockLessThanEqual(int umbral);

    // Used to detect duplicate spare by reference before creating
    java.util.Optional<Spare> findByReference(String reference);
}
