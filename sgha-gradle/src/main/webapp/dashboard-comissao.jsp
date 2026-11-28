<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map, java.net.URLEncoder, java.nio.charset.StandardCharsets" %>
<%@ page import="br.ufrrj.sgha.model.Solicitacao, br.ufrrj.sgha.model.UsuarioComissao, br.ufrrj.sgha.model.PerfilUsuario, br.ufrrj.sgha.model.StatusSolicitacao, br.ufrrj.sgha.model.HistoricoStatus" %>
<%
    UsuarioComissao usuario = (UsuarioComissao) request.getAttribute("usuarioComissao");
    PerfilUsuario perfilAtivo = (PerfilUsuario) request.getAttribute("perfilAtivo");
    List<Solicitacao> solicitacoes = (List<Solicitacao>) request.getAttribute("solicitacoes");
    Map<Long, List<Solicitacao>> mapaDuplicidades = (Map<Long, List<Solicitacao>>) request.getAttribute("mapaDuplicidades");
    String busca = (String) request.getAttribute("busca");
    boolean isSecretario = (perfilAtivo == PerfilUsuario.SECRETARIO);
    boolean isCoordenador = (perfilAtivo == PerfilUsuario.COORDENADOR);
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>SGHA - UFRRJ | Painel da Comissão</title>
    <style>
        body { font-family: Arial, sans-serif; background: #0f172a; color: #f1f5f9; margin: 0; padding: 32px; }
        .container { max-width: 1250px; margin: auto; }
        .header { background: #1e293b; padding: 20px 24px; border-radius: 8px; border: 1px solid #334155; display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .busca-box { background: #1e293b; padding: 14px 20px; border-radius: 8px; border: 1px solid #334155; margin-bottom: 20px; display: flex; gap: 10px; align-items: center; }
        .btn-sair { background: #dc2626; color: #fff; border: none; padding: 10px 16px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .btn-avaliar { background: #16a34a; color: #fff; border: none; padding: 7px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .btn-restaurar { background: #d97706; color: #fff; border: none; padding: 7px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .btn-azul { background: #2563eb; color: #fff; border: none; padding: 7px 14px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .link-pdf { color: #38bdf8; text-decoration: none; font-weight: bold; }
        .alerta-dup { background: #451a03; border: 1px solid #f59e0b; color: #fde68a; padding: 8px; border-radius: 4px; margin-top: 8px; font-size: 12px; }
        .hist-box { margin-top: 8px; padding: 6px 8px; background: #0f172a; border-radius: 4px; font-size: 12px; color: #cbd5e1; }
        table { width: 100%; border-collapse: collapse; background: #1e293b; border-radius: 8px; overflow: hidden; }
        th, td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #334155; font-size: 14px; vertical-align: top; }
        th { background: #0f172a; color: #93c5fd; }
        input[type="number"], input[type="text"] { background: #0f172a; color: #f1f5f9; border: 1px solid #475569; border-radius: 4px; padding: 5px 8px; font-size: 13px; }
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

    <% if (isSecretario || isCoordenador) { %>
    <form class="busca-box" action="<%= request.getContextPath() %>/comissao" method="get">
        <label for="busca"><strong>Pesquisar Discente:</strong></label>
        <input type="text" id="busca" name="busca" value="<%= busca != null ? busca : "" %>" placeholder="Nome ou matrícula..." style="width: 280px;">
        <button type="submit" class="btn-azul">Filtrar</button>
        <% if (busca != null && !busca.isBlank()) { %>
            <a href="<%= request.getContextPath() %>/comissao" style="color: #93c5fd; font-size: 13px;">Limpar filtro</a>
        <% } %>
    </form>
    <% } %>

    <h3>Solicitações <%= isSecretario ? "(Consulta Global)" : "para Análise" %></h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Discente</th>
                <th>Documento / Histórico</th>
                <th>Comissão</th>
                <th>Horas (Pret. / Homol.)</th>
                <th>Status</th>
                <th><%= isSecretario ? "Observações" : "Ação / Parecer" %></th>
            </tr>
        </thead>
        <tbody>
        <% if (solicitacoes == null || solicitacoes.isEmpty()) { %>
            <tr><td colspan="7" style="text-align: center; color: #94a3b8;">Nenhuma solicitação encontrada na fila.</td></tr>
        <% } else {
            for (Solicitacao s : solicitacoes) {
                String arqEncoded = URLEncoder.encode(s.getCaminhoArquivoComprovante(), StandardCharsets.UTF_8);
                List<Solicitacao> duplicadas = (mapaDuplicidades != null) ? mapaDuplicidades.get(s.getId()) : null;
        %>
            <tr>
                <td><%= s.getId() %></td>
                <td>
                    <strong><%= s.getDiscente() != null ? s.getDiscente().getNomeCompleto() : "-" %></strong><br>
                    <small style="color: #94a3b8;">Mat.: <%= s.getDiscente() != null ? s.getDiscente().getMatricula() : "-" %></small>
                </td>
                <td>
                    <strong><%= s.getTituloDocumento() %></strong><br>
                    <a class="link-pdf" target="_blank" href="<%= request.getContextPath() %>/comprovante?arquivo=<%= arqEncoded %>">Abrir Comprovante</a>

                    <% if (duplicadas != null && !duplicadas.isEmpty()) { %>
                        <div class="alerta-dup">
                            <strong>Alerta de Possível Duplicidade Cruzada:</strong><br>
                            <% for (Solicitacao d : duplicadas) { %>
                                • ID #<%= d.getId() %> (<%= d.getComissaoDestino() %> - <%= d.getStatus() %>): "<%= d.getTituloDocumento() %>"<br>
                            <% } %>
                        </div>
                    <% } %>

                    <% if (s.getHistorico() != null && !s.getHistorico().isEmpty()) { %>
                        <div class="hist-box">
                            <strong>Trilha de Auditoria:</strong><br>
                            <% for (HistoricoStatus h : s.getHistorico()) { %>
                                • [<%= h.getStatusNovo() %>] por <%= h.getNomeResponsavel() %><%= h.getPerfilNoMomento() != null ? " (" + h.getPerfilNoMomento() + ")" : "" %>: <%= h.getParecerPublico() != null ? h.getParecerPublico() : "" %><%= (h.getObservacaoAdministrativa() != null && !h.getObservacaoAdministrativa().isBlank()) ? " | <em>Obs. adm.:</em> " + h.getObservacaoAdministrativa() : "" %><br>
                            <% } %>
                        </div>
                    <% } %>
                </td>
                <td><%= s.getComissaoDestino() %></td>
                <td><%= s.getHorasPretendidas() %>h / <strong><%= s.getHorasHomologadas() %>h</strong></td>
                <td><strong><%= s.getStatus() %></strong></td>
                <td>
                    <% if (isSecretario) { %>
                        <span style="color: #94a3b8; font-size: 13px;">Modo consulta (Secretaria)</span>
                    <% } else if (s.getStatus() == StatusSolicitacao.CANCELADA && isCoordenador) { %>
                        <form action="<%= request.getContextPath() %>/comissao" method="post" style="display: flex; flex-direction: column; gap: 6px;">
                            <input type="hidden" name="acao" value="restaurar">
                            <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                            <input type="text" name="justificativa" required placeholder="Motivo da restauração">
                            <button type="submit" class="btn-restaurar">Restaurar Solicitação</button>
                        </form>
                    <% } else if (s.getStatus() == StatusSolicitacao.ENVIADA
                            || s.getStatus() == StatusSolicitacao.RECURSO
                            || (s.getStatus() == StatusSolicitacao.RECURSO_COLEGIADO && isCoordenador)) { %>
                        <form action="<%= request.getContextPath() %>/comissao" method="post" style="display: flex; flex-direction: column; gap: 6px;">
                            <input type="hidden" name="acao" value="avaliar">
                            <input type="hidden" name="solicitacaoId" value="<%= s.getId() %>">
                            <input type="text" name="novoTituloDocumento" placeholder="Corrigir nome doc. (opcional)">
                            <input type="number" name="horasHomologadas" min="0" max="<%= s.getHorasPretendidas() %>" value="<%= s.getHorasPretendidas() %>" required placeholder="Horas" style="width: 100px;">
                            <input type="text" name="parecerPublico" required placeholder="Parecer público (visível ao discente)">
                            <input type="text" name="obsPrivada" placeholder="Obs. administrativa (privada)">
                            <button type="submit" class="btn-avaliar">Homologar / Emitir Parecer</button>
                        </form>
                    <% } else { %>
                        <span style="color: #94a3b8; font-size: 13px;">Aguardando ação do discente ou concluída</span>
                    <% } %>
                </td>
            </tr>
        <%  }
           } %>
        </tbody>
    </table>
</div>
</body>
</html>