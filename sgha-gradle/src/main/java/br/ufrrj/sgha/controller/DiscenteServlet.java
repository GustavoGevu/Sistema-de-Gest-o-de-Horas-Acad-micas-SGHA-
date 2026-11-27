package br.ufrrj.sgha.controller;

import br.ufrrj.sgha.dao.DiscenteDAO;
import br.ufrrj.sgha.model.Discente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/discente")
public class DiscenteServlet extends HttpServlet {

    private final DiscenteDAO discenteDAO = new DiscenteDAO();

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

        String nomeCompleto = request.getParameter("nomeCompleto");
        String matricula = request.getParameter("matricula");

        if (nomeCompleto == null || nomeCompleto.isBlank() || matricula == null || matricula.isBlank()) {
            request.setAttribute("erro", "Informe nome completo e matrícula.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        Discente discente = discenteDAO.buscarOuCadastrar(nomeCompleto, matricula);
        HttpSession sessao = request.getSession(true);
        sessao.setMaxInactiveInterval(120); // 2 minutos de inatividade
        sessao.setAttribute("discenteLogado", discente);

        request.setAttribute("discente", discente);
        request.getRequestDispatcher("/dashboard-discente.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        if (sessao == null || sessao.getAttribute("discenteLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Discente discenteSessao = (Discente) sessao.getAttribute("discenteLogado");

        // Recarrega do banco para garantir a lista de solicitações atualizada
        Discente discenteAtualizado = discenteDAO.buscarPorMatriculaComSolicitacoes(discenteSessao.getMatricula());
        if (discenteAtualizado == null) {
            sessao.invalidate();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        sessao.setAttribute("discenteLogado", discenteAtualizado);
        request.setAttribute("discente", discenteAtualizado);
        request.getRequestDispatcher("/dashboard-discente.jsp").forward(request, response);
    }
}