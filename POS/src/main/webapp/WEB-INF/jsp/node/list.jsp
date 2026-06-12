<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .container {
            max-width: 1100px;
        }

        .card {
            border-radius: 18px;
            border: none;
            background: #ffffff;
            box-shadow: 0 15px 40px rgba(0,0,0,0.25);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .header h2 {
            margin: 0;
            font-weight: 600;
            color: #333;
        }

        .header-actions {
            display: flex;
            gap: 12px;
        }

        .btn-home {
            background: linear-gradient(90deg, #22c55e, #16a34a);
            border: none;
            color: #fff;
            border-radius: 25px;
            padding: 8px 22px;
            font-weight: 600;
        }

        .btn-add {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            color: #fff;
            border-radius: 25px;
            padding: 8px 22px;
            font-weight: 600;
        }

        .btn-home:hover,
        .btn-add:hover {
            opacity: 0.9;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #f4f6fb;
            color: #555;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 12px;
            padding: 14px;
        }

        td {
            padding: 14px;
            font-size: 14px;
            border-top: 1px solid #eee;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #f9fafb;
        }

        .roles {
            color: #666;
            font-size: 13px;
        }

        .actions a {
            display: inline-block;
            padding: 6px 14px;
            font-size: 12px;
            font-weight: 600;
            border-radius: 18px;
            text-decoration: none;
            margin-right: 6px;
        }

        .actions a.edit {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
        }

        .actions a.delete {
            background: #fee2e2;
            color: #dc2626;
        }

        .actions a.edit:hover {
            opacity: 0.9;
        }

        .actions a.delete:hover {
            background: #fecaca;
        }

        .empty {
            text-align: center;
            padding: 40px;
            color: #777;
            font-style: italic;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card p-4">

        <!-- Header -->
        <div class="header">
            <h2>Node Management</h2>

            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/" class="btn btn-home">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/node/add" class="btn btn-add">
                    + Add Node
                </a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty nodes}">
                <div class="empty">
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
                                <a class="edit"
                                   href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}">
                                    Edit
                                </a>

                                <a class="delete"
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
</div>

</body>
</html>