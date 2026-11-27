package br.ufrrj.sgha.controller;

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
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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

            Long solicitacaoId = Long.parseLong(request.getParameter("solicitacaoId"));
            int horasHomologadas = Integer.parseInt(request.getParameter("horasHomologadas"));
            String parecerPublico = request.getParameter("parecerPublico");
            String obsPrivada = request.getParameter("obsPrivada");

            solicitacaoDAO.avaliarSolicitacao(solicitacaoId, avaliador.getId(), horasHomologadas, null, parecerPublico, obsPrivada);
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

        List<Solicitacao> solicitacoes = (perfilAtivo != null && perfilAtivo != PerfilUsuario.COORDENADOR && perfilAtivo != PerfilUsuario.SECRETARIO)
                ? solicitacaoDAO.listarPorComissao(perfilAtivo)
                : solicitacaoDAO.listarTodos();

        request.setAttribute("usuarioComissao", usuario);
        request.setAttribute("perfilAtivo", perfilAtivo);
        request.setAttribute("solicitacoes", solicitacoes);
        request.getRequestDispatcher("/dashboard-comissao.jsp").forward(request, response);
    }
}