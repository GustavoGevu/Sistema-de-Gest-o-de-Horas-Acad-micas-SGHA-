package br.ufrrj.sgha.dao;

import java.util.List;

import br.ufrrj.sgha.model.Discente;
import br.ufrrj.sgha.model.MandatoComissao;
import br.ufrrj.sgha.model.PerfilUsuario;
import br.ufrrj.sgha.model.Solicitacao;
import br.ufrrj.sgha.model.StatusSolicitacao;
import br.ufrrj.sgha.model.UsuarioComissao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class SolicitacaoDAO extends GenericDAO<Solicitacao, Long> {

    public SolicitacaoDAO() {
        super(Solicitacao.class);
    }

    public Solicitacao criarParaDiscente(Long discenteId, String titulo, String nomeArquivoSalvo,
                                         PerfilUsuario comissao, int horas, String obs) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Discente discenteGerenciado = em.find(Discente.class, discenteId);
            if (discenteGerenciado == null) {
                throw new IllegalArgumentException("Discente não encontrado para ID: " + discenteId);
            }

            Solicitacao nova = new Solicitacao(
                    null,
                    discenteGerenciado,
                    titulo,
                    nomeArquivoSalvo,
                    comissao,
                    horas,
                    obs
            );
            discenteGerenciado.adicionarSolicitacao(nova);
            em.persist(nova);
            tx.commit();
            return nova;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Solicitacao buscarPorIdComHistorico(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT s FROM Solicitacao s " +
                    "LEFT JOIN FETCH s.discente " +
                    "LEFT JOIN FETCH s.historico " +
                    "WHERE s.id = :id",
                    Solicitacao.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Solicitacao> listarPorComissao(PerfilUsuario comissao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Solicitacao s JOIN FETCH s.discente WHERE s.comissaoDestino = :comissao ORDER BY s.dataCriacao DESC",
                    Solicitacao.class)
                    .setParameter("comissao", comissao)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Solicitacao> listarPorStatus(StatusSolicitacao status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Solicitacao s JOIN FETCH s.discente WHERE s.status = :status ORDER BY s.dataCriacao DESC",
                    Solicitacao.class)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Solicitacao> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT s FROM Solicitacao s JOIN FETCH s.discente ORDER BY s.dataCriacao DESC",
                    Solicitacao.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void avaliarSolicitacao(Long solicitacaoId, Long avaliadorId, int horasHomologadas,
                                   String novoTituloOpcional, String parecerPublico, String obsPrivada) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            UsuarioComissao avaliador = em.find(UsuarioComissao.class, avaliadorId);
            if (s == null || avaliador == null) {
                throw new IllegalArgumentException("Solicitação ou avaliador não encontrado.");
            }
            MandatoComissao mandatoAtivo = avaliador.getMandatoVigente(java.time.LocalDate.now());
            if (mandatoAtivo == null) {
                throw new IllegalStateException("Avaliador sem mandato vigente.");
            }
            s.avaliar(avaliador, mandatoAtivo, horasHomologadas, novoTituloOpcional, parecerPublico, obsPrivada);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}