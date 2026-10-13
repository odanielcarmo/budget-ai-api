package com.dio.budget;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Orquestra o fluxo: texto do comando -> IA entende a intencao ->
 * IA chama a tool certa -> resposta final para a pessoa usuaria.
 */
@Service
public class AssistantService {

    private static final String SYSTEM_PROMPT = """
            Voce e um assistente de orcamento pessoal. Responda sempre em portugues, de forma curta.
            Use as ferramentas disponiveis para registrar transacoes, consultar o saldo
            ou listar transacoes. Nunca invente valores: se faltar informacao, pergunte.
            """;

    private final ChatClient chatClient;

    public AssistantService(ChatClient.Builder builder, BudgetTools tools) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(tools)
                .build();
    }

    public String handle(String command) {
        return chatClient.prompt()
                .user(command)
                .call()
                .content();
    }
}
