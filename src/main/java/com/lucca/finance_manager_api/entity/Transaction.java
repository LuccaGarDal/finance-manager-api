package com.lucca.finance_manager_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column (nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private LocalDate transactionDate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDate updatedAt;

    @Column
    private Boolean applied;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
