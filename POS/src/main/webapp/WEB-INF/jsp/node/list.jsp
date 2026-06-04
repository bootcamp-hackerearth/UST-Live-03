<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
            color: #020617;
        }

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

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #e5e7eb;
        }

        th {
            background: #f1f5f9;
            font-weight: 700;
        }

        tr {
            line-height: 1.3;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        .roles {
            font-size: 13px;
        }

        .action-link {
            padding: 6px 12px;
            border-radius: 18px;
            text-decoration: none;
            font-weight: 600;
            font-size: 12px;
            color: #ffffff;
        }

        .edit {
            background: teal;
        }

        .delete {
            background: #ef4444;
            margin-left: 6px;
        }

        .empty {
            text-align: center;
            padding: 18px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="app-title">POS Application</div>
    <h2>Node Management</h2>
    <div class="list-actions">
        <a href="${pageContext.request.contextPath}/" class="home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/node/add" class="add-btn">
            Add Node
        </a>
    </div>

    <c:choose>
        <c:when test="${empty nodes}">
            <div class="empty">No nodes available</div>
        </c:when>

        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Identifier</th>
                        <th>Path</th>
                        <th>Roles</th>
                        <th>Action</th>
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
                            <td>
                                <a class="action-link edit"
                                   href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}">
                                    Edit
                                </a>

                                <a class="action-link delete"
                                   href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                   onclick="return confirm('Are you sure you want to delete this node?');">
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
</body>
</html>