package org.repositories;

import org.models.ServiceRequest;
import org.models.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByRequestedByOrderByCreatedAtDesc(String email);

    List<ServiceRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(ServiceStatus status);
}
