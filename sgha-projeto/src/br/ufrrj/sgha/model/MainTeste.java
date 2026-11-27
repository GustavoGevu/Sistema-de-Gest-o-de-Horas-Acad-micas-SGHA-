package br.ufrrj.sgha.model;

import java.time.LocalDate;

public class MainTeste {
    public static void main(String[] args) {
        LocalDate hoje = LocalDate.now();

        // 1. Discente
        Discente aluno = new Discente(1L, "Gustavo Gevu dos Santos Lemos", "20250012345");

        // 2. Docentes com histórico de mandatos (vigência e troca de cargo)
        UsuarioComissao profTiago = new UsuarioComissao(1L, "Prof. Tiago Cruz", "tiago.cruz", "1234");
        profTiago.adicionarMandato(new MandatoComissao(1L, PerfilUsuario.COMISSAO_EXTENSAO,
                hoje.minusYears(1), hoje.minusMonths(2), false)); // mandato encerrado
        profTiago.adicionarMandato(new MandatoComissao(2L, PerfilUsuario.COORDENADOR,
                hoje.minusMonths(1), null, true)); // mandato atual como Coordenador

        UsuarioComissao profInativo = new UsuarioComissao(2L, "Prof. Antigo", "antigo", "1234");
        profInativo.adicionarMandato(new MandatoComissao(3L, PerfilUsuario.COMISSAO_ESTAGIO,
                hoje.minusYears(2), hoje.minusDays(10), false));

        System.out.println("Tiago pode logar hoje? " + profTiago.podeFazerLogin(hoje)
                + " | Perfil atual: " + profTiago.getMandatoVigente(hoje).getPerfil());
        System.out.println("Prof. Antigo pode logar hoje? " + profInativo.podeFazerLogin(hoje));

        // 3. Criação, envio, avaliação, recurso e aceite
        Solicitacao sol = new Solicitacao(
                100L,
                aluno,
                "Certificado SNCT UFRRJ",
                "snct_2026_uuid.pdf",
                PerfilUsuario.COMISSAO_EXTENSAO,
                20,
                "Apresentação de ferramentas digitais na SNCT."
        );
        aluno.adicionarSolicitacao(sol);
        System.out.println("Pode excluir em CRIADA? " + sol.podeExcluirFisicamente());

        sol.enviar();
        System.out.println("Pode excluir após ENVIADA? " + sol.podeExcluirFisicamente());

        MandatoComissao mandatoTiago = profTiago.getMandatoVigente(hoje);
        sol.avaliar(profTiago, mandatoTiago, 15, "Certificado SNCT 2026 - UFRRJ",
                "Homologado parcialmente conforme carga horária do evento.",
                "Conferido na ata oficial da SNCT.");

        sol.abrirRecurso("O certificado inclui também a carga horária de organização.");
        sol.avaliar(profTiago, mandatoTiago, 20, null,
                "Recurso deferido após validação da lista de organizadores.",
                "Lista complementar anexada pela coordenação.");
        sol.aceitarParecer();

        System.out.println("\nStatus final: " + sol.getStatus() + " | Total horas concluídas do discente: " + aluno.getTotalHorasConcluidas());
        System.out.println("\n--- TRILHA DE AUDITORIA ---");
        for (HistoricoStatus h : sol.getHistorico()) {
            System.out.println(h.getId() + ". [" + h.getStatusAnterior() + " -> " + h.getStatusNovo() + "] por "
                    + h.getNomeResponsavel() + " (Perfil: " + h.getPerfilNoMomento() + ") | Público: " + h.getParecerPublico());
        }
    }
}