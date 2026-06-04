<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap"
          rel="stylesheet">

    <style>
        :root {
            --bg: #ede9fe;
            --card: #ffffff;

            --text: #4c1d95;
            --muted: #6b7280;

            --primary: #7c3aed;
            --primary-hover: #6d28d9;

            --accent: #c4b5fd;

            --danger: #dc2626;
            --danger-hover: #b91c1c;

            --border: #ddd6fe;

            --radius: 14px;
            --shadow: 0 15px 35px rgba(76, 29, 149, 0.18);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            min-height: 100vh;
            padding: 40px 16px;
            background: linear-gradient(135deg, #ede9fe, #ddd6fe);
            color: var(--text);
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .card {
            background: var(--card);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            overflow: hidden;
        }

        .card-header {
            padding: 18px;
            text-align: center;
            font-size: 18px;
            font-weight: 600;
            color: #ffffff;
            background: var(--primary);
        }

        .card-body {
            padding: 18px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid var(--border);
            font-size: 13px;
        }

        th {
            font-size: 12px;
            text-transform: uppercase;
            color: var(--muted);
            font-weight: 600;
            background: #f5f3ff;
        }

        tr:hover {
            background: #f5f3ff;
        }

        .actions a {
            margin-right: 6px;
        }

        .btn {
            padding: 7px 10px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
            transition: 0.2s;
        }

        .btn-danger {
            background: var(--danger);
            color: #ffffff;
        }

        .btn-danger:hover {
            background: var(--danger-hover);
        }

        .btn-success {
            background: var(--primary);
            color: #ffffff;
        }

        .btn-success:hover {
            background: var(--primary-hover);
        }

        .btn-home {
            background: var(--accent);
            color: #4c1d95;
        }

        .btn-home:hover {
            background: #b197fc;
        }

        .btn-secondary {
            background: var(--accent);
            color: #4c1d95;
        }

        .btn-secondary:hover {
            background: #b197fc;
        }

        .card-footer {
            padding: 16px;
            text-align: center;
            background: #f5f3ff;
            border-top: 1px solid var(--border);
        }

        .footer-actions {
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .alert {
            background: #efe9ff;
            color: #5b21b6;
            padding: 10px 12px;
            border-radius: 8px;
            font-size: 13px;
            text-align: center;
        }

        .roles {
            font-size: 13px;
            color: #4c1d95;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="card">

        <div class="card-header">
            List of Nodes
        </div>

        <div class="card-body">

            <c:choose>
                <c:when test="${empty nodes}">
                    <div class="alert">
                        No nodes available
                    </div>
                </c:when>

                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>Identifier</th>
                            <th>Path</th>
                            <th>Roles</th>
                            <th>Actions</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="node" items="${nodes}">
                            <tr>
                                <td>${node.identifier}</td>
                                <td>${node.path}</td>

                                <td class="roles">
                                    <c:forEach var="role" items="${node.roles}" varStatus="s">
                                        ${role}<c:if test="${!s.last}">, </c:if>
                                    </c:forEach>
                                </td>

                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                       class="btn btn-secondary">
                                        Edit
                                    </a>

                                    <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                       class="btn btn-danger">
                                        Delete
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="card-footer">
            <div class="footer-actions">
                <a href="/" class="btn btn-home">Home</a>

                <a href="${pageContext.request.contextPath}/node/add"
                   class="btn btn-success">+ Add Node</a>
            </div>
        </div>

    </div>
</div>

</body>
</html>