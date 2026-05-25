package org.services;

import org.models.ServiceRequest;
import org.models.enums.ServiceStatus;
import org.repositories.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository repo;

    @Transactional
    public ServiceRequest create(ServiceRequest request) {
        return repo.save(request);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequest> findAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<ServiceRequest> findByUser(String email) {
        return repo.findByRequestedByOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public ServiceRequest findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }

    @Transactional
    public void updateStatus(Long id, ServiceStatus status, String notes) {
        ServiceRequest req = findById(id);
        req.setStatus(status);
        req.setNotes(notes);
        repo.save(req);
    }

    @Transactional(readOnly = true)
    public long countOpen() {
        return repo.countByStatus(ServiceStatus.ABIERTA);
    }
}
