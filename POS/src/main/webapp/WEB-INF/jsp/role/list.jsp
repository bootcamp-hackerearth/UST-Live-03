<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

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
            color: #14b8a6;
            margin-bottom: 4px;
        }

        h2 {
            text-align: center;
            font-size: 22px;
            margin-bottom: 12px;
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
            background: #f1f5f9;
            font-weight: 700;
            border-bottom: 1px solid #e5e7eb;
        }

        td {
            border-bottom: 1px solid #e5e7eb;
        }

        tr:hover {
            background: #f8fafc;
        }

        /* ===== STATUS ===== */
        .status-toggle {
            padding: 5px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            border: none;
        }

        .status-true { background: teal; }
        .status-false { background: #9ca3af; }

        /* ===== ACTION BUTTONS ===== */
        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            font-size: 12px;
            font-weight: 600;
            color: white;
            text-decoration: none;
        }

        .edit { background: teal; }
        .delete { background: #ef4444; margin-left: 6px; }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>
    <h2>Role Management</h2>
    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>
        <a href="${pageContext.request.contextPath}/role/add" class="add-btn">
            Add Role
        </a>
    </div>

    <c:if test="${empty roles}">
        <div style="text-align:center; padding:18px;">No roles found</div>
    </c:if>

    <c:if test="${not empty roles}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="role" items="${roles}">
                    <tr>
                        <td>${role.id}</td>
                        <td>${role.identifier}</td>
                        <td>${role.description}</td>

                        <td>
                            <button
                                class="status-toggle ${role.status ? 'status-true' : 'status-false'}"
                                onclick="toggleStatus('${role.identifier}')">
                                ${role.status ? 'Active' : 'Inactive'}
                            </button>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}"
                               class="action-link edit">Edit</a>

                            <a href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
                               class="action-link delete">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

<script>
function toggleStatus(identifier) {
    fetch('${pageContext.request.contextPath}/role/toggle-status?identifier=' + identifier, {
        method: 'POST'
    }).then(() => location.reload());
}
</script>

</body>
</html>