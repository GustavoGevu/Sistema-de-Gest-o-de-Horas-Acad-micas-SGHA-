package br.ufrrj.sgha.controller;

import br.ufrrj.sgha.config.AppConfig;
import br.ufrrj.sgha.model.Discente;
import br.ufrrj.sgha.model.PerfilUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/solicitacao")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
    maxFileSize = 1024 * 1024 * 5,        // 5 MB
    maxRequestSize = 1024 * 1024 * 10     // 10 MB
)
public class SolicitacaoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession sessao = request.getSession(false);
        Discente discente = (sessao != null) ? (Discente) sessao.getAttribute("discenteLogado") : null;
        if (discente == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String titulo = request.getParameter("tituloDocumento");
        String comissaoStr = request.getParameter("comissaoDestino");
        int horas = Integer.parseInt(request.getParameter("horasPretendidas"));
        String obs = request.getParameter("observacaoDiscente");

        Part filePart = request.getPart("arquivoComprovante");
        String nomeOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String nomeUnico = UUID.randomUUID() + "_" + nomeOriginal;

        File dirUpload = AppConfig.garantirDiretorioUpload();
        File destino = new File(dirUpload, nomeUnico);
        filePart.write(destino.getAbsolutePath());

        PerfilUsuario comissao = PerfilUsuario.valueOf(comissaoStr);
        MemoriaStore.criarSolicitacao(discente, titulo, nomeUnico, comissao, horas, obs);

        response.sendRedirect(request.getContextPath() + "/discente");
    }
}