package com.lucca.finance_manager_api.mapper;

import com.lucca.finance_manager_api.dto.transaction.TransactionRequestDTO;
import com.lucca.finance_manager_api.dto.transaction.TransactionResponseDTO;
import com.lucca.finance_manager_api.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toResponse (Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getType(),
                transaction.getAmount()
        );
    }

    public Transaction toEntity (TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setType(dto.type());
        transaction.setAmount(dto.amount());
        return transaction;
    }
}
