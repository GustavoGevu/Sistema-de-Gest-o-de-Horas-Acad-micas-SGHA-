package br.ufrrj.sgha.dao;

import br.ufrrj.sgha.model.MandatoComissao;
import br.ufrrj.sgha.model.PerfilUsuario;
import br.ufrrj.sgha.model.UsuarioComissao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;

public class UsuarioComissaoDAO extends GenericDAO<UsuarioComissao, Long> {

    public UsuarioComissaoDAO() {
        super(UsuarioComissao.class);
    }

    public void inicializarUsuariosPadraoSeVazio() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Long total = em.createQuery("SELECT COUNT(u) FROM UsuarioComissao u", Long.class).getSingleResult();
            if (total != null && total > 0) {
                return;
            }

            tx.begin();
            LocalDate hoje = LocalDate.now();

            UsuarioComissao coord = new UsuarioComissao(null, "Prof. Coordenador", "coord", "1234");
            coord.adicionarMandato(new MandatoComissao(null, PerfilUsuario.COORDENADOR, hoje.minusMonths(6), null, true));

            UsuarioComissao ext = new UsuarioComissao(null, "Prof. Extensão", "extensao", "1234");
            ext.adicionarMandato(new MandatoComissao(null, PerfilUsuario.COMISSAO_EXTENSAO, hoje.minusMonths(6), null, true));

            UsuarioComissao comp = new UsuarioComissao(null, "Prof. Complementares", "complementares", "1234");
            comp.adicionarMandato(new MandatoComissao(null, PerfilUsuario.COMISSAO_COMPLEMENTARES, hoje.minusMonths(6), null, true));

            UsuarioComissao est = new UsuarioComissao(null, "Prof. Estágio", "estagio", "1234");
            est.adicionarMandato(new MandatoComissao(null, PerfilUsuario.COMISSAO_ESTAGIO, hoje.minusMonths(6), null, true));

            UsuarioComissao sec = new UsuarioComissao(null, "Secretaria Acadêmica", "secretaria", "1234");
            sec.adicionarMandato(new MandatoComissao(null, PerfilUsuario.SECRETARIO, hoje.minusMonths(6), null, true));

            for (UsuarioComissao u : List.of(coord, ext, comp, est, sec)) {
                em.persist(u);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public UsuarioComissao autenticar(String login, String senha, LocalDate dataReferencia) {
        if (login == null || senha == null) return null;
        inicializarUsuariosPadraoSeVazio();

        EntityManager em = JPAUtil.getEntityManager();
        try {
            UsuarioComissao usuario = em.createQuery(
                    "SELECT DISTINCT u FROM UsuarioComissao u LEFT JOIN FETCH u.mandatos WHERE LOWER(u.login) = :login",
                    UsuarioComissao.class)
                    .setParameter("login", login.trim().toLowerCase())
                    .getSingleResult();

            if (usuario.getSenha().equals(senha) && usuario.podeFazerLogin(dataReferencia)) {
                return usuario;
            }
            return null;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}