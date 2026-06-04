<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

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
            --success: #16a34a;

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

        /* BACK BUTTON (SAME AS USERS LIST) */
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

        .actions {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .action-link {
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 600;
            text-decoration: none;
            transition: 0.2s;
        }

        .action-link.edit {
            background: #eef2ff;
            color: var(--primary);
        }

        .action-link.edit:hover {
            background: #e0e7ff;
        }

        .action-link.delete {
            background: #fee2e2;
            color: var(--danger);
        }

        .action-link.delete:hover {
            background: #fecaca;
        }

        .status-toggle {
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;
            border: none;
            cursor: pointer;
            transition: 0.2s;
        }

        .status-true {
            background: #dcfce7;
            color: var(--success);
        }

        .status-false {
            background: #fee2e2;
            color: var(--danger);
        }

        .status-toggle:hover {
            transform: scale(1.03);
        }

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
            <span>Customer Management</span>
            <a class="add-btn" href="${pageContext.request.contextPath}/customer/add">
                Add Customer
            </a>
        </div>

        <c:if test="${empty customers}">
            <div class="empty-state">No customers found</div>
        </c:if>

        <c:if test="${not empty customers}">
            <table>
                <thead>
                <tr>
                    <th>Phone</th>
                    <th>Name</th>
                    <th>Party Type</th>
                    <th>Credit</th>
                    <th>Credit Type</th>
                    <th>Status</th>
                    <th style="width:200px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="c" items="${customers}">
                    <tr>
                        <td>${c.identifier}</td>
                        <td>${c.customerName}</td>
                        <td>${c.partyType}</td>
                        <td>${c.credit}</td>
                        <td>${c.creditType}</td>

                        <td>
                            <button class="status-toggle ${c.status ? 'status-true' : 'status-false'}"
                                    onclick="toggleStatus('${c.identifier}')">
                                ${c.status ? 'Active' : 'Inactive'}
                            </button>
                        </td>

                        <td>
                            <div class="actions">
                                <a class="action-link edit"
                                   href="${pageContext.request.contextPath}/customer/get?identifier=${c.identifier}">
                                    Edit
                                </a>

                                <a class="action-link delete"
                                   href="${pageContext.request.contextPath}/customer/delete?identifier=${c.identifier}"
                                   onclick="return confirm('Are you sure you want to delete this customer?');">
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
    function toggleStatus(identifier) {
        fetch('${pageContext.request.contextPath}/customer/toggle-status?identifier=' + identifier, {
            method: 'POST'
        }).then(() => location.reload());
    }
</script>

</body>
</html>