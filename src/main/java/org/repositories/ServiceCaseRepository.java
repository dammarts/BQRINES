package org.repositories;

import org.models.ServiceCase;
import org.models.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCaseRepository extends JpaRepository<ServiceCase, Long> {
    List<ServiceCase> findByCreatedByOrderByCreatedAtDesc(String createdBy);
    List<ServiceCase> findAllByOrderByCreatedAtDesc();
    long countByStatus(CaseStatus status);
}
