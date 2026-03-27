package com.lucca.finance_manager_api.dto.transaction;

import java.util.List;

public record PaginatedTransactionResponseDTO<T> (
        List<T> data,
        int page,
        int limit,
        Long total
){
}
