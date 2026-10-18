 <div align="center">

# LP2627_E3_C1 – Achados e Perdidos
### Componente 1: Dados e Persistência

Laboratório de Programação — 2026/2027

**1º Ciclo - Licenciatura em Engenharia Informática — Universidade Portucalense**

</div>

---

## Descrição e objetivo

O projeto **Achados e Perdidos** consiste no desenvolvimento de uma aplicação, em arquitetura cliente-servidor, para a gestão de itens perdidos e achados. O desenvolvimento segue uma metodologia ágil baseada em SCRUM e está dividido em 3 componentes, cada uma correspondente a um Sprint:

- **Componente 1 (Sprint 1) — Dados e Persistência**: modelação de dados e mapeamento objeto-relacional com Hibernate, numa aplicação Java/Maven.
- **Componente 2 (Sprint 2) — API REST**: desenvolvimento de uma API RESTful com Spring Boot, testada com Postman e documentada com OpenAPI.
- **Componente 3 (Sprint 3) — Cliente JavaFX**: aplicação cliente com interface gráfica em JavaFX, integrada com a API REST.

### Objetivo desta componente

Este repositório corresponde à **Componente 1**. O objetivo é desenvolver uma aplicação Java que persista os dados do domínio numa base de dados relacional (MySQL), utilizando Hibernate/JPA para o mapeamento objeto-relacional. Esta componente implementa as operações de criação, consulta, atualização e eliminação de dados, bem como consultas que permitem verificar que os dados e os relacionamentos entre entidades estão corretamente armazenados.
## Equipa 3

| Nome | Nº Aluno |
|---|---|
| Ana Clara Pereira Souto | 53986 |
| António Jorge Campos dos Santos | 47303 |
| Guilherme Tomé da Silva | 50755 |
| João Gonçalo Guedes Pinto | 53255 |
| Mathias Fernando Concha Pando | 53035 |

## Tecnologias e dependências C1


### Tecnologias
- **Java** (versão 21) — linguagem de desenvolvimento
- **Maven** (versão 3.9.9, integrado no Eclipse) — gestão de dependências e configuração do projeto
- **MySQL** (versão 8.0.46 ) — base de dados relacional
  
### Principais dependências (`pom.xml`)
- **Hibernate ORM** (`hibernate-core`, versão 6.6.45.Final) — mapeamento objeto-relacional (ORM)
- **MySQL Connector/J** (`mysql-connector-j`, versão 9.7.0) — driver JDBC para ligação ao MySQL
  
### Ferramentas
- **Eclipse IDE** — ambiente de desenvolvimento
- **Git / GitHub** — controlo de versões e colaboração
- **MS Planner** — gestão do projeto (Product Backlog e Do, Doing, Done)
- **MySQL Workbench** — inspeção do esquema, tabelas e registos

  ---

## Modelo de domínio

Diagrama de classes e modelo relacional:

//![Diagrama de classes](docs/diagrama-classes.png)/ - colocar os diagramas
//![Modelo relacional](docs/modelo-relacional.png)/

### Entidades principais

| Entidade | Descrição |
|---|---|
| `Utilizador` | Pessoa registada na aplicação (tipo Normal ou Gestor) |
| `Anuncio` | Superclasse dos anúncios de objetos |
| `AnuncioPerdido` | Anúncio de um objeto perdido |
| `AnuncioEncontrado` | Anúncio de um objeto encontrado, com a respetiva localização |
| `Chat` | Conversa entre utilizadores sobre um anúncio |
| `Mensagem` | Mensagem enviada por um utilizador num chat |

Enumerados: `Estado` (Pendente, Resolvido), `Categoria` (Eletrónicos, Livros, Material_Escrita, Roupa) e `TipoU` (Normal, Gestor).

### Relacionamentos

- **Um-para-muitos:** `Utilizador` → `Anuncio` (um utilizador publica vários anúncios)
- **Um-para-muitos:** `Anuncio` → `Chat` (um anúncio pode ter vários chats)
- **Um-para-muitos:** `Chat` → `Mensagem` (um chat contém várias mensagens)
- **Muitos-para-muitos:** `Chat` ↔ `Utilizador`, através da tabela de associação `chat_utilizador`
- **Herança:** `AnuncioPerdido` e `AnuncioEncontrado` herdam de `Anuncio`
  
---

## User stories implementadas

Product Backlog no MS Planner: [<link do Planner>](https://planner.cloud.microsoft/ui/web/plan/hHkyn65800yXqvLRVLWy55YAHxqJ/view/grid?tid=0fd45313-b71c-42da-a488-ad857179a11a)

| ID | User story | Estado | Atribuído a:
|---|---|---|---|
| US01 | Como potencial utilizador, eu quero registar uma nova conta de utilizador para ter acesso às funcionalidades do sistema. | Implementada | Ana Souto
| US02 | US02 - •	Como utilizador, quero autenticar-me para conseguir aceder ao sistema. | Implementada | Ana Souto
| US03 | US03 - Como utilizador, eu quero visualizar os meus dados pessoais para consultar as minhas informações de perfil. | Implementada | Ana Souto
| US04 | US04 -Como utilizador, eu quero atualizar os meus dados pessoais para manter a minha informação atualizada.| Implementada | Mathias Pando
| US05 | US05 - Como utilizador, eu quero visualizar artigos para encontrar os meus pertences. | Implementada | Antônio Santos
| US06 | US06 - Como utilizador, eu quero visualizar os pedidos para devolver os pertences. | Implementada | Antônio Santos
| US07 | US07 - •	Como utilizador, eu quero registar um objeto encontrado para que o dono o possa reivindicar. | Implementada | Guilherme Silvia
| US08 | US08 - •	Como utilizador, eu quero criar um pedido de objeto perdido para tentar encontrar o meu pertence. | Implementada | Guilherme Silva
| US09 | US09  - Como utilizador, eu quero filtrar a lista de artigos para refinar os meus resultados de pesquisa. | Implementada | Antônio Santos
| US10 | US10 - Como gestor, eu quero visualizar indicadores estatísticos para analisar o fluxo de objetos na plataforma. | Implementada | Mathias Pando
| US11 | US11 - Como utilizador, eu quero submeter um pedido de reivindicação num objeto encontrado para sinalizar que esse bem me pertence. | Implementada | João Pinto

OBS.: João Pinto e Antônio Santos ficaram responsáveis pelas classes teste e gerir.

---

## Estrutura do projeto

```text
objetos-perdidos/
├── src/main/java/upt/lp/objetosperdidos/
│   ├── Anuncio.java
│   ├── AnuncioEncontrado.java
│   ├── AnuncioPerdido.java
│   ├── Categoria.java
│   ├── Chat.java
│   ├── Estado.java
│   ├── Gerir.java
│   ├── Mensagem.java
│   ├── Teste.java
│   ├── TipoUtilizador.java
│   └── Utilizador.java
├── src/main/resources/
├── pom.xml
└── README.md
```

---

## Gestão do projeto

- Projeto criado no **MS Planner** com o nome `LP2627_E3`
- Repositórios GitHub criados por componente, seguindo o formato `LP2627_E3_C1`, `LP2627_E3_C2` e `LP2627_E3_C3`
- Docentes adicionados como colaboradores nos repositórios:
  - pmorais@upt.pt
  - tiago.bastos@upt.pt

---

## Utilização de IA Generativa

Conforme o guia do projeto, o uso de IA Generativa nas componentes 1 e 2 é permitido apenas como apoio tutorial (esclarecimento de conceitos, orientação, análise de abordagens e identificação de erros), não sendo permitida a resolução integral ou substancial do código da aplicação. Na componente 3, é permitido o uso de IA Generativa para produzir ou corrigir código, mantendo-se a equipa responsável por todo o código entregue.
