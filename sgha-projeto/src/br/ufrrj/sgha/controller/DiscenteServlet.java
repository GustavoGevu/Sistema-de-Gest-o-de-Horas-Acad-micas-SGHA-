package br.ufrrj.sgha.controller;

import br.ufrrj.sgha.model.Discente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/discente")
public class DiscenteServlet extends HttpServlet {

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

        Discente discente = MemoriaStore.buscarOuCadastrarDiscente(nomeCompleto, matricula);
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
        Discente discente = (sessao != null) ? (Discente) sessao.getAttribute("discenteLogado") : null;
        if (discente == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        request.setAttribute("discente", discente);
        request.getRequestDispatcher("/dashboard-discente.jsp").forward(request, response);
    }
}
