package br.ufrrj.sgha.controller;

import java.io.IOException;

import br.ufrrj.sgha.dao.DiscenteDAO;
import br.ufrrj.sgha.dao.SolicitacaoDAO;
import br.ufrrj.sgha.model.Discente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/discente")
public class DiscenteServlet extends HttpServlet {

    private final DiscenteDAO discenteDAO = new DiscenteDAO();
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

        // Ações sobre solicitações do discente já autenticado
        if (acao != null && !"login".equals(acao)) {
            HttpSession sessao = request.getSession(false);
            Discente discenteLogado = (sessao != null) ? (Discente) sessao.getAttribute("discenteLogado") : null;
            if (discenteLogado == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            long solicitacaoId = Long.parseLong(request.getParameter("solicitacaoId"));
            switch (acao) {
                case "enviar" -> solicitacaoDAO.enviarSolicitacao(solicitacaoId, discenteLogado.getId());
                case "excluir" -> solicitacaoDAO.excluirSolicitacao(solicitacaoId, discenteLogado.getId());
                case "cancelar" -> solicitacaoDAO.cancelarSolicitacaoPeloDiscente(
                        solicitacaoId, discenteLogado.getId(), request.getParameter("motivo"));
                case "aceitar" -> solicitacaoDAO.aceitarOuRecorrer(solicitacaoId, discenteLogado.getId(), true, null);
                case "recurso" -> solicitacaoDAO.aceitarOuRecorrer(
                        solicitacaoId, discenteLogado.getId(), false, request.getParameter("justificativa"));
            }

            response.sendRedirect(request.getContextPath() + "/discente");
            return;
        }

        // Login / cadastro automático
        String nomeCompleto = request.getParameter("nomeCompleto");
        String matricula = request.getParameter("matricula");

        if (nomeCompleto == null || nomeCompleto.isBlank() || matricula == null || matricula.isBlank()) {
            request.setAttribute("erro", "Informe nome completo e matrícula.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        Discente discente = discenteDAO.buscarOuCadastrar(nomeCompleto.trim(), matricula.trim());
        HttpSession sessao = request.getSession(true);
        sessao.setMaxInactiveInterval(120); // 2 minutos conforme regra de negócio
        sessao.setAttribute("discenteLogado", discente);

        response.sendRedirect(request.getContextPath() + "/discente");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        Discente discenteSessao = (sessao != null) ? (Discente) sessao.getAttribute("discenteLogado") : null;
        if (discenteSessao == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Recarrega do banco com solicitações atualizadas
        Discente atualizado = discenteDAO.buscarPorMatriculaComSolicitacoes(discenteSessao.getMatricula());
        if (atualizado != null) {
            sessao.setAttribute("discenteLogado", atualizado);
            request.setAttribute("discente", atualizado);
        } else {
            request.setAttribute("discente", discenteSessao);
        }
        request.getRequestDispatcher("/dashboard-discente.jsp").forward(request, response);
    }
}