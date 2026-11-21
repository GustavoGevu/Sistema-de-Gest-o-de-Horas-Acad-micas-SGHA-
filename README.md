# SGHA — Sistema de Gestão de Horas Acadêmicas (UFRRJ)

Aplicação Java Web desenvolvida no padrão **MVC** para gerenciar a submissão, verificação de duplicidades, avaliação por comissões específicas, fluxo de recursos e homologação de comprovantes de carga horária dos discentes do curso de **Bacharelado em Sistemas de Informação** da **Universidade Federal Rural do Rio de Janeiro (UFRRJ)**.

---

## 👥 Integrantes do Grupo

- **GUSTAVO GEVU DOS SANTOS LEMOS** — Curso: Sistemas de Informação | Matrícula: `20250024868`
- **NICOLAS MORENO CALABÁ DE OLIVEIRA SILVA** — Curso: Sistemas de Informação | Matrícula: `20250017827`

**Disciplina:** Sistemas Web 2 (2026.2) — ICE / Departamento de Computação (UFRRJ)  
**Docente:** Prof. Tiago Cruz de França  
**Repositório:** [Sistema-de-Gest-o-de-Horas-Acad-micas-SGHA-](https://github.com/GustavoGevu/Sistema-de-Gest-o-de-Horas-Acad-micas-SGHA-)

---

## 🎯 Introdução e Escopo do Projeto

No curso de Sistemas de Informação da UFRRJ, os discentes precisam comprovar carga horária em três frentes distintas: **Horas Complementares**, **Ações Extensionistas** e **Horas de Estágio**. O **SGHA** centraliza esse processo acadêmico-administrativo em uma única plataforma web, substituindo controles manuais dispersos por um fluxo auditável, seguro e rastreável.

### Objetivos Principais
- Evitar a contabilização duplicada de um mesmo comprovante físico entre comissões distintas (alerta automático de duplicidade cruzada).
- Garantir que cada solicitação siga uma máquina de estados bem definida, com bloqueios de exclusão após o envio e trilha completa de auditoria.
- Controlar o acesso por perfis funcionais (**RBAC**) e pela vigência real dos mandatos dos docentes e da secretaria, preservando no histórico quem emitiu cada parecer e qual função exercia naquele momento.

---

## ⚙️ Funcionalidades Principais

### 1. Módulo do Discente
- **Acesso e Cadastro Simplificado:** entrada por nome completo e matrícula; caso a matrícula ainda não exista, o cadastro é realizado automaticamente, e em acessos futuros suas solicitações são recuperadas sem permitir alteração ou exclusão da conta.
- **Submissão de Comprovantes:** envio de documento (PDF/imagem) com título, comissão destino (Extensão, Complementares ou Estágio), horas pretendidas e observação do aluno.
- **Ciclo de Vida (Máquina de Estados):** `CRIADA`, `ENVIADA`, `AVALIADA`, `ACEITA`, `RECURSO`, `AVALIADA_APOS_RECURSO`, `RECURSO_COLEGIADO`, `CANCELADA` e `CONCLUIDO` — permitindo exclusão física apenas enquanto `CRIADA` e restauração de canceladas exclusivamente pelo Coordenador.
- **Segurança de Sessão:** botão explícito de logout e encerramento automático após **2 minutos** de inatividade.

### 2. Módulo da Comissão, Coordenação e Secretaria (RBAC)
- **5 Perfis Funcionais:** `COMISSAO_EXTENSAO`, `COMISSAO_COMPLEMENTARES`, `COMISSAO_ESTAGIO`, `COORDENADOR` (acesso irrestrito e poder exclusivo de restaurar solicitações canceladas) e `SECRETARIO` (consulta global sem poder de decisão).
- **Gestão de Mandatos:** controle de períodos de vigência (`dataInicio`, `dataFim`, `ativo`), bloqueando o login de ex-membros e permitindo que um mesmo docente alterne entre comissões e coordenação ao longo do tempo.
- **Análise e Auditoria:** ajuste do título do documento pela comissão, separação entre **Parecer Público** (visível ao discente) e **Observação Administrativa** (restrita às comissões/coordenação/secretaria) e registro imutável de todas as transições.

### 3. Armazenamento Externo ao Container
- Os comprovantes enviados via formulário `multipart/form-data` (`@MultipartConfig`) são gravados fora do diretório do servidor web (`C:/sgha-uploads/`), persistindo na solicitação apenas o identificador único gerado para o arquivo.

---

## 🛠️ Tecnologias e Requisitos

- **Linguagem:** Java (JDK 17+ / 25)
- **Especificação Web:** Jakarta EE 11 (Jakarta Servlet API & JSP)
- **Container Web:** Apache Tomcat 11.0.29 (Standalone)
- **Controle de Versão:** Git / GitHub

---

## 📂 Estrutura de Pastas e Descrição dos Arquivos

```text
sgha-projeto/
├── README.md
├── src/
│   └── br/ufrrj/sgha/
│       ├── config/
│       │   └── AppConfig.java
│       ├── controller/
│       │   ├── DiscenteServlet.java
│       │   ├── MemoriaStore.java
│       │   └── SolicitacaoServlet.java
│       └── model/
│           ├── Discente.java
│           ├── HistoricoStatus.java
│           ├── MainTeste.java
│           ├── MandatoComissao.java
│           ├── PerfilUsuario.java
│           ├── Solicitacao.java
│           ├── StatusSolicitacao.java
│           └── UsuarioComissao.java
└── WebContent/
    ├── WEB-INF/
    │   └── web.xml
    ├── dashboard-discente.jsp
    ├── index.jsp
    └── nova-solicitacao.jsp

### Descrição de Cada Pasta e Arquivo

#### Raiz do Projeto (`sgha-projeto/`)
* **`README.md`**: documentação geral do projeto, identificação dos integrantes, escopo, regras de negócio, arquitetura e instruções de execução.
* **`src/`**: diretório raiz que armazena todo o código-fonte Java da aplicação, organizado no pacote base `br.ufrrj.sgha`.
* **`WebContent/`**: diretório raiz dos recursos web públicos, páginas JSP e descritores de configuração do container servlet (Apache Tomcat).

#### Camada de Configuração (`src/br/ufrrj/sgha/config/`)
* **`AppConfig.java`**: centraliza os parâmetros globais de infraestrutura da aplicação, definindo o caminho externo de armazenamento dos comprovantes (`C:/sgha-uploads/`) fora do container web e garantindo a criação automática desse diretório.

#### Camada de Controle (`src/br/ufrrj/sgha/controller/`)
* **`MemoriaStore.java`**: simula a camada de persistência em memória para o Entregável 2, mantendo coleções estáticas de discentes, solicitações e contas pré-cadastradas de comissões, coordenação e secretaria com seus respectivos mandatos.
* **`DiscenteServlet.java`**: controlador responsável pelo acesso e cadastro simplificado do discente por matrícula, inicialização da sessão (`HttpSession`) com tempo limite de inatividade de 2 minutos e processamento do logout explícito.
* **`SolicitacaoServlet.java`**: controlador anotado com `@MultipartConfig` que recebe o formulário de nova solicitação, valida a sessão ativa, grava o arquivo comprovante com nome único (`UUID`) na pasta externa `C:/sgha-uploads/` e registra a solicitação no modelo.

#### Camada de Modelo (`src/br/ufrrj/sgha/model/`)
* **`StatusSolicitacao.java`**: enumeração (`enum`) que define todos os estados possíveis do ciclo de vida de uma solicitação (`CRIADA`, `ENVIADA`, `AVALIADA`, `ACEITA`, `RECURSO`, `AVALIADA_APOS_RECURSO`, `RECURSO_COLEGIADO`, `CANCELADA` e `CONCLUIDO`).
* **`PerfilUsuario.java`**: enumeração (`enum`) que representa os cinco papéis funcionais de avaliação e consulta do sistema (`COMISSAO_EXTENSAO`, `COMISSAO_COMPLEMENTARES`, `COMISSAO_ESTAGIO`, `COORDENADOR` e `SECRETARIO`).
* **`Discente.java`**: entidade que representa o estudante (identificador, nome completo, matrícula e lista de solicitações vinculadas), contendo também o cálculo consolidado de horas homologadas com status `CONCLUIDO`.
* **`UsuarioComissao.java`**: entidade que modela os docentes e servidores administrativos, armazenando seus dados de autenticação e verificando se possuem algum mandato vigente na data de acesso.
* **`MandatoComissao.java`**: entidade que controla o vínculo temporal (`dataInicio`, `dataFim`, `ativo` e `PerfilUsuario`) de um membro da comissão, coordenação ou secretaria, permitindo histórico de troca de funções.
* **`HistoricoStatus.java`**: entidade responsável pela trilha de auditoria da solicitação, registrando cada transição de estado, data/hora, nome do responsável, perfil exercido no momento da ação, parecer público e observação administrativa.
* **`Solicitacao.java`**: entidade central de negócio que encapsula os dados do comprovante, a máquina de estados, o bloqueio de exclusão física após o envio (`ENVIADA`), o fluxo de recursos e a prerrogativa exclusiva do Coordenador para restaurar solicitações canceladas.
* **`MainTeste.java`**: classe executável (`main`) criada para simular e validar no console a criação de objetos, verificação de vigência de mandatos, transições de status e geração da trilha de auditoria.

#### Camada de Visualização e Configuração Web (`WebContent/` e `WebContent/WEB-INF/`)
* **`index.jsp`**: página inicial (portal de acesso) com os formulários de identificação do discente e de autenticação das comissões/coordenação.
* **`dashboard-discente.jsp`**: painel principal do estudante autenticado, exibindo o total de horas concluídas, a tabela com suas solicitações e o botão de encerramento de sessão (logout).
* **`nova-solicitacao.jsp`**: página com formulário `multipart/form-data` para o discente preencher o título do documento, selecionar a comissão de destino, informar as horas pretendidas e anexar o comprovante.
* **`WEB-INF/`**: pasta protegida do container web cujos arquivos não são acessíveis diretamente via URL pelo navegador.
* **`WEB-INF/web.xml`**: descritor de implantação da aplicação Jakarta EE 11 que define a página de boas-vindas (`index.jsp`) e configura o tempo máximo de inatividade da sessão para 2 minutos (`<session-timeout>2</session-timeout>`).
