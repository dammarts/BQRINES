package org.services;

import org.models.ServiceCase;
import org.models.ServiceCaseItem;
import org.models.enums.CaseItemStatus;
import org.models.enums.CaseStatus;
import org.repositories.ServiceCaseItemRepository;
import org.repositories.ServiceCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceCaseService {

    private final ServiceCaseRepository caseRepository;
    private final ServiceCaseItemRepository itemRepository;

    public ServiceCaseService(ServiceCaseRepository caseRepository,
                              ServiceCaseItemRepository itemRepository) {
        this.caseRepository = caseRepository;
        this.itemRepository = itemRepository;
    }

    public List<ServiceCase> findAll() {
        return caseRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ServiceCase> findByTechnician(String email) {
        return caseRepository.findByCreatedByOrderByCreatedAtDesc(email);
    }

    public Optional<ServiceCase> findById(Long id) {
        return caseRepository.findById(id);
    }

    public long countByStatus(CaseStatus status) {
        return caseRepository.countByStatus(status);
    }

    public long countPendingItems() {
        return itemRepository.countByStatus(CaseItemStatus.SOLICITADO);
    }

    @Transactional
    public ServiceCase create(ServiceCase serviceCase) {
        return caseRepository.save(serviceCase);
    }

    @Transactional
    public ServiceCase addItem(Long caseId, ServiceCaseItem item) {
        ServiceCase sc = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado: " + caseId));
        item.setServiceCase(sc);
        sc.getItems().add(item);
        return caseRepository.save(sc);
    }

    @Transactional
    public ServiceCaseItem updateItemStatus(Long itemId, CaseItemStatus status, String notes) {
        ServiceCaseItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado: " + itemId));
        item.setStatus(status);
        if (notes != null && !notes.isBlank()) item.setNotes(notes);
        return itemRepository.save(item);
    }

    @Transactional
    public ServiceCase updateStatus(Long caseId, CaseStatus status, String asorNotes) {
        ServiceCase sc = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado: " + caseId));
        sc.setStatus(status);
        if (asorNotes != null && !asorNotes.isBlank()) sc.setAsorNotes(asorNotes);
        if (status == CaseStatus.ENTREGADO) sc.setClosedAt(LocalDateTime.now());
        return caseRepository.save(sc);
    }

    @Transactional
    public ServiceCase close(Long caseId, Long sellId, String asorNotes) {
        ServiceCase sc = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado: " + caseId));
        sc.setStatus(CaseStatus.ENTREGADO);
        sc.setSellId(sellId);
        sc.setClosedAt(LocalDateTime.now());
        if (asorNotes != null && !asorNotes.isBlank()) sc.setAsorNotes(asorNotes);
        sc.getItems().stream()
                .filter(i -> i.getStatus() == CaseItemStatus.DISPONIBLE)
                .forEach(i -> i.setStatus(CaseItemStatus.ENTREGADO));
        return caseRepository.save(sc);
    }

    @Transactional
    public void updateTechnicianNotes(Long caseId, String notes) {
        ServiceCase sc = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado: " + caseId));
        sc.setTechnicianNotes(notes);
        caseRepository.save(sc);
    }
}
