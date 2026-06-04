<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 2px solid #eee;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }

        h2 { margin: 0; }

        .header-buttons {
            display: flex;
            gap: 8px;
        }

        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            color: white;
            font-weight: bold;
            display: inline-block;
        }

        .btn-home   { background-color: #6c757d; }
        .btn-add    { background-color: #28a745; }
        .btn-edit   { background-color: #007bff; padding: 6px 12px; }
        .btn-delete { background-color: #dc3545; padding: 6px 12px; }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #f8f9fa;
            color: #666;
            text-align: left;
            padding: 12px 15px;
            border-bottom: 2px solid #dee2e6;
            font-size: 14px;
            white-space: nowrap;
        }

        td {
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            vertical-align: middle;
        }

        tr:hover { background-color: #fafafa; }

        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .text-muted { color: #999; }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h2>Role List</h2>
        <div class="header-buttons">
            <a href="/" class="btn btn-home">Home</a>
            <a href="/role/add" class="btn btn-add">+ Add Role</a>
        </div>
    </div>

    <c:if test="${empty roles}">
        <div class="empty-msg">No roles found</div>
    </c:if>

    <c:if test="${not empty roles}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Role</th>
                    <th>Description</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
            <c:forEach var="role" items="${roles}">
                <tr>
                    <td>${role.id}</td>
                    <td>${role.identifier}</td>
                    <td>
                        <c:choose>
                            <c:when test="${empty role.description}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>${role.description}</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <a href="/role/get?identifier=${role.identifier}"
                               class="btn btn-edit">Edit</a>
                            <a href="/role/delete?identifier=${role.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Are you sure you want to delete this role?');">
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

</body>
</html>