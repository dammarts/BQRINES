package org.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sells")
@Getter
@Setter
@NoArgsConstructor
public class Sell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 150)
    private String clientName;

    @Column(length = 50)
    private String buyerDocument;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 150, updatable = false)
    private String registeredBy;

    @OneToMany(mappedBy = "sell", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SellItem> items = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        this.date = LocalDateTime.now();
    }
}
