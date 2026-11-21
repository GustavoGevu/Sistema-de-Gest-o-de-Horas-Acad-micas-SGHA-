package br.ufrrj.sgha.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Solicitacao {
    private Long id;
    private Discente discente;
    private String tituloDocumento;
    private String caminhoArquivoComprovante;
    private PerfilUsuario comissaoDestino; // COMISSAO_EXTENSAO, COMISSAO_COMPLEMENTARES ou COMISSAO_ESTAGIO
    private int horasPretendidas;
    private int horasHomologadas;
    private String observacaoDiscente;
    private StatusSolicitacao status;
    private StatusSolicitacao statusAntesCancelamento;
    private LocalDateTime dataCriacao;
    private List<HistoricoStatus> historico = new ArrayList<>();

    public Solicitacao() {
        this.status = StatusSolicitacao.CRIADA;
        this.dataCriacao = LocalDateTime.now();
    }

    public Solicitacao(Long id, Discente discente, String tituloDocumento, String caminhoArquivoComprovante,
                       PerfilUsuario comissaoDestino, int horasPretendidas, String observacaoDiscente) {
        this();
        this.id = id;
        this.discente = discente;
        this.tituloDocumento = tituloDocumento;
        this.caminhoArquivoComprovante = caminhoArquivoComprovante;
        this.comissaoDestino = comissaoDestino;
        this.horasPretendidas = horasPretendidas;
        this.observacaoDiscente = observacaoDiscente;
        registrarHistorico(null, StatusSolicitacao.CRIADA, discente.getNomeCompleto(), null, "Solicitação criada pelo discente.", null);
    }

    public boolean podeExcluirFisicamente() {
        return this.status == StatusSolicitacao.CRIADA;
    }

    public void enviar() {
        if (this.status != StatusSolicitacao.CRIADA) {
            throw new IllegalStateException("Apenas solicitações CRIADAS podem ser enviadas.");
        }
        registrarHistorico(this.status, StatusSolicitacao.ENVIADA, discente.getNomeCompleto(), null, "Enviada para análise da comissão.", null);
        this.status = StatusSolicitacao.ENVIADA;
    }

    public void avaliar(UsuarioComissao avaliador, MandatoComissao mandatoAtivo, int horasHomologadas,
                        String novoTituloOpcional, String parecerPublico, String obsPrivada) {
        if (novoTituloOpcional != null && !novoTituloOpcional.isBlank()) {
            this.tituloDocumento = novoTituloOpcional;
        }
        this.horasHomologadas = horasHomologadas;
        StatusSolicitacao proximoStatus = (this.status == StatusSolicitacao.RECURSO)
                ? StatusSolicitacao.AVALIADA_APOS_RECURSO
                : (this.status == StatusSolicitacao.RECURSO_COLEGIADO ? StatusSolicitacao.CONCLUIDO : StatusSolicitacao.AVALIADA);

        registrarHistorico(this.status, proximoStatus, avaliador.getNome(), mandatoAtivo.getPerfil(), parecerPublico, obsPrivada);
        this.status = proximoStatus;
    }

    public void aceitarParecer() {
        if (this.status != StatusSolicitacao.AVALIADA && this.status != StatusSolicitacao.AVALIADA_APOS_RECURSO) {
            throw new IllegalStateException("Não há parecer pendente de aceite.");
        }
        registrarHistorico(this.status, StatusSolicitacao.CONCLUIDO, discente.getNomeCompleto(), null, "Discente aceitou o parecer.", null);
        this.status = StatusSolicitacao.CONCLUIDO;
    }

    public void abrirRecurso(String justificativa) {
        if (this.status == StatusSolicitacao.AVALIADA) {
            registrarHistorico(this.status, StatusSolicitacao.RECURSO, discente.getNomeCompleto(), null, justificativa, null);
            this.status = StatusSolicitacao.RECURSO;
        } else if (this.status == StatusSolicitacao.AVALIADA_APOS_RECURSO) {
            registrarHistorico(this.status, StatusSolicitacao.RECURSO_COLEGIADO, discente.getNomeCompleto(), null, justificativa, null);
            this.status = StatusSolicitacao.RECURSO_COLEGIADO;
        } else {
            throw new IllegalStateException("Status atual não permite abertura de recurso.");
        }
    }

    public void cancelar(String responsavel, PerfilUsuario perfilResponsavel, String motivo) {
        this.statusAntesCancelamento = this.status;
        registrarHistorico(this.status, StatusSolicitacao.CANCELADA, responsavel, perfilResponsavel, motivo, null);
        this.status = StatusSolicitacao.CANCELADA;
    }

    public void restaurar(UsuarioComissao coordenador, MandatoComissao mandatoAtivo, String justificativa) {
        if (mandatoAtivo == null || mandatoAtivo.getPerfil() != PerfilUsuario.COORDENADOR) {
            throw new SecurityException("Apenas o Coordenador ativo pode restaurar solicitações canceladas.");
        }
        if (this.status != StatusSolicitacao.CANCELADA || this.statusAntesCancelamento == null) {
            throw new IllegalStateException("A solicitação não está cancelada.");
        }
        StatusSolicitacao restaurado = this.statusAntesCancelamento;
        registrarHistorico(this.status, restaurado, coordenador.getNome(), PerfilUsuario.COORDENADOR, justificativa, null);
        this.status = restaurado;
        this.statusAntesCancelamento = null;
    }

    private void registrarHistorico(StatusSolicitacao ant, StatusSolicitacao novo, String resp,
                                    PerfilUsuario perfil, String pub, String priv) {
        long seq = this.historico.size() + 1L;
        this.historico.add(new HistoricoStatus(seq, ant, novo, resp, perfil, pub, priv));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Discente getDiscente() { return discente; }
    public void setDiscente(Discente discente) { this.discente = discente; }

    public String getTituloDocumento() { return tituloDocumento; }
    public void setTituloDocumento(String tituloDocumento) { this.tituloDocumento = tituloDocumento; }

    public String getCaminhoArquivoComprovante() { return caminhoArquivoComprovante; }
    public void setCaminhoArquivoComprovante(String caminhoArquivoComprovante) { this.caminhoArquivoComprovante = caminhoArquivoComprovante; }

    public PerfilUsuario getComissaoDestino() { return comissaoDestino; }
    public void setComissaoDestino(PerfilUsuario comissaoDestino) { this.comissaoDestino = comissaoDestino; }

    public int getHorasPretendidas() { return horasPretendidas; }
    public void setHorasPretendidas(int horasPretendidas) { this.horasPretendidas = horasPretendidas; }

    public int getHorasHomologadas() { return horasHomologadas; }
    public void setHorasHomologadas(int horasHomologadas) { this.horasHomologadas = horasHomologadas; }

    public String getObservacaoDiscente() { return observacaoDiscente; }
    public void setObservacaoDiscente(String observacaoDiscente) { this.observacaoDiscente = observacaoDiscente; }

    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<HistoricoStatus> getHistorico() { return historico; }
}