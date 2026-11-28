package br.ufrrj.sgha.dao;

import java.time.LocalDate;
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

    // Fila da comissão específica (oculta CRIADA e CANCELADA)
    public List<Solicitacao> listarPorComissao(PerfilUsuario comissao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT s FROM Solicitacao s " +
                    "JOIN FETCH s.discente " +
                    "LEFT JOIN FETCH s.historico " +
                    "WHERE s.comissaoDestino = :comissao " +
                    "AND s.status NOT IN (:criada, :cancelada) " +
                    "ORDER BY s.dataCriacao DESC",
                    Solicitacao.class)
                    .setParameter("comissao", comissao)
                    .setParameter("criada", StatusSolicitacao.CRIADA)
                    .setParameter("cancelada", StatusSolicitacao.CANCELADA)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Fila global (oculta CRIADA; inclui CANCELADA se for Coordenador)
    public List<Solicitacao> listarGlobais(boolean incluirCanceladas, String filtroDiscente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT s FROM Solicitacao s " +
                    "JOIN FETCH s.discente d " +
                    "LEFT JOIN FETCH s.historico " +
                    "WHERE s.status <> :criada ");
            if (!incluirCanceladas) {
                jpql.append("AND s.status <> :cancelada ");
            }
            if (filtroDiscente != null && !filtroDiscente.isBlank()) {
                jpql.append("AND (LOWER(d.nomeCompleto) LIKE :busca OR d.matricula LIKE :busca) ");
            }
            jpql.append("ORDER BY s.dataCriacao DESC");

            var query = em.createQuery(jpql.toString(), Solicitacao.class)
                    .setParameter("criada", StatusSolicitacao.CRIADA);
            if (!incluirCanceladas) {
                query.setParameter("cancelada", StatusSolicitacao.CANCELADA);
            }
            if (filtroDiscente != null && !filtroDiscente.isBlank()) {
                query.setParameter("busca", "%" + filtroDiscente.trim().toLowerCase() + "%");
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Solicitacao> buscarPossiveisDuplicidades(Long solicitacaoId, Long discenteId, String tituloDocumento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String termo = (tituloDocumento != null && tituloDocumento.trim().length() >= 4)
                    ? tituloDocumento.trim().substring(0, Math.min(12, tituloDocumento.trim().length())).toLowerCase()
                    : (tituloDocumento != null ? tituloDocumento.trim().toLowerCase() : "");

            return em.createQuery(
                    "SELECT DISTINCT s FROM Solicitacao s " +
                    "LEFT JOIN FETCH s.historico " +
                    "WHERE s.discente.id = :discenteId " +
                    "AND s.id <> :solicitacaoId " +
                    "AND s.status NOT IN (:criada, :cancelada) " +
                    "AND LOWER(s.tituloDocumento) LIKE :termo",
                    Solicitacao.class)
                    .setParameter("discenteId", discenteId)
                    .setParameter("solicitacaoId", solicitacaoId)
                    .setParameter("criada", StatusSolicitacao.CRIADA)
                    .setParameter("cancelada", StatusSolicitacao.CANCELADA)
                    .setParameter("termo", "%" + termo + "%")
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
            MandatoComissao mandatoAtivo = avaliador.getMandatoVigente(LocalDate.now());
            if (mandatoAtivo == null || mandatoAtivo.getPerfil() == PerfilUsuario.SECRETARIO) {
                throw new SecurityException("Perfil sem permissão para emitir parecer.");
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

    public void restaurarSolicitacao(Long solicitacaoId, Long coordenadorId, String justificativa) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            UsuarioComissao coordenador = em.find(UsuarioComissao.class, coordenadorId);
            if (s == null || coordenador == null) {
                throw new IllegalArgumentException("Solicitação ou coordenador não encontrado.");
            }
            MandatoComissao mandatoAtivo = coordenador.getMandatoVigente(LocalDate.now());
            s.restaurar(coordenador, mandatoAtivo,
                    (justificativa != null && !justificativa.isBlank()) ? justificativa : "Restaurada pela Coordenação.");
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void enviarSolicitacao(Long solicitacaoId, Long discenteId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            if (s != null && s.getDiscente().getId().equals(discenteId)) {
                s.enviar();
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void excluirSolicitacao(Long solicitacaoId, Long discenteId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            if (s != null && s.getDiscente().getId().equals(discenteId)) {
                if (!s.podeExcluirFisicamente()) {
                    throw new IllegalStateException("Somente solicitações CRIADAS podem ser excluídas.");
                }
                s.getDiscente().getSolicitacoes().remove(s);
                em.remove(s);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void cancelarSolicitacaoPeloDiscente(Long solicitacaoId, Long discenteId, String motivo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            if (s != null && s.getDiscente().getId().equals(discenteId)) {
                s.cancelar(s.getDiscente().getNomeCompleto(), null,
                        (motivo != null && !motivo.isBlank()) ? motivo : "Cancelada pelo discente.");
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void aceitarOuRecorrer(Long solicitacaoId, Long discenteId, boolean aceitar, String justificativa) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Solicitacao s = em.find(Solicitacao.class, solicitacaoId);
            if (s != null && s.getDiscente().getId().equals(discenteId)) {
                if (aceitar) {
                    s.aceitarParecer();
                } else {
                    s.abrirRecurso((justificativa != null && !justificativa.isBlank())
                            ? justificativa : "Recurso solicitado pelo discente.");
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}