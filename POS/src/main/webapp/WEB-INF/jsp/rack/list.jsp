<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Rack Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary:#2563eb;
            --primary-hover:#1e40af;
            --bg:#f8fafc;
            --glass:rgba(255,255,255,0.75);
            --text:#0f172a;
            --muted:#64748b;
            --border:#e2e8f0;
            --danger:#dc2626;
            --radius:16px;
            --shadow:0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            background: var(--bg);
            padding: 40px 20px;
            color: var(--text);
        }

        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;
            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);
            border: 1px solid var(--border);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            box-shadow: var(--shadow);
            text-decoration: none;
            color: var(--text);
        }

        .container-box {
            max-width: 1200px;
            margin: 60px auto 0;
        }

        .card {
            background: var(--glass);
            backdrop-filter: blur(16px);
            border-radius: var(--radius);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
            overflow: hidden;
        }

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
            color: white;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px 16px;
            font-size: 13px;
            border-bottom: 1px solid var(--border);
            vertical-align: top;
        }

        th {
            font-size: 11px;
            text-transform: uppercase;
            color: var(--muted);
            background: rgba(248,250,252,0.8);
        }

        .badge-active {
            background: #dcfce7;
            color: #166534;
            font-size: 11px;
            padding: 4px 8px;
            border-radius: 6px;
        }

        .badge-inactive {
            background: #fee2e2;
            color: #991b1b;
            font-size: 11px;
            padding: 4px 8px;
            border-radius: 6px;
        }

        .actions {
            display: flex;
            gap: 8px;
        }

        .btn-edit {
            background: #eef2ff;
            color: var(--primary);
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-decoration: none;
        }

        .btn-delete {
            background: #fee2e2;
            color: var(--danger);
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-decoration: none;
        }

        .empty-state {
            padding: 40px;
            text-align: center;
            color: var(--muted);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">
    <div class="card">

        <div class="card-header">
            <span>Rack Management</span>
            <a href="${pageContext.request.contextPath}/rack/add" class="add-btn">
                + Add Rack
            </a>
        </div>

        <c:if test="${empty racks}">
            <div class="empty-state">No racks found</div>
        </c:if>

        <c:if test="${not empty racks}">
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Shelves</th>
                    <th style="width:160px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${racks}" var="rack">
                    <tr>
                        <td>${rack.identifier}</td>

                        <td>
                            <c:forEach items="${rack.shelfIdentifiers}" var="s">
                                <span class="badge bg-secondary me-1">${s}</span>
                            </c:forEach>
                        </td>

                        <td class="actions">
                            <a class="btn-edit"
                               href="${pageContext.request.contextPath}/rack/get?id=${rack.id}">
                                Edit
                            </a>
                            <a class="btn-delete"
                               href="${pageContext.request.contextPath}/rack/delete?id=${rack.id}">
                                Delete
                            </a>
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