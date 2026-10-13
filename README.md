# Budget AI API

API de orçamento inteligente feita com **Spring Boot** e **Spring AI**, para o desafio
"Desenvolvendo sua API Inteligente com Reconhecimento de Fala e Spring Boot" (DIO).

## O que o projeto faz

Recebe um comando de voz (arquivo de áudio) ou de texto, entende a intenção com IA e
executa uma função real da aplicação (registrar transação, consultar saldo ou listar transações),
devolvendo uma resposta final para a pessoa usuária.

Fluxo principal:

1. Recebe o arquivo de áudio (`POST /api/assistant/audio`)
2. **Transcription API** transforma o áudio em texto
3. **ChatClient** com IA entende a intenção do comando
4. **Tool Calling** executa a função real (`BudgetTools`)
5. A transação é salva no banco via JPA e a IA gera a resposta final

## Tecnologias

- Java 21
- Spring Boot 3.4
- Spring AI 1.0 (ChatClient, Tool Calling, Transcription API) com OpenAI
- Spring Data JPA + banco H2 em memória
- Bean Validation
- JUnit 5 + Mockito

## Como executar

Pré-requisitos: Java 21, Maven e uma chave de API da OpenAI.

```bash
export OPENAI_API_KEY=sua-chave-aqui
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Melhoria implementada

**Validações antes de salvar uma transação** (`TransactionService`):

- valor deve ser maior que zero
- descrição é obrigatória
- categoria padrão (`geral`) quando não informada
- erros de validação voltam como `400 Bad Request` com mensagem clara (`ApiExceptionHandler`)
- erros de validação também são devolvidos para a IA, que explica o problema à pessoa usuária

Também foram adicionados testes unitários para essas regras e para o cálculo de saldo.

## Como testar o fluxo principal

Comando por texto:

```bash
curl -X POST http://localhost:8080/api/assistant/text \
  -H "Content-Type: application/json" \
  -d '{"command": "Registre uma despesa de 35 reais com almoço, categoria alimentação"}'
```

Comando por áudio (pode ser um .mp3 ou .m4a gravado dizendo o comando):

```bash
curl -X POST http://localhost:8080/api/assistant/audio -F "file=@comando.mp3"
```

Endpoints REST diretos:

```bash
# criar transação
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"type":"INCOME","amount":1500,"description":"salario","category":"renda"}'

# listar e consultar saldo
curl http://localhost:8080/api/transactions
curl http://localhost:8080/api/transactions/balance
```

Rodar os testes:

```bash
mvn test
```

## O que aprendi

- Como configurar o Spring AI e integrar um modelo de linguagem a uma aplicação Java.
- Como o Tool Calling permite que a IA execute funções reais, mantendo as regras de negócio no código.
- Como usar a Transcription API para transformar áudio em texto.
- A importância de separar responsabilidades (controller, service, tools) e validar os dados antes de persistir.
