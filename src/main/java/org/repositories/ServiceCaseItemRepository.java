package org.repositories;

import org.models.ServiceCaseItem;
import org.models.enums.CaseItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCaseItemRepository extends JpaRepository<ServiceCaseItem, Long> {
    List<ServiceCaseItem> findByStatus(CaseItemStatus status);
    long countByStatus(CaseItemStatus status);
}
