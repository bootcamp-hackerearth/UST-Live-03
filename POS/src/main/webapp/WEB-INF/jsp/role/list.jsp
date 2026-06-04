<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <style>
        :root {
            --bg: #f6fff8;
            --card: #ffffff;

            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #218838;

            --accent: #ffc107;

            --danger: #dc3545;
            --danger-hover: #c82333;

            --border: #e5e7eb;

            --radius: 14px;
            --shadow: 0 10px 30px rgba(0,0,0,0.08);
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
            background: var(--bg);
            color: var(--text);
            position: relative;
        }

        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 50%;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: var(--text);
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: var(--accent);
            color: black;
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
            color: white;
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
            background: #f9fafb;
        }

        tr:hover {
            background: #f9fafb;
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
            color: #fff;
        }

        .btn-danger:hover {
            background: var(--danger-hover);
        }

        .btn-success {
            background: var(--primary);
            color: #fff;
        }

        .btn-success:hover {
            background: var(--primary-hover);
        }

        .btn-secondary {
            background: var(--accent);
            color: black;
        }

        .btn-secondary:hover {
            background: #e0a800;
        }

        .card-footer {
            padding: 16px;
            text-align: center;
            background: #f9fafb;
            border-top: 1px solid var(--border);
        }

        .footer-actions {
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .alert {
            background: #fff3cd;
            color: #856404;
            padding: 10px 12px;
            border-radius: 8px;
            font-size: 13px;
            text-align: center;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container">

    <div class="card">

        <div class="card-header">
            Role Management
        </div>

        <div class="card-body">

            <c:if test="${empty roles}">
                <div class="alert">No roles found</div>
            </c:if>

            <c:if test="${not empty roles}">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Role</th>
                        <th>Description</th>
                        <th>Action</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="role" items="${roles}">
                        <tr>
                            <td>${role.id}</td>
                            <td>${role.identifier}</td>
                            <td>${role.description}</td>

                            <td class="actions">

                               <a class="btn btn-secondary"
                                  href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}">
                                   Edit
                               </a>

                                <a class="btn btn-danger"
                                   href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}">
                                    Delete
                                </a>

                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>

                </table>
            </c:if>

        </div>

        <div class="card-footer">

            <div class="footer-actions">
                <a href="${pageContext.request.contextPath}/role/add"
                   class="btn btn-success">
                    + Add Role
                </a>
            </div>

        </div>

    </div>

</div>

</body>
</html>