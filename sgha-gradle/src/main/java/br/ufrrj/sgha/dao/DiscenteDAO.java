package br.ufrrj.sgha.dao;

import br.ufrrj.sgha.model.Discente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class DiscenteDAO extends GenericDAO<Discente, Long> {

    public DiscenteDAO() {
        super(Discente.class);
    }

    public Discente buscarPorMatriculaComSolicitacoes(String matricula) {
        if (matricula == null) return null;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT d FROM Discente d LEFT JOIN FETCH d.solicitacoes WHERE d.matricula = :mat",
                    Discente.class)
                    .setParameter("mat", matricula.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Discente buscarOuCadastrar(String nomeCompleto, String matricula) {
        String mat = matricula.trim();
        Discente existente = buscarPorMatriculaComSolicitacoes(mat);
        if (existente != null) {
            return existente;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Discente novo = new Discente(null, nomeCompleto.trim(), mat);
            em.persist(novo);
            tx.commit();
            return novo;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}