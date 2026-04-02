package com.lucca.finance_manager_api.dto.dashboard;

import java.math.BigDecimal;

public record DashboardResponseDTO (
        BigDecimal totalBalance,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpense
) {
}
