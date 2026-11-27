package br.ufrrj.sgha.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historico_status")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusSolicitacao statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusSolicitacao statusNovo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "nome_responsavel", nullable = false, length = 150)
    private String nomeResponsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_no_momento", length = 40)
    private PerfilUsuario perfilNoMomento;

    @Column(name = "parecer_publico", columnDefinition = "TEXT")
    private String parecerPublico;

    @Column(name = "observacao_administrativa", columnDefinition = "TEXT")
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

    public Solicitacao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(Solicitacao solicitacao) { this.solicitacao = solicitacao; }

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