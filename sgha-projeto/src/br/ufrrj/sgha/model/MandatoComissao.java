package br.ufrrj.sgha.model;
import java.time.LocalDate;

public class MandatoComissao {
    private Long id;
    private UsuarioComissao usuario;
    private PerfilUsuario perfil;
    private LocalDate dataInicio;
    private LocalDate dataFim; // null se ainda estiver ativo sem data de término definida
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