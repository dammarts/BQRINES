package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.models.enums.CaseItemStatus;

@Entity
@Table(name = "service_case_items")
@Getter
@Setter
@NoArgsConstructor
public class ServiceCaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_case_id", nullable = false)
    private ServiceCase serviceCase;

    private Long spareId;

    @Column(length = 200)
    private String spareName;

    @Column(length = 100)
    private String spareReference;

    private Double unitaryPrice;

    @Column(nullable = false)
    private Integer requestedQuantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CaseItemStatus status = CaseItemStatus.SOLICITADO;

    @Column(length = 500)
    private String notes;
}
