package br.ufrrj.sgha.controller;

import br.ufrrj.sgha.config.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/comprovante")
public class ComprovanteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String arquivo = request.getParameter("arquivo");
        if (arquivo == null || arquivo.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Arquivo não informado.");
            return;
        }

        String nomeSeguro = Paths.get(arquivo).getFileName().toString();
        File ficheiro = new File(AppConfig.garantirDiretorioUpload(), nomeSeguro);
        if (!ficheiro.exists() || !ficheiro.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comprovante não encontrado.");
            return;
        }

        String mimeType = getServletContext().getMimeType(ficheiro.getName());
        response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + nomeSeguro + "\"");
        response.setContentLengthLong(ficheiro.length());
        Files.copy(ficheiro.toPath(), response.getOutputStream());
    }
}