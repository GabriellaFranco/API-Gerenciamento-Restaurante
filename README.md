API criada para o gerenciamento de estoque de restaurantes/lanchonetes com foco em operações de entrada e saída de produtos, gestão de inventário, rastreamento de movimentações e controle de usuários com permissões diferenciadas baseadas em roles.

Tecnologias utilizadas:
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- JUnit 5 + Mockito
- Jacoco (para cobertura de testes)
- Swagger/OpenAPI

Exemplo de requisição para registro de uma nova transição de estoque:

POST /inventory-transactions
Content-Type: application/json
{
  "product": {
    "id": 1,
    "name": "Tomato",
    "measurementUnit": "KILOGRAM"
  },
  "responsible": {
    "id": 1,
    "name": "João da Silva"
  },
  "transactionType": "INBOUND",
  "quantity": 10,
  "unitPrice": 9.99,
  "motivation": "REPLENISHMENT",
  "details": "Compra semanal de tomates"
}
