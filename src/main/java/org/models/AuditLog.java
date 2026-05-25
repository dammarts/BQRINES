package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.models.enums.AuditAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "VEHICULO" or "REPUESTO"
    @Column(nullable = false, length = 20)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    // Human-readable name: "Toyota Corolla 2020 (ABC123)" / "Pastillas (REF-001)"
    @Column(nullable = false, length = 300)
    private String entityDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AuditAction action;

    // What changed: "Stock: 5 → 2 | Precio: $8.000 → $7.500"
    @Column(length = 1000)
    private String details;

    @Column(nullable = false, length = 150)
    private String performedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    private void onPersist() {
        this.performedAt = LocalDateTime.now();
    }
}
