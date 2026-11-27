package br.ufrrj.sgha.model;
import java.util.ArrayList;
import java.util.List;

public class Discente {
    private Long id;
    private String nomeCompleto;
    private String matricula;
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