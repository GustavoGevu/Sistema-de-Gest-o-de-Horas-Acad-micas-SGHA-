<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.net.URLEncoder, java.nio.charset.StandardCharsets, br.ufrrj.sgha.model.Solicitacao, br.ufrrj.sgha.model.UsuarioComissao, br.ufrrj.sgha.model.PerfilUsuario" %>
<%
    UsuarioComissao usuario = (UsuarioComissao) request.getAttribute("usuarioComissao");
    PerfilUsuario perfilAtivo = (PerfilUsuario) request.getAttribute("perfilAtivo");
    List<Solicitacao> solicitacoes = (List<Solicitacao>) request.getAttribute("solicitacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>SGHA - UFRRJ | Painel da Comissão</title>
    <style>
        body { font-family: Arial, sans-serif; background: #0f172a; color: #f1f5f9; margin: 0; padding: 32px; }
        .container { max-width: 1150px; margin: auto; }
        .header { background: #1e293b; padding: 20px 24px; border-radius: 8px; border: 1px solid #334155; display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .btn-sair { background: #dc2626; color: #fff; border: none; padding: 10px 16px; border-radius: 4px; cursor: pointer; }
        .btn-avaliar { background: #16a34a; color: #fff; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; }
        .link-pdf { color: #38bdf8; text-decoration: none; }
        table { width: 100%; border-collapse: collapse; background: #1e293b; border-radius: 8px; overflow: hidden; }
        th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #334155; font-size: 14px; vertical-align: top; }
        th { background: #0f172a; color: #93c5fd; }
        input[type="number"], input[type="text"] { background: #0f172a; color: #f1f5f9; border: 1px solid #475569; border-radius: 4px; padding: 5px 8px; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div>
            <h2 style="margin: 0; color: #38bdf8;"><%= usuario != null ? usuario.getNome() : "Avaliador" %></h2>
            <p style="margin: 6px 0 0; color: #cbd5e1;">Perfil vigente: <strong><%= perfilAtivo != null ? perfilAtivo.name() : "Sem mandato vigente" %></strong></p>
        </div>
        <form action="<%= request.getContextPath() %>/comissao" method="post" style="margin: 0;">
            <input type="hidden" name="acao" value="sair">
            <button type="submit" class="btn-sair">Sair (Logout)</button>
        </form>
    </div>

    <h3>Solicitações para Análise</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Discente</th>
                <th>Documento / Comprovante</th>
                <th>Comissão</th>
                <th>Horas (Pret. / Homol.)</th>
                <th>Status</th>
                <th>Parecer da Comissão</th>
            </tr>
        </thead>
        <tbody>
        <% if (solicitacoes == null || solicitacoes.isEmpty()) { %>
            <tr><td colspan="7" style="text-align: center; color: #94a3b8;">Nenhuma solicitação encontrada.</td></tr>
        <% } else {
            for (Solicitacao s : solicitacoes) {
                String arqEncoded = URLEncoder.encode(s.getCaminhoArquivoComprovante(), StandardCharsets.UTF_8);
        %>
            <tr>
                <td><%= s.getId() %></td>
                <td>
                    <strong><%= s.getDiscente() != null ? s.getDiscente().getNomeCompleto() : "-" %></strong><br>
                    <small style="color: #94a3b8;">Mat.: <%= s.getDiscente() != null ? s.getDiscente().getMatricula() : "-" %></small>
                </td>
                <td>
                    <%= s.getTituloDocumento() %><br>
                    <a class="link-pdf" target="_blank" href="<%= request.getContextPath() %>/comprovante?arquivo=<%= arqEncoded %>">Abrir Comprovante</a>
                </td>
                <td><%= s.getComissaoDestino() %></td>
                <td><%= s.getHorasPretendidas() %>h / <strong><%= s.getHorasHomologadas() %>h</strong></td>
                <td><%= s.getStatus() %></td>
                <td>
                    <form action="<%= request.getContextPath() %>/comissao" method="post" style="display: flex; flex-direction: column; gap: 6px;">
                        <input type="hidden" name="acao" value="avaliar">
                        <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                        <input type="number" name="horasHomologadas" min="0" max="<%= s.getHorasPretendidas() %>" value="<%= s.getHorasPretendidas() %>" required placeholder="Horas" style="width: 90px;">
                        <input type="text" name="parecerPublico" required placeholder="Parecer público (ex: Deferido)">
                        <input type="text" name="obsPrivada" placeholder="Obs. interna (opcional)">
                        <button type="submit" class="btn-avaliar">Homologar / Avaliar</button>
                    </form>
                </td>
            </tr>
        <%  }
           } %>
        </tbody>
    </table>
</div>
</body>
</html>