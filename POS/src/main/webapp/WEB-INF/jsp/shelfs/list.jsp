<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Shelfs Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CONTAINER ===== */
        .container {
            width: 95%;
            max-width: 1000px;
            margin: 40px auto;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            padding: 18px;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6; /* light teal */
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 10px;
            color: #020617;
        }

        /* ===== ACTIONS ===== */
        .list-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-bottom: 12px;
        }

        .home-btn {
            padding: 7px 16px;
            background: #ffffff;
            color: teal;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid teal;
        }

        .add-btn {
            padding: 7px 16px;
            background: teal;
            color: #ffffff;
            text-decoration: none;
            border-radius: 18px;
            font-size: 13px;
            font-weight: 600;
        }

        /* ===== TABLE ===== */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            text-align: center;
        }

        th {
            background-color: #f1f5f9;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            border-bottom: 1px solid #e5e7eb;
            font-weight: 500;
        }

        tr {
            line-height: 1.3;
        }

        tbody tr:hover {
            background-color: #f8fafc;
        }

        /* ===== STATUS ===== */
        .status-toggle {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            border: none;
            cursor: pointer;
        }

        .status-true {
            background-color: teal;
        }

        .status-false {
            background-color: #9ca3af;
        }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: #ffffff;
            text-decoration: none;
        }

        .edit {
            background-color: teal;
        }

        .delete {
            background-color: #ef4444;
            margin-left: 6px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>

    <h2>Shelfs Management</h2>

    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/shelfs/add" class="add-btn">
            Add Shelf
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Shelf Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="s" items="${shelfss}">
            <tr>
                <td>${s.id}</td>
                <td>${s.identifier}</td>

                <td>
                    <button
                        class="status-toggle ${s.status ? 'status-true' : 'status-false'}"
                        onclick="toggleStatus('${s.identifier}')">
                        ${s.status ? 'Active' : 'Inactive'}
                    </button>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/shelfs/get?identifier=${s.identifier}"
                       class="action-link edit">
                        Edit
                    </a>

                    <a href="${pageContext.request.contextPath}/shelfs/delete?identifier=${s.identifier}"
                       class="action-link delete"
                       onclick="return confirm('Are you sure you want to delete this shelf?');">
                        Delete
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<script>
    function toggleStatus(identifier) {
        fetch('${pageContext.request.contextPath}/shelfs/toggle-status?identifier=' + identifier, {
            method: 'POST'
        }).then(() => location.reload());
    }
</script>

</body>
</html>