package org.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_vehicles")
@Getter
@Setter
@NoArgsConstructor
public class ServiceVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String placa;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 50)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "serviceVehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceCase> cases = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.placa != null) this.placa = this.placa.toUpperCase().trim();
    }
}
