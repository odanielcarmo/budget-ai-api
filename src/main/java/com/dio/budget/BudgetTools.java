package com.dio.budget;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TOOL CALLING: funcoes reais da aplicacao que a IA pode chamar
 * para executar o comando de voz do usuario.
 */
@Component
public class BudgetTools {

    private final TransactionService service;

    public BudgetTools(TransactionService service) {
        this.service = service;
    }

    @Tool(description = "Registra uma nova transacao financeira (receita ou despesa)")
    public String registerTransaction(
            @ToolParam(description = "Tipo: INCOME para receita ou EXPENSE para despesa") String type,
            @ToolParam(description = "Valor positivo da transacao") double amount,
            @ToolParam(description = "Descricao curta da transacao") String description,
            @ToolParam(description = "Categoria, por exemplo alimentacao ou transporte", required = false) String category) {
        try {
            TransactionType transactionType = TransactionType.valueOf(type.trim().toUpperCase());
            Transaction saved = service.create(transactionType, BigDecimal.valueOf(amount), description, category);
            return "Transacao registrada com id " + saved.getId();
        } catch (IllegalArgumentException e) {
            return "Erro ao registrar: " + e.getMessage();
        }
    }

    @Tool(description = "Consulta o saldo atual (receitas menos despesas)")
    public String getBalance() {
        return "Saldo atual: " + service.balance();
    }

    @Tool(description = "Lista as ultimas transacoes registradas")
    public String listTransactions() {
        List<Transaction> last = service.lastTransactions();
        if (last.isEmpty()) {
            return "Nenhuma transacao registrada";
        }
        return last.stream()
                .map(t -> t.getType() + " " + t.getAmount() + " - " + t.getDescription() + " (" + t.getCategory() + ")")
                .collect(Collectors.joining("\n"));
    }
}
