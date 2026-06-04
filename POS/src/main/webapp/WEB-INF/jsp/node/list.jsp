<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .table thead th {
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.03em;
            vertical-align: middle;
            text-align: center;
        }

        .table tbody td {
            vertical-align: middle;
        }

        .badge-role {
            font-size: 12px;
            margin: 2px;
        }

        .action-btns a {
            margin: 0 3px;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-diagram-3-fill me-2"></i> Node Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light fw-semibold me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>

        <a href="${pageContext.request.contextPath}/node/add"
           class="btn btn-light fw-semibold">
            <i class="bi bi-plus-circle me-1"></i> Add Node
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Path</th>
                <th>Roles</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="node" items="${nodes}">
                <tr>
                    <td class="text-center fw-semibold">${node.id}</td>
                    <td class="text-center">${node.identifier}</td>
                    <td class="text-center">${node.path}</td>

                    <td class="text-center">
                        <c:if test="${empty node.roles}">
                            <span class="text-muted">No roles</span>
                        </c:if>

                        <c:forEach var="role" items="${node.roles}">
                            <span class="badge bg-secondary badge-role">
                                ${role}
                            </span>
                        </c:forEach>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this node?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty nodes}">
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No nodes found
                    </td>
                </tr>
            </c:if>

            </tbody>
        </table>

    </div>
</div>

</body>
</html>
