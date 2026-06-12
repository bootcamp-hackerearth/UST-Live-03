<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            min-height: 100vh;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
        }

        .card {
            border-radius: 16px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.85);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .table th {
            background: #e5e7eb;
            color: #111827;
            border: none;
            font-weight: 600;
        }

        .table td {
            background: #ffffff;
            color: #111827;
        }

        .table-hover tbody tr:hover {
            background-color: #f1f5f9;
            transition: 0.2s;
        }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: 0.2s ease;
            color: white;
            text-decoration: none;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .edit-btn {
            background: #3b82f6;
        }

        .delete-btn {
            background: #ef4444;
        }

        .btn-add {
            background: #2563eb;
            border: none;
            color: white;
        }

        .btn-add:hover {
            background: #1d4ed8;
        }

        .roles {
            font-size: 13px;
            color: #334155;
        }
    </style>

</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="text-dark">Node Management</h3>
            </div>

            <c:choose>

                <c:when test="${empty nodes}">
                    <div class="alert alert-warning text-center">
                        No nodes available
                    </div>
                </c:when>

                <c:otherwise>

                    <div class="table-responsive">
                        <table class="table table-bordered table-hover text-center align-middle">

                            <thead>
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

                                    <td>${node.identifier}</td>
                                    <td>${node.path}</td>

                                    <td class="roles">
                                        <c:forEach var="role" items="${node.roles}" varStatus="s">
                                            ${role}<c:if test="${!s.last}">, </c:if>
                                        </c:forEach>
                                    </td>

                                    <td class="d-flex justify-content-center gap-2">

                                        <a class="icon-btn edit-btn"
                                           href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                           title="Edit">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>

                                        <a class="icon-btn delete-btn"
                                           href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                           onclick="return confirm('Delete this node?');"
                                           title="Delete">
                                            <i class="bi bi-trash"></i>
                                        </a>

                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                </c:otherwise>

            </c:choose>
        </div>
        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="/" class="btn btn-secondary">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/node/add"
                   class="btn btn-add">
                    Add Node
                </a>

            </div>

            <div class="text-muted small mt-2">
                Node Management System
            </div>
        </div>
    </div>
</div>
</body>
</html>