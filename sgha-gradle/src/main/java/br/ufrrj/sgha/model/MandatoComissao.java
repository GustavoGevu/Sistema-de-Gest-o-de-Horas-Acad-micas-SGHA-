package br.ufrrj.sgha.model;

import java.time.LocalDate;

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
@Table(name = "mandatos_comissao")
public class MandatoComissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioComissao usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PerfilUsuario perfil;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(nullable = false)
    private boolean ativo;

    public MandatoComissao() {}

    public MandatoComissao(Long id, PerfilUsuario perfil, LocalDate dataInicio, LocalDate dataFim, boolean ativo) {
        this.id = id;
        this.perfil = perfil;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.ativo = ativo;
    }

    public boolean isVigenteEm(LocalDate data) {
        if (!ativo) return false;
        boolean aposInicio = !data.isBefore(dataInicio);
        boolean antesFim = (dataFim == null) || !data.isAfter(dataFim);
        return aposInicio && antesFim;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UsuarioComissao getUsuario() { return usuario; }
    public void setUsuario(UsuarioComissao usuario) { this.usuario = usuario; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}