package org.models;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spares")
@Getter
@Setter
@NoArgsConstructor
public class Spare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String reference;
    
    @Column(nullable = false, length = 100)
    private String category;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    @PreUpdate
    private void verifyStock() {
        if (this.stock != null && this.stock <= 0) {
            this.stock = 0;
            this.active = false;
        }
    }
}
