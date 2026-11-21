<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="br.ufrrj.sgha.model.Discente" %>
<%
    Discente discente = (Discente) session.getAttribute("discenteLogado");
    if (discente == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>SGHA - Nova Solicitação</title>
    <style>
        body { font-family: Arial, sans-serif; background: #0f172a; color: #f1f5f9; margin: 0; padding: 32px; }
        .card { max-width: 560px; margin: auto; background: #1e293b; padding: 24px; border-radius: 8px; border: 1px solid #334155; }
        label { display: block; margin-top: 14px; font-size: 14px; }
        input, select, textarea { width: 100%; padding: 8px; margin-top: 4px; border-radius: 4px; border: 1px solid #475569; background: #0f172a; color: #fff; box-sizing: border-box; }
        button { margin-top: 20px; width: 100%; padding: 10px; background: #2563eb; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
        a { color: #93c5fd; display: inline-block; margin-top: 12px; text-decoration: none; }
    </style>
</head>
<body>
    <div class="card">
        <h2 style="margin-top: 0; color: #38bdf8;">Nova Solicitação de Carga Horária</h2>
        <form action="${pageContext.request.contextPath}/solicitacao" method="post" enctype="multipart/form-data">
            <label for="tituloDocumento">Título / Nome do Documento:</label>
            <input type="text" id="tituloDocumento" name="tituloDocumento" required placeholder="Ex: Certificado Semana Nacional de Ciência e Tecnologia">

            <label for="comissaoDestino">Comissão Destino:</label>
            <select id="comissaoDestino" name="comissaoDestino" required>
                <option value="COMISSAO_COMPLEMENTARES">Comissão de Atividades Complementares</option>
                <option value="COMISSAO_EXTENSAO">Comissão de Extensão</option>
                <option value="COMISSAO_ESTAGIO">Comissão de Estágio</option>
            </select>

            <label for="horasPretendidas">Horas Pretendidas:</label>
            <input type="number" id="horasPretendidas" name="horasPretendidas" min="1" required placeholder="Ex: 20">

            <label for="arquivoComprovante">Comprovante (PDF/Imagem - até 5MB):</label>
            <input type="file" id="arquivoComprovante" name="arquivoComprovante" required>

            <label for="observacaoDiscente">Observação do Discente:</label>
            <textarea id="observacaoDiscente" name="observacaoDiscente" rows="3" placeholder="Informações adicionais sobre a atividade..."></textarea>

            <button type="submit">Salvar Solicitação</button>
        </form>
        <a href="${pageContext.request.contextPath}/discente">&larr; Voltar ao Dashboard</a>
    </div>
</body>
</html>