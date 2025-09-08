
---

# Microservices Communication

Repositório para estudos de comunicação entre microserviços, combinando abordagens **síncronas** e **assíncronas**, com uso de **RabbitMQ** como mensageria.

---

## Tecnologias Utilizadas

* **Java 21** com **Spring Boot 3**
* **Node.js** com **Express.js**
* **APIs REST** (HTTP + JWT)
* **Bancos de Dados**: PostgreSQL (container) e MongoDB (container ou Cloud)
* **RabbitMQ** (container ou CloudAMQP)
* **Docker** & **docker-compose**
* **Axios** para chamadas HTTP

---

## Arquitetura & Componentes

O sistema simula um ambiente de microserviços composto por:

* **auth-api**: responsável pela autenticação e emissão de tokens JWT.
* **product-api**: fornece endpoints para operações relacionadas a produtos.
* **sales-api**: gerencia vendas e pode processar eventos recebidos via RabbitMQ.
* **RabbitMQ** como broker de mensagens, possibilitando comunicação **assincrona** entre serviços.
* Todos os serviços são orquestrados via **docker-compose**.

---

## Funcionalidades Principais

* **Comunicação síncrona**: APIs REST com autenticação via JWT e chamadas HTTP usando Axios.
* **Comunicação assíncrona**: troca de mensagens entre microserviços usando RabbitMQ.
* Estrutura modularizada, facilitando testes isolados e escalabilidade.
* Infraestrutura containerizada com Docker para replicabilidade e desenvolvimento local. 

---

## Requisitos

* Docker & Docker-Compose instalados
* JDK 21
* Node.js (versão compatível)
* Acesso à internet para baixar imagens do Docker e dependências

---


## Estrutura do Projeto

```
/
├── auth-api/
├── product-api/
├── sales-api/
├── docker-compose.yaml
├── README.md
```

* **auth-api**: serviço de autenticação
* **product-api**: gestão de produtos
* **sales-api**: lógica de vendas e integração via RabbitMQ

---

