package br.ufrrj.sgha.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioComissao {
    private Long id;
    private String nome;
    private String login;
    private String senha;
    private List<MandatoComissao> mandatos = new ArrayList<>();

    public UsuarioComissao() {}

    public UsuarioComissao(Long id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    public void adicionarMandato(MandatoComissao mandato) {
        mandato.setUsuario(this);
        this.mandatos.add(mandato);
    }

    public MandatoComissao getMandatoVigente(LocalDate dataReferencia) {
        for (MandatoComissao m : mandatos) {
            if (m.isVigenteEm(dataReferencia)) {
                return m;
            }
        }
        return null;
    }

    public boolean podeFazerLogin(LocalDate dataReferencia) {
        return getMandatoVigente(dataReferencia) != null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public List<MandatoComissao> getMandatos() { return mandatos; }
    public void setMandatos(List<MandatoComissao> mandatos) { this.mandatos = mandatos; }
}