<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.ufrrj.sgha.model.Discente" %>
<%@ page import="br.ufrrj.sgha.model.Solicitacao" %>
<%@ page import="br.ufrrj.sgha.model.StatusSolicitacao" %>
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
        .btn { padding: 7px 12px; border-radius: 4px; text-decoration: none; font-weight: bold; border: none; cursor: pointer; font-size: 13px; }
        .btn-azul { background: #2563eb; color: #fff; }
        .btn-verde { background: #16a34a; color: #fff; }
        .btn-amarelo { background: #d97706; color: #fff; }
        .btn-vermelho { background: #dc2626; color: #fff; }
        table { width: 100%; border-collapse: collapse; background: #1e293b; border-radius: 8px; overflow: hidden; }
        th, td { padding: 12px; border-bottom: 1px solid #334155; text-align: left; font-size: 14px; vertical-align: top; }
        th { background: #334155; color: #93c5fd; }
        input[type="text"] { background: #0f172a; color: #f1f5f9; border: 1px solid #475569; border-radius: 4px; padding: 5px 8px; font-size: 13px; }
        .acoes { display: flex; flex-wrap: wrap; gap: 6px; align-items: center; }
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
                <th>Horas (Pret. / Homol.)</th>
                <th>Status</th>
                <th>Ações Disponíveis</th>
            </tr>
        </thead>
        <tbody>
            <% if (discente.getSolicitacoes().isEmpty()) { %>
                <tr>
                    <td colspan="6" style="text-align: center; color: #94a3b8;">Nenhuma solicitação cadastrada ainda.</td>
                </tr>
            <% } else {
                for (Solicitacao s : discente.getSolicitacoes()) {
                    StatusSolicitacao st = s.getStatus();
            %>
                <tr>
                    <td><%= s.getId() %></td>
                    <td><%= s.getTituloDocumento() %></td>
                    <td><%= s.getComissaoDestino() %></td>
                    <td><%= s.getHorasPretendidas() %>h / <strong><%= s.getHorasHomologadas() %>h</strong></td>
                    <td><strong><%= st %></strong></td>
                    <td>
                        <div class="acoes">
                        <% if (st == StatusSolicitacao.CRIADA) { %>
                            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0;">
                                <input type="hidden" name="acao" value="enviar">
                                <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                                <button type="submit" class="btn btn-verde">Enviar para Comissão</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0;">
                                <input type="hidden" name="acao" value="excluir">
                                <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                                <button type="submit" class="btn btn-vermelho">Excluir</button>
                            </form>
                        <% } else if (st == StatusSolicitacao.ENVIADA || st == StatusSolicitacao.RECURSO || st == StatusSolicitacao.RECURSO_COLEGIADO) { %>
                            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0;">
                                <input type="hidden" name="acao" value="cancelar">
                                <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                                <button type="submit" class="btn btn-vermelho">Cancelar</button>
                            </form>
                        <% } else if (st == StatusSolicitacao.AVALIADA || st == StatusSolicitacao.AVALIADA_APOS_RECURSO) { %>
                            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0;">
                                <input type="hidden" name="acao" value="aceitar">
                                <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                                <button type="submit" class="btn btn-verde">Aceitar Parecer</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/discente" method="post" style="margin: 0; display: flex; gap: 4px;">
                                <input type="hidden" name="acao" value="recurso">
                                <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                                <input type="text" name="justificativa" required placeholder="Motivo do recurso">
                                <button type="submit" class="btn btn-amarelo">
                                    <%= st == StatusSolicitacao.AVALIADA ? "Abrir Recurso" : "Recurso Colegiado" %>
                                </button>
                            </form>
                        <% } else { %>
                            <span style="color: #94a3b8;">Sem ações pendentes</span>
                        <% } %>
                        </div>
                    </td>
                </tr>
            <%  }
               } %>
        </tbody>
    </table>
</body>
</html>