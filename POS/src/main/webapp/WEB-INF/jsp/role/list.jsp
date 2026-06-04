<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;

            --border: #e2e8f0;

            --danger: #dc2626;
            --danger-hover: #b91c1c;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            padding: 40px 20px;
            color: var(--text);
        }

        /* BACK BUTTON */
        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;

            width: 42px;
            height: 42px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);

            border: 1px solid var(--border);
            color: var(--text);

            text-decoration: none;
            font-size: 18px;

            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
        }

        .container-box {
            max-width: 1100px;
            margin: 60px auto 0;
        }

        /* CARD */
        .card {
            background: var(--glass);
            backdrop-filter: blur(16px);

            border-radius: var(--radius);
            border: 1px solid var(--border);

            box-shadow: var(--shadow);
            overflow: hidden;
        }

        /* HEADER */
        .card-header {
            padding: 20px 24px;
            font-size: 18px;
            font-weight: 600;

            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .add-btn {
            padding: 8px 14px;
            border-radius: 10px;
            background: var(--primary);
            color: #fff;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition: 0.2s;
        }

        .add-btn:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }

        /* TABLE */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px 16px;
            font-size: 13px;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }

        th {
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--muted);
            background: rgba(248,250,252,0.8);
        }

        tr:hover {
            background: rgba(241,245,249,0.6);
        }

        /* ACTION BUTTONS */
        .actions {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-decoration: none;
            transition: 0.2s;
        }

        .btn-edit {
            background: #eef2ff;
            color: var(--primary);
        }

        .btn-edit:hover {
            background: #e0e7ff;
        }

        .btn-delete {
            background: #fee2e2;
            color: var(--danger);
        }

        .btn-delete:hover {
            background: #fecaca;
        }

        /* EMPTY STATE */
        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--muted);
            font-size: 14px;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">

    <div class="card">

        <div class="card-header">
            <span>Role Management</span>

            <a href="${pageContext.request.contextPath}/role/add" class="add-btn">
                + Add Role
            </a>
        </div>

        <c:if test="${empty roles}">
            <div class="empty-state">
                No roles found
            </div>
        </c:if>

        <c:if test="${not empty roles}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th style="width:160px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="role" items="${roles}">
                    <tr>
                        <td>${role.id}</td>
                        <td>${role.identifier}</td>
                        <td>${role.description}</td>

                        <td>
                            <div class="actions">

                                <a class="btn btn-edit"
                                   href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}">
                                    Edit
                                </a>

                                <a class="btn btn-delete"
                                   href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}">
                                    Delete
                                </a>

                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

            </table>
        </c:if>

    </div>

</div>

</body>
</html>