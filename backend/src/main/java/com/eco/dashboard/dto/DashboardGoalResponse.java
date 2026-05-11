package com.eco.dashboard.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DashboardGoalResponse {

    private final UUID id;
    private final String name;
    private final BigDecimal progressPercent;

    public DashboardGoalResponse(UUID id, String name, BigDecimal progressPercent) {
        this.id = id;
        this.name = name;
        this.progressPercent = progressPercent;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getProgressPercent() {
        return progressPercent;
    }
}

