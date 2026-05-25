package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.models.enums.CaseStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_cases")
@Getter
@Setter
@NoArgsConstructor
public class ServiceCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_vehicle_id", nullable = false)
    private ServiceVehicle serviceVehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status = CaseStatus.RECIBIDO;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 150)
    private String createdBy;

    @Column(length = 150)
    private String assignedTo;

    @Column(length = 1000)
    private String technicianNotes;

    @Column(length = 1000)
    private String asorNotes;

    private Double laborPrice;

    @Column(length = 300)
    private String laborDescription;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    private Long sellId;

    @OneToMany(mappedBy = "serviceCase", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ServiceCaseItem> items = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
