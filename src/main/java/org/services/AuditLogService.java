package org.services;

import org.models.AuditLog;
import org.models.enums.AuditAction;
import org.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String entityType, Long entityId, String entityDescription,
                    AuditAction action, String details, String performedBy) {
        AuditLog entry = new AuditLog();
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setEntityDescription(entityDescription);
        entry.setAction(action);
        entry.setDetails(details);
        entry.setPerformedBy(performedBy);
        repo.save(entry);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> findByType(String entityType) {
        return repo.findByEntityTypeOrderByPerformedAtDesc(entityType);
    }
}
