package br.ufrrj.sgha.controller;

import br.ufrrj.sgha.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MemoriaStore {
    private static final Map<String, Discente> DISCENTES_POR_MATRICULA = new LinkedHashMap<>();
    private static final List<Solicitacao> SOLICITACOES = new ArrayList<>();
    private static final List<UsuarioComissao> USUARIOS_COMISSAO = new ArrayList<>();
    private static final AtomicLong SEQ_DISCENTE = new AtomicLong(1);
    private static final AtomicLong SEQ_SOLICITACAO = new AtomicLong(1);

    static {
        LocalDate hoje = LocalDate.now();
        UsuarioComissao coord = new UsuarioComissao(1L, "Prof. Coordenador", "coord", "1234");
        coord.adicionarMandato(new MandatoComissao(1L, PerfilUsuario.COORDENADOR, hoje.minusMonths(6), null, true));

        UsuarioComissao ext = new UsuarioComissao(2L, "Prof. Extensão", "extensao", "1234");
        ext.adicionarMandato(new MandatoComissao(2L, PerfilUsuario.COMISSAO_EXTENSAO, hoje.minusMonths(6), null, true));

        UsuarioComissao comp = new UsuarioComissao(3L, "Prof. Complementares", "complementares", "1234");
        comp.adicionarMandato(new MandatoComissao(3L, PerfilUsuario.COMISSAO_COMPLEMENTARES, hoje.minusMonths(6), null, true));

        UsuarioComissao est = new UsuarioComissao(4L, "Prof. Estágio", "estagio", "1234");
        est.adicionarMandato(new MandatoComissao(4L, PerfilUsuario.COMISSAO_ESTAGIO, hoje.minusMonths(6), null, true));

        UsuarioComissao sec = new UsuarioComissao(5L, "Secretaria Acadêmica", "secretaria", "1234");
        sec.adicionarMandato(new MandatoComissao(5L, PerfilUsuario.SECRETARIO, hoje.minusMonths(6), null, true));

        USUARIOS_COMISSAO.addAll(List.of(coord, ext, comp, est, sec));
    }

    public static Discente buscarOuCadastrarDiscente(String nomeCompleto, String matricula) {
        String mat = matricula.trim();
        Discente existente = DISCENTES_POR_MATRICULA.get(mat);
        if (existente != null) {
            return existente;
        }
        Discente novo = new Discente(SEQ_DISCENTE.getAndIncrement(), nomeCompleto.trim(), mat);
        DISCENTES_POR_MATRICULA.put(mat, novo);
        return novo;
    }

    public static Discente buscarDiscentePorMatricula(String matricula) {
        if (matricula == null) return null;
        return DISCENTES_POR_MATRICULA.get(matricula.trim());
    }

    public static Solicitacao criarSolicitacao(Discente discente, String titulo, String nomeArquivoSalvo,
                                              PerfilUsuario comissao, int horas, String obs) {
        Solicitacao s = new Solicitacao(
                SEQ_SOLICITACAO.getAndIncrement(),
                discente,
                titulo,
                nomeArquivoSalvo,
                comissao,
                horas,
                obs
        );
        discente.adicionarSolicitacao(s);
        SOLICITACOES.add(s);
        return s;
    }

    public static List<Solicitacao> listarSolicitacoes() {
        return SOLICITACOES;
    }

    public static UsuarioComissao autenticarComissao(String login, String senha, LocalDate dataRef) {
        for (UsuarioComissao u : USUARIOS_COMISSAO) {
            if (u.getLogin().equalsIgnoreCase(login) && u.getSenha().equals(senha) && u.podeFazerLogin(dataRef)) {
                return u;
            }
        }
        return null;
    }
}