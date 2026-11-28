# SGHA — Sistema de Gestão de Horas Acadêmicas (UFRRJ)

Aplicação Java Web desenvolvida no padrão **MVC** com persistência relacional via **JPA / Hibernate** e **MySQL** para gerenciar a submissão, verificação de duplicidades cruzadas, avaliação por comissões específicas, fluxo de recursos e homologação de comprovantes de carga horária dos discentes do curso de **Bacharelado em Sistemas de Informação** da **Universidade Federal Rural do Rio de Janeiro (UFRRJ)**.

---

## 👥 Integrantes do Grupo

- **GUSTAVO GEVU DOS SANTOS LEMOS** — Curso: Sistemas de Informação | Matrícula: `20250024868`
- **NICOLAS MORENO CALABÁ DE OLIVEIRA SILVA** — Curso: Sistemas de Informação | Matrícula: `20250017827`

**Disciplina:** Sistemas Web 2 (2026.2) — ICE / Departamento de Computação (UFRRJ)  
**Docente:** Prof. Tiago Cruz de França  
**Repositório:** [Sistema-de-Gest-o-de-Horas-Acad-micas-SGHA-](https://github.com/GustavoGevu/Sistema-de-Gest-o-de-Horas-Acad-micas-SGHA-)

---

## 🎯 Introdução e Escopo do Projeto

No curso de Sistemas de Informação da UFRRJ, os discentes precisam comprovar carga horária em três frentes distintas: **Horas Complementares**, **Ações Extensionistas** e **Horas de Estágio**. O **SGHA** centraliza esse processo acadêmico-administrativo em uma única plataforma web, substituindo controles manuais dispersos por um fluxo auditável, seguro, persistido em banco de dados relacional e totalmente rastreável.

### Objetivos Principais
- Evitar a contabilização duplicada de um mesmo comprovante físico entre comissões distintas por meio de alerta automático de duplicidade cruzada.
- Garantir que cada solicitação siga uma máquina de estados bem definida, com bloqueios de exclusão após o envio e trilha completa de auditoria persistida no MySQL.
- Controlar o acesso por perfis funcionais (**RBAC**) e pela vigência real dos mandatos dos docentes e da secretaria, preservando no histórico quem emitiu cada parecer e qual função exercia naquele momento.

---

## ⚙️ Funcionalidades Implementadas

### 1. Módulo do Discente
- **Acesso e Cadastro Simplificado:** entrada por nome completo e matrícula; caso a matrícula ainda não exista no banco `sgha_db`, o cadastro é realizado automaticamente, e em acessos futuros suas solicitações são recuperadas sem permitir alteração ou exclusão da conta.
- **Submissão de Comprovantes:** envio de documento (PDF/imagem) via formulário `multipart/form-data` com título, comissão destino (Extensão, Complementares ou Estágio), horas pretendidas e observação do aluno.
- **Gestão do Ciclo de Vida no Painel:**
  - Quando `CRIADA`: permite **Enviar para Comissão** (transição para `ENVIADA`) ou **Excluir** fisicamente do banco.
  - Quando `ENVIADA`, `RECURSO` ou `RECURSO_COLEGIADO`: permite **Cancelar** a solicitação.
  - Quando `AVALIADA` ou `AVALIADA_APOS_RECURSO`: permite **Aceitar Parecer** (concluindo a solicitação e somando as horas homologadas ao total do aluno) ou **Abrir Recurso** / **Recurso Colegiado** com justificativa.
- **Segurança de Sessão:** botão explícito de logout e encerramento automático após **2 minutos** de inatividade.

### 2. Módulo da Comissão, Coordenação e Secretaria (RBAC)
- **5 Perfis Funcionais:**
  - `COMISSAO_EXTENSAO`, `COMISSAO_COMPLEMENTARES` e `COMISSAO_ESTAGIO`: visualizam e avaliam apenas as solicitações destinadas à sua respectiva comissão (ocultando rascunhos `CRIADA` e `CANCELADA`).
  - `COORDENADOR`: acesso global a todas as comissões, análise de `RECURSO_COLEGIADO`, filtro de busca por discente (nome ou matrícula) e poder exclusivo de **Restaurar** solicitações `CANCELADA`.
  - `SECRETARIO`: consulta global com filtro de busca por discente, visualização de comprovantes, duplicidades e trilha de auditoria, sem permissão para emitir pareceres ou alterar status.
- **Gestão de Mandatos:** controle de períodos de vigência (`dataInicio`, `dataFim`, `ativo`), bloqueando o login de ex-membros e permitindo que um mesmo docente alterne entre comissões e coordenação ao longo do tempo.
- **Alerta de Duplicidade Cruzada:** durante a análise, o sistema identifica e exibe automaticamente um alerta visual caso o mesmo discente possua outras solicitações com título semelhante em qualquer comissão.
- **Análise, Correção e Auditoria:** permite ajustar o nome do documento durante a avaliação, homologar horas, separar **Parecer Público** (visível ao discente) de **Observação Administrativa** (restrita às comissões/coordenação/secretaria) e registrar cada transição na tabela `historico_status`.

### 3. Armazenamento Externo ao Container
- Os comprovantes enviados via `@MultipartConfig` são gravados fora do diretório de deploy do servidor web (`C:/sgha-uploads/`), persistindo no banco apenas o nome único gerado (`UUID`) e servindo o arquivo de forma controlada pelo `ComprovanteServlet`.

---

## 🛠️ Tecnologias e Requisitos

- **Linguagem:** Java 17+ (testado com JDK 26)
- **Especificação Web:** Jakarta EE 11 (Jakarta Servlet API 6.0 & JSP)
- **Persistência / ORM:** Jakarta Persistence (JPA) + Hibernate 6
- **Banco de Dados:** MySQL 8 (`sgha_db`)
- **Build & Empacotamento:** Gradle (`war`)
- **Container Web:** Apache Tomcat 11.0.29 (Standalone)
- **Controle de Versão:** Git / GitHub

---

## 📂 Estrutura de Pastas e Descrição dos Arquivos

```text
projeto/
├── README.md
├── DescricaoTrabalho.pdf
├── sgha-projeto/                         # Legado (Entregáveis 1 e 2 - protótipo inicial)
└── sgha-gradle/                          # Projeto principal atualizado (Entregável 3 - Gradle + JPA/MySQL)
    ├── build.gradle
    ├── settings.gradle
    └── src/
        └── main/
            ├── java/
            │   └── br/ufrrj/sgha/
            │       ├── config/
            │       │   └── AppConfig.java
            │       ├── controller/
            │       │   ├── ComissaoServlet.java
            │       │   ├── ComprovanteServlet.java
            │       │   ├── DiscenteServlet.java
            │       │   └── SolicitacaoServlet.java
            │       ├── dao/
            │       │   ├── DiscenteDAO.java
            │       │   ├── GenericDAO.java
            │       │   ├── JPAUtil.java
            │       │   ├── SolicitacaoDAO.java
            │       │   └── UsuarioComissaoDAO.java
            │       └── model/
            │           ├── Discente.java
            │           ├── HistoricoStatus.java
            │           ├── MainTeste.java
            │           ├── MandatoComissao.java
            │           ├── PerfilUsuario.java
            │           ├── Solicitacao.java
            │           ├── StatusSolicitacao.java
            │           └── UsuarioComissao.java
            ├── resources/
            │   └── META-INF/
            │       └── persistence.xml
            └── webapp/
                ├── WEB-INF/
                │   └── web.xml
                ├── dashboard-comissao.jsp
                ├── dashboard-discente.jsp
                ├── index.jsp
                └── nova-solicitacao.jsp

```

### Descrição Detalhada de Cada Pasta e Arquivo (`sgha-gradle/`)

#### Raiz do Módulo Gradle (`sgha-gradle/`)
* **`build.gradle`**: arquivo de configuração do Gradle que aplica os plugins `java` e `war`, define compatibilidade Java 17+, configura o nome final do pacote (`sgha-projeto.war`) e declara as dependências (Jakarta Servlet API, Hibernate Core 6 e MySQL Connector/J).
* **`settings.gradle`**: define o nome raiz do projeto Gradle (`sgha-projeto`).

#### Camada de Configuração (`src/main/java/br/ufrrj/sgha/config/`)
* **`AppConfig.java`**: centraliza os parâmetros globais de infraestrutura da aplicação, definindo o diretório externo de armazenamento dos comprovantes (`C:/sgha-uploads/`) fora do container Tomcat e garantindo sua criação automática.

#### Camada de Controle (`src/main/java/br/ufrrj/sgha/controller/`)
* **`DiscenteServlet.java`** (`/discente`): gerencia o cadastro/login simplificado do estudante por matrícula, inicializa a sessão com expiração de 2 minutos (`120s`), recarrega os dados atualizados do banco via `DiscenteDAO` e processa ações do discente (`enviar`, `excluir`, `cancelar`, `aceitar`, `recurso` e `sair`).
* **`SolicitacaoServlet.java`** (`/solicitacao`): servlet anotado com `@MultipartConfig` que recebe o upload de novos comprovantes, salva o arquivo com identificador único (`UUID`) em `C:/sgha-uploads/` e persiste a nova solicitação no banco via `SolicitacaoDAO`.
* **`ComissaoServlet.java`** (`/comissao`): autentica os membros das comissões, coordenação e secretaria verificando mandatos vigentes, carrega a fila filtrada por perfil RBAC, detecta possíveis duplicidades cruzadas e processa avaliações/pareceres e restaurações de solicitações canceladas.
* **`ComprovanteServlet.java`** (`/comprovante`): realiza o streaming seguro dos arquivos PDF/imagens armazenados em `C:/sgha-uploads/` para visualização no navegador pelos avaliadores e pela secretaria.

#### Camada de Persistência — DAO (`src/main/java/br/ufrrj/sgha/dao/`)
* **`JPAUtil.java`**: gerencia o ciclo de vida único da `EntityManagerFactory` vinculada à unidade de persistência `sghaPU`.
* **`GenericDAO.java`**: classe base genérica com operações CRUD reutilizáveis (`salvar`, `atualizar`, `buscarPorId`, `listarTodos` e `remover`) encapsulando transações JPA.
* **`DiscenteDAO.java`**: consultas específicas de estudantes, incluindo carregamento antecipado (`JOIN FETCH`) das solicitações por matrícula e rotina de busca ou cadastro automático.
* **`SolicitacaoDAO.java`**: centraliza todas as operações transacionais de negócio sobre solicitações (criação, listagem por comissão, consulta global com filtro de busca, detecção de duplicidade cruzada, avaliação com parecer, envio, exclusão física, cancelamento, aceite/recurso e restauração pelo Coordenador).
* **`UsuarioComissaoDAO.java`**: realiza a autenticação de avaliadores com validação de mandato vigente e executa a carga inicial automática (seed) das contas padrão de teste caso o banco esteja vazio.

#### Camada de Modelo — Entidades JPA (`src/main/java/br/ufrrj/sgha/model/`)
* **`StatusSolicitacao.java`**: enumeração (`enum`) dos estados da solicitação (`CRIADA`, `ENVIADA`, `AVALIADA`, `ACEITA`, `RECURSO`, `AVALIADA_APOS_RECURSO`, `RECURSO_COLEGIADO`, `CANCELADA` e `CONCLUIDO`).
* **`PerfilUsuario.java`**: enumeração (`enum`) dos cinco papéis funcionais do sistema (`COMISSAO_EXTENSAO`, `COMISSAO_COMPLEMENTARES`, `COMISSAO_ESTAGIO`, `COORDENADOR` e `SECRETARIO`).
* **`Discente.java`**: entidade JPA (`@Entity`, tabela `discentes`) que representa o aluno, suas solicitações vinculadas (`@OneToMany`) e o cálculo consolidado de horas com status `CONCLUIDO`.
* **`UsuarioComissao.java`**: entidade JPA (tabela `usuarios_comissao`) que modela docentes e servidores administrativos, armazenando credenciais e seus mandatos vinculados.
* **`MandatoComissao.java`**: entidade JPA (tabela `mandatos_comissao`) que controla a vigência temporal (`dataInicio`, `dataFim`, `ativo`) e o `PerfilUsuario` de cada membro.
* **`Solicitacao.java`**: entidade JPA central (tabela `solicitacoes`) que encapsula os dados do documento, a máquina de estados, regras de transição, bloqueios de exclusão e relacionamento com o histórico.
* **`HistoricoStatus.java`**: entidade JPA (tabela `historico_status`) que mantém a trilha imutável de auditoria de cada mudança de estado, responsável, perfil exercido no momento, parecer público e observação administrativa.
* **`MainTeste.java`**: classe utilitária de apoio para testes rápidos de regras de domínio em console.

#### Recursos JPA e Camada Web (`src/main/resources/` e `src/main/webapp/`)
* **`META-INF/persistence.xml`**: configura a unidade de persistência `sghaPU`, o driver MySQL, a URL do banco `sgha_db`, o dialeto Hibernate e a atualização automática do esquema (`hibernate.hbm2ddl.auto = update`).
* **`WEB-INF/web.xml`**: descritor Jakarta EE 11 que define `index.jsp` como página inicial e configura o timeout de sessão para 2 minutos (`<session-timeout>2</session-timeout>`).
* **`index.jsp`**: portal de acesso inicial dividido entre login/cadastro automático de discentes e login institucional das comissões/coordenação/secretaria.
* **`dashboard-discente.jsp`**: painel do estudante autenticado com total de horas concluídas e botões dinâmicos por status (`Enviar para Comissão`, `Excluir`, `Cancelar`, `Aceitar Parecer` e `Abrir Recurso`).
* **`nova-solicitacao.jsp`**: formulário `multipart/form-data` para submissão de documento comprobatório, comissão de destino, horas pretendidas e observações.
* **`dashboard-comissao.jsp`**: painel analítico das comissões, coordenação e secretaria com filtro de busca por discente, visualização de comprovantes, alertas de duplicidade cruzada, trilha de auditoria e formulários de homologação/restauração conforme o perfil RBAC.

---

## 🚀 Como Configurar e Executar

1. **Criar o Banco de Dados MySQL:**
   ```sql
   CREATE DATABASE sgha_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   *(As tabelas e os usuários padrão são gerados automaticamente na primeira execução; ajuste usuário/senha em `sgha-gradle/src/main/resources/META-INF/persistence.xml` caso seu MySQL não utilize `root` sem senha na porta `3306`.)*

2. **Gerar o Arquivo `.war` via Gradle:**
   ```bash
   cd sgha-gradle
   ./gradlew war
   ```
   O pacote será gerado em `sgha-gradle/build/libs/sgha-projeto.war`.

3. **Implantar no Apache Tomcat 11:**
   - Copie `sgha-projeto.war` para a pasta `webapps/` do Apache Tomcat 11.
   - Certifique-se de que `JAVA_HOME` aponta para um JDK 17+ e inicie o servidor (`bin/startup.bat` ou `bin/catalina.bat run`).
   - Acesse no navegador: `http://localhost:8080/sgha-projeto/`

---

## 🔑 Credenciais de Teste

| Perfil | Login | Senha | Permissões |
| :--- | :--- | :--- | :--- |
| **Coordenação** | `coord` | `1234` | Visão global, avaliação, recurso colegiado e restauração de canceladas |
| **Comissão de Extensão** | `extensao` | `1234` | Avaliação e parecer nas solicitações de Extensão |
| **Comissão de Complementares** | `complementares` | `1234` | Avaliação e parecer nas solicitações de Atividades Complementares |
| **Comissão de Estágio** | `estagio` | `1234` | Avaliação e parecer nas solicitações de Estágio |
| **Secretaria Acadêmica** | `secretaria` | `1234` | Consulta global e auditoria (somente leitura) |
| **Discente** | *(Nome + Matrícula)* | — | Cadastro automático no primeiro acesso; submissão, envio, aceite e recurso |