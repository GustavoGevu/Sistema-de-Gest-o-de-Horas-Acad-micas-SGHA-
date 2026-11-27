<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.ufrrj.sgha.model.Discente" %>
<%@ page import="br.ufrrj.sgha.model.Solicitacao" %>
<%
    Discente discente = (Discente) request.getAttribute("discente");
    if (discente == null) {
        discente = (Discente) session.getAttribute("discenteLogado");
    }
    if (discente == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Dashboard do Discente</title>
    <style>
        body { font-family: Arial, sans-serif; background: #0f172a; color: #f1f5f9; margin: 0; padding: 32px; }
        .topo { display: flex; justify-content: space-between; align-items: center; background: #1e293b; padding: 16px 24px; border-radius: 8px; margin-bottom: 24px; }
        .btn { padding: 8px 14px; border-radius: 4px; text-decoration: none; font-weight: bold; border: none; cursor: pointer; }
        .btn-azul { background: #2563eb; color: #fff; }
        .btn-vermelho { background: #dc2626; color: #fff; }
        table { width: 100%; border-collapse: collapse; background: #1e293b; border-radius: 8px; overflow: hidden; }
        th, td { padding: 12px; border-bottom: 1px solid #334155; text-align: left; font-size: 14px; }
        th { background: #334155; color: #93c5fd; }
    </style>
</head>
<body>
    <div class="topo">
        <div>
            <h2 style="margin: 0;">Olá, <%= discente.getNomeCompleto() %></h2>
            <small>Matrícula: <%= discente.getMatricula() %> | Horas Concluídas: <strong><%= discente.getTotalHorasConcluidas() %>h</strong> (Sessão expira em 2 min de inatividade)</small>
        </div>
        <div style="display: flex; gap: 10px;">
            <a class="btn btn-azul" href="${pageContext.request.contextPath}/nova-solicitacao.jsp">+ Nova Solicitação</a>
            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0;">
                <input type="hidden" name="acao" value="sair">
                <button type="submit" class="btn btn-vermelho">Sair (Logout)</button>
            </form>
        </div>
    </div>

    <h3>Minhas Solicitações</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Documento</th>
                <th>Comissão Destino</th>
                <th>Horas Pretendidas</th>
                <th>Arquivo Salvo</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <% if (discente.getSolicitacoes().isEmpty()) { %>
                <tr>
                    <td colspan="6" style="text-align: center; color: #94a3b8;">Nenhuma solicitação cadastrada ainda.</td>
                </tr>
            <% } else {
                for (Solicitacao s : discente.getSolicitacoes()) { %>
                <tr>
                    <td><%= s.getId() %></td>
                    <td><%= s.getTituloDocumento() %></td>
                    <td><%= s.getComissaoDestino() %></td>
                    <td><%= s.getHorasPretendidas() %>h</td>
                    <td><%= s.getCaminhoArquivoComprovante() %></td>
                    <td><strong><%= s.getStatus() %></strong></td>
                </tr>
            <%  }
               } %>
        </tbody>
    </table>
</body>
</html>