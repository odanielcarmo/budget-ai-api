package com.dio.budget;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regras de negocio das transacoes.
 * MELHORIA DO DESAFIO: validacoes antes de salvar (valor positivo, descricao
 * obrigatoria, categoria padrao) e calculo de saldo.
 */
@Service
public class TransactionService {

    private static final String DEFAULT_CATEGORY = "geral";

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction create(TransactionType type, BigDecimal amount, String description, String category) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo da transacao e obrigatorio (INCOME ou EXPENSE)");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descricao e obrigatoria");
        }
        String finalCategory = (category == null || category.isBlank())
                ? DEFAULT_CATEGORY
                : category.trim().toLowerCase();

        return repository.save(new Transaction(type, amount, description.trim(), finalCategory));
    }

    public List<Transaction> lastTransactions() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }

    public BigDecimal balance() {
        BigDecimal income = sum(repository.findByType(TransactionType.INCOME));
        BigDecimal expense = sum(repository.findByType(TransactionType.EXPENSE));
        return income.subtract(expense);
    }

    private BigDecimal sum(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
