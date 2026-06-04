<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary:#2563eb;
            --bg:#f8fafc;
            --glass:rgba(255,255,255,.75);
            --text:#0f172a;
            --muted:#64748b;
            --border:#e2e8f0;
            --danger:#dc2626;
            --success:#16a34a;
            --radius:16px;
            --shadow:0 20px 40px rgba(2,6,23,.08);
        }

        * {
            box-sizing:border-box;
            font-family:'Inter',sans-serif;
        }

        body {
            background:var(--bg);
            min-height:100vh;
            padding:40px 20px;
            color:var(--text);
        }

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
        }

        .container-box {
            max-width:1100px;
            margin:60px auto 0;
        }

        .card {
            background:var(--glass);
            backdrop-filter:blur(16px);
            border-radius:var(--radius);
            border:1px solid var(--border);
            box-shadow:var(--shadow);
            overflow:hidden;
        }

        .card-header {
            padding:20px 24px;
            display:flex;
            justify-content:space-between;
            align-items:center;
            font-weight:600;
            font-size:18px;
        }

        .add-btn {
            padding:8px 14px;
            border-radius:10px;
            background:var(--primary);
            color:white;
            text-decoration:none;
            font-size:13px;
            font-weight:600;
        }

        table {
            width:100%;
            border-collapse:collapse;
        }

        th, td {
            padding:14px 16px;
            font-size:13px;
            border-bottom:1px solid var(--border);
        }

        th {
            font-size:11px;
            text-transform:uppercase;
            color:var(--muted);
            background:rgba(248,250,252,.9);
        }

        tr:hover {
            background:rgba(241,245,249,.6);
        }

        /* TOGGLE */
        .toggle-switch {
            width:46px;
            height:24px;
            border-radius:999px;
            background:#cbd5f5;
            cursor:pointer;
            position:relative;
        }

        .toggle-switch::after {
            content:"";
            position:absolute;
            top:3px;
            left:4px;
            width:18px;
            height:18px;
            background:#fff;
            border-radius:50%;
            transition:.25s;
        }

        .toggle-switch.active {
            background:var(--success);
        }

        .toggle-switch.active::after {
            transform:translateX(20px);
        }

        .actions {
            display:flex;
            gap:8px;
        }

        .btn-edit {
            background:#eef2ff;
            color:var(--primary);
            padding:6px 12px;
            border-radius:8px;
            text-decoration:none;
        }

        .btn-delete {
            background:#fee2e2;
            color:var(--danger);
            padding:6px 12px;
            border-radius:8px;
            text-decoration:none;
        }

        .empty-state {
            padding:40px;
            text-align:center;
            color:var(--muted);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">
    <div class="card">

        <div class="card-header">
            <span>Warehouse Management</span>
            <a href="${pageContext.request.contextPath}/warehouse/add" class="add-btn">
                + Add Warehouse
            </a>
        </div>

        <c:if test="${empty warehouses}">
            <div class="empty-state">No warehouses found</div>
        </c:if>

        <c:if test="${not empty warehouses}">
            <table>
                <thead>
                <tr>
                    <th>Identifier</th>
                    <th>Name</th>
                    <th>Location</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="w" items="${warehouses}">
                    <tr>
                        <td><strong>${w.identifier}</strong></td>
                        <td>${w.name}</td>
                        <td>${w.location}</td>

                        <!-- TOGGLE -->
                        <td>
                            <div class="toggle-switch ${w.status ? 'active' : ''}"
                                 onclick="toggleWarehouse('${w.identifier}', this)">
                            </div>
                        </td>

                        <td>
                            <div class="actions">
                                <a class="btn-edit"
                                   href="${pageContext.request.contextPath}/warehouse/get?identifier=${w.identifier}">
                                    Edit
                                </a>

                                <a class="btn-delete"
                                   href="${pageContext.request.contextPath}/warehouse/delete?identifier=${w.identifier}"
                                   onclick="return confirm('Delete this warehouse?');">
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

<script>
    function toggleWarehouse(identifier, el) {
        el.classList.toggle("active");

        fetch('${pageContext.request.contextPath}/warehouse/toggle-status?identifier=' + identifier, {
            method: 'POST'
        }).then(() => location.reload());
    }
</script>

</body>
</html>