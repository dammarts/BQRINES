package org.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Drops NOT NULL constraints from legacy sells columns that were removed
 * when Sell was refactored to a multi-item header entity.
 * Safe to run repeatedly — each ALTER is wrapped in its own try/catch.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class LegacySchemaMigration {

    private final JdbcTemplate jdbc;

    @PostConstruct
    public void migrate() {
        String[] legacyCols = {"product_id", "product_name", "product_type", "quantity", "unitary_price"};
        for (String col : legacyCols) {
            try {
                jdbc.execute("ALTER TABLE sells ALTER COLUMN " + col + " DROP NOT NULL");
                log.info("Migration: dropped NOT NULL on sells.{}", col);
            } catch (Exception ignored) {
                // Column doesn't exist or is already nullable — nothing to do
            }
        }
    }
}
