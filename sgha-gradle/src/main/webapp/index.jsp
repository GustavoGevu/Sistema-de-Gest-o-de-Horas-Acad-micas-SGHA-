<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>SGHA - UFRRJ | Portal de Acesso</title>
    <style>
        body { font-family: Arial, sans-serif; background: #0f172a; color: #f1f5f9; margin: 0; padding: 40px; }
        .container { max-width: 820px; margin: auto; display: flex; gap: 24px; flex-wrap: wrap; }
        .card { background: #1e293b; padding: 24px; border-radius: 8px; flex: 1; min-width: 300px; border: 1px solid #334155; }
        h1 { text-align: center; color: #38bdf8; margin-bottom: 32px; }
        h2 { color: #93c5fd; margin-top: 0; }
        label { display: block; margin-top: 12px; font-size: 14px; }
        input { width: 100%; padding: 8px; margin-top: 4px; border-radius: 4px; border: 1px solid #475569; background: #0f172a; color: #fff; box-sizing: border-box; }
        button { margin-top: 18px; width: 100%; padding: 10px; background: #2563eb; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; }
        button:hover { background: #1d4ed8; }
        .erro { background: #7f1d1d; color: #fecaca; padding: 10px; border-radius: 4px; margin-bottom: 16px; text-align: center; }
    </style>
</head>
<body>
    <h1>SGHA - Sistema de Gestão de Horas Acadêmicas (UFRRJ)</h1>

    <% if (request.getAttribute("erro") != null) { %>
        <div class="erro"><%= request.getAttribute("erro") %></div>
    <% } %>

    <div class="container">
        <div class="card">
            <h2>Acesso do Discente</h2>
            <p style="font-size: 13px; color: #cbd5e1;">Primeiro acesso realiza cadastro automático pela matrícula.</p>
            <form action="${pageContext.request.contextPath}/discente" method="post">
                <label for="nomeCompleto">Nome Completo:</label>
                <input type="text" id="nomeCompleto" name="nomeCompleto" required placeholder="Ex: Gustavo Gevu dos Santos Lemos">

                <label for="matricula">Matrícula:</label>
                <input type="text" id="matricula" name="matricula" required placeholder="Ex: 20250012345">

                <button type="submit">Entrar / Cadastrar</button>
            </form>
        </div>

        <div class="card">
            <h2>Acesso Comissão / Coordenação</h2>
            <p style="font-size: 13px; color: #cbd5e1;">Contas pré-cadastradas com mandato vigente.</p>
            <form action="${pageContext.request.contextPath}/comissao" method="post">
                <label for="login">Login:</label>
                <input type="text" id="login" name="login" required placeholder="coord, extensao, complementares, estagio, secretaria">

                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required placeholder="1234">

                <button type="submit" style="background: #dc2626;">Entrar como Avaliador</button>
            </form>
        </div>
    </div>
</body>
</html>