package br.ufrrj.sgha.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrrj.sgha.dao.SolicitacaoDAO;
import br.ufrrj.sgha.dao.UsuarioComissaoDAO;
import br.ufrrj.sgha.model.MandatoComissao;
import br.ufrrj.sgha.model.PerfilUsuario;
import br.ufrrj.sgha.model.Solicitacao;
import br.ufrrj.sgha.model.UsuarioComissao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/comissao")
public class ComissaoServlet extends HttpServlet {

    private final UsuarioComissaoDAO usuarioComissaoDAO = new UsuarioComissaoDAO();
    private final SolicitacaoDAO solicitacaoDAO = new SolicitacaoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        if ("sair".equals(acao)) {
            HttpSession sessao = request.getSession(false);
            if (sessao != null) {
                sessao.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if ("avaliar".equals(acao)) {
            HttpSession sessao = request.getSession(false);
            UsuarioComissao avaliador = (sessao != null)
                    ? (UsuarioComissao) sessao.getAttribute("usuarioComissaoLogado")
                    : null;
            if (avaliador == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            long solicitacaoId = Long.parseLong(request.getParameter("solicitacaoId"));
            int horasHomologadas = Integer.parseInt(request.getParameter("horasHomologadas"));
            String novoTitulo = request.getParameter("novoTituloDocumento");
            String parecerPublico = request.getParameter("parecerPublico");
            String obsPrivada = request.getParameter("obsPrivada");

            solicitacaoDAO.avaliarSolicitacao(solicitacaoId, avaliador.getId(), horasHomologadas, novoTitulo, parecerPublico, obsPrivada);
            response.sendRedirect(request.getContextPath() + "/comissao");
            return;
        }

        if ("restaurar".equals(acao)) {
            HttpSession sessao = request.getSession(false);
            UsuarioComissao coordenador = (sessao != null)
                    ? (UsuarioComissao) sessao.getAttribute("usuarioComissaoLogado")
                    : null;
            if (coordenador == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            long solicitacaoId = Long.parseLong(request.getParameter("solicitacaoId"));
            String justificativa = request.getParameter("justificativa");
            solicitacaoDAO.restaurarSolicitacao(solicitacaoId, coordenador.getId(), justificativa);
            response.sendRedirect(request.getContextPath() + "/comissao");
            return;
        }

        String login = request.getParameter("login");
        String senha = request.getParameter("senha");

        UsuarioComissao usuario = usuarioComissaoDAO.autenticar(login, senha, LocalDate.now());
        if (usuario == null) {
            request.setAttribute("erro", "Login ou senha inválidos, ou mandato inativo/expirado.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        HttpSession sessao = request.getSession(true);
        sessao.setMaxInactiveInterval(1800);
        sessao.setAttribute("usuarioComissaoLogado", usuario);

        response.sendRedirect(request.getContextPath() + "/comissao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        UsuarioComissao usuario = (sessao != null)
                ? (UsuarioComissao) sessao.getAttribute("usuarioComissaoLogado")
                : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        MandatoComissao mandatoVigente = usuario.getMandatoVigente(LocalDate.now());
        PerfilUsuario perfilAtivo = (mandatoVigente != null) ? mandatoVigente.getPerfil() : null;
        String busca = request.getParameter("busca");

        List<Solicitacao> solicitacoes;
        if (perfilAtivo == PerfilUsuario.COORDENADOR) {
            solicitacoes = solicitacaoDAO.listarGlobais(true, busca);
        } else if (perfilAtivo == PerfilUsuario.SECRETARIO) {
            solicitacoes = solicitacaoDAO.listarGlobais(false, busca);
        } else if (perfilAtivo != null) {
            solicitacoes = solicitacaoDAO.listarPorComissao(perfilAtivo);
        } else {
            solicitacoes = List.of();
        }

        Map<Long, List<Solicitacao>> mapaDuplicidades = new HashMap<>();
        for (Solicitacao s : solicitacoes) {
            List<Solicitacao> dup = solicitacaoDAO.buscarPossiveisDuplicidades(
                    s.getId(), s.getDiscente().getId(), s.getTituloDocumento());
            if (!dup.isEmpty()) {
                mapaDuplicidades.put(s.getId(), dup);
            }
        }

        request.setAttribute("usuarioComissao", usuario);
        request.setAttribute("perfilAtivo", perfilAtivo);
        request.setAttribute("solicitacoes", solicitacoes);
        request.setAttribute("mapaDuplicidades", mapaDuplicidades);
        request.setAttribute("busca", busca != null ? busca : "");
        request.getRequestDispatcher("/dashboard-comissao.jsp").forward(request, response);
    }
}