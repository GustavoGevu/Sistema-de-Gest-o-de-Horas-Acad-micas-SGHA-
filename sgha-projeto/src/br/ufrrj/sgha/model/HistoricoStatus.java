package br.ufrrj.sgha.model;
import java.time.LocalDateTime;

public class HistoricoStatus {
    private Long id;
    private StatusSolicitacao statusAnterior;
    private StatusSolicitacao statusNovo;
    private LocalDateTime dataHora;
    private String nomeResponsavel;
    private PerfilUsuario perfilNoMomento; // null se a ação foi do próprio discente
    private String parecerPublico;
    private String observacaoAdministrativa;

    public HistoricoStatus() {}

    public HistoricoStatus(Long id, StatusSolicitacao statusAnterior, StatusSolicitacao statusNovo,
                           String nomeResponsavel, PerfilUsuario perfilNoMomento,
                           String parecerPublico, String observacaoAdministrativa) {
        this.id = id;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataHora = LocalDateTime.now();
        this.nomeResponsavel = nomeResponsavel;
        this.perfilNoMomento = perfilNoMomento;
        this.parecerPublico = parecerPublico;
        this.observacaoAdministrativa = observacaoAdministrativa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusSolicitacao getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(StatusSolicitacao statusAnterior) { this.statusAnterior = statusAnterior; }

    public StatusSolicitacao getStatusNovo() { return statusNovo; }
    public void setStatusNovo(StatusSolicitacao statusNovo) { this.statusNovo = statusNovo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getNomeResponsavel() { return nomeResponsavel; }
    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public PerfilUsuario getPerfilNoMomento() { return perfilNoMomento; }
    public void setPerfilNoMomento(PerfilUsuario perfilNoMomento) { this.perfilNoMomento = perfilNoMomento; }

    public String getParecerPublico() { return parecerPublico; }
    public void setParecerPublico(String parecerPublico) { this.parecerPublico = parecerPublico; }

    public String getObservacaoAdministrativa() { return observacaoAdministrativa; }
    public void setObservacaoAdministrativa(String observacaoAdministrativa) { this.observacaoAdministrativa = observacaoAdministrativa; }
}