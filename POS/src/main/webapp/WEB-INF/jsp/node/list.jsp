<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body { background-color:#f5f6f7; }
        .page-header {
            background:#000; color:#fff;
            padding:14px 20px;
            border-radius:8px;
            margin-bottom:20px;
        }
        .card { border-radius:10px; }
        .table thead th { background:#f1f3f5; }

        .action-icon {
            font-size:1.1rem; margin:0 8px;
        }
        .edit-icon { color:#0d6efd; }
        .delete-icon { color:#dc3545; }

        .role-text {
            margin:2px 6px;
            font-weight:500;
        }

        .add-btn { background:#198754; color:#fff; }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h3>Node Management</h3>

        <div>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/node/add" class="btn add-btn">
                <i class="bi bi-plus-circle"></i> Add Node
            </a>
        </div>
    </div>

    <div class="card shadow">
        <div class="card-body p-0">

            <c:if test="${empty nodes}">
                <div class="text-center text-muted py-4">
                    No nodes found
                </div>
            </c:if>

            <c:if test="${not empty nodes}">
                <table class="table table-hover align-middle mb-0">

                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Identifier</th>
                        <th>Path</th>
                        <th>Roles</th>
                        <th class="text-center">Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="node" items="${nodes}">
                        <tr>

                            <td>${node.id}</td>
                            <td>${node.identifier}</td>
                            <td>${node.path}</td>

                            <td>
                                <c:if test="${empty node.roles}">
                                    <span class="text-muted">No roles</span>
                                </c:if>

                                <c:forEach var="role" items="${node.roles}">
                                    <span class="badge bg-info text-dark">${role}</span>
                                </c:forEach>
                            </td>

                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                   class="action-icon edit-icon">
                                    <i class="bi bi-pencil-square"></i>
                                </a>

                                <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                   class="action-icon delete-icon"
                                   onclick="return confirm('Delete this node?');">
                                    <i class="bi bi-trash"></i>
                                </a>
                            </td>

                        </tr>
                    </c:forEach>
                    </tbody>

                </table>
            </c:if>

        </div>
    </div>

</div>

</body>
</html>
