package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.models.enums.ServiceStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
@Getter
@Setter
@NoArgsConstructor
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceStatus status = ServiceStatus.ABIERTA;

    @Column(nullable = false, length = 150)
    private String requestedBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    @Column(length = 500)
    private String notes;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == ServiceStatus.CERRADA && this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
    }
}
