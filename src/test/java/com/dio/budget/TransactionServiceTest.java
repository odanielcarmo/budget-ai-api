package com.dio.budget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private TransactionRepository repository;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepository.class);
        service = new TransactionService(repository);
    }

    @Test
    void shouldRejectZeroOrNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(TransactionType.EXPENSE, BigDecimal.ZERO, "cafe", "alimentacao"));
        assertThrows(IllegalArgumentException.class,
                () -> service.create(TransactionType.EXPENSE, new BigDecimal("-5"), "cafe", "alimentacao"));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(TransactionType.INCOME, BigDecimal.TEN, "  ", "salario"));
    }

    @Test
    void shouldUseDefaultCategoryWhenMissing() {
        when(repository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction saved = service.create(TransactionType.EXPENSE, BigDecimal.TEN, "padaria", null);

        assertEquals("geral", saved.getCategory());
    }

    @Test
    void shouldCalculateBalance() {
        when(repository.findByType(TransactionType.INCOME))
                .thenReturn(List.of(new Transaction(TransactionType.INCOME, new BigDecimal("1000"), "salario", "renda")));
        when(repository.findByType(TransactionType.EXPENSE))
                .thenReturn(List.of(
                        new Transaction(TransactionType.EXPENSE, new BigDecimal("200"), "mercado", "alimentacao"),
                        new Transaction(TransactionType.EXPENSE, new BigDecimal("50"), "onibus", "transporte")));

        assertEquals(new BigDecimal("750"), service.balance());
    }
}
