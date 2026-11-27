package br.ufrrj.sgha.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "discentes")
public class Discente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @OneToMany(mappedBy = "discente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Discente() {}

    public Discente(Long id, String nomeCompleto, String matricula) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.matricula = matricula;
    }

    public void adicionarSolicitacao(Solicitacao s) {
        s.setDiscente(this);
        this.solicitacoes.add(s);
    }

    @Transient
    public int getTotalHorasConcluidas() {
        return solicitacoes.stream()
                .filter(s -> s.getStatus() == StatusSolicitacao.CONCLUIDO)
                .mapToInt(Solicitacao::getHorasHomologadas)
                .sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public List<Solicitacao> getSolicitacoes() { return solicitacoes; }
    public void setSolicitacoes(List<Solicitacao> solicitacoes) { this.solicitacoes = solicitacoes; }
}