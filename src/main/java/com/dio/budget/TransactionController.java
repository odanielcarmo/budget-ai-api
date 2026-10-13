package com.dio.budget;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Exposicao REST das transacoes. */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@Valid @RequestBody CreateTransactionRequest request) {
        return service.create(request.type(), request.amount(), request.description(), request.category());
    }

    @GetMapping
    public List<Transaction> list() {
        return service.lastTransactions();
    }

    @GetMapping("/balance")
    public Map<String, Object> balance() {
        return Map.of("balance", service.balance());
    }
}
