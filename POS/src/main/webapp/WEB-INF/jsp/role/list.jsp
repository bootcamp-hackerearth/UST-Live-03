<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #eef2ff, #e0e7ff);
            min-height: 100vh;
        }

        .card {
            border-radius: 15px;
        }

        .table th {
            background-color: #343a40;
            color: white;
        }

        a.role-link {
            text-decoration: none;
            font-weight: 500;
            color: #0d6efd;
        }

        a.role-link:hover {
            text-decoration: underline;
        }

        .action-icons a {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
        }

        .action-icons a:hover {
            opacity: 0.7;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">

        <div class="card-body">

            <h3 class="text-center mb-4">Role Management</h3>

            <c:if test="${empty roles}">
                <div class="alert alert-warning text-center">
                    No roles found
                </div>
            </c:if>

            <c:if test="${not empty roles}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Role</th>
                            <th>Description</th>
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="role" items="${roles}">
                            <tr>

                                <td>
                                    <a class="role-link"
                                       href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}">
                                        ${role.id}
                                    </a>
                                </td>

                                <td>${role.identifier}</td>

                                <td>
                                    <c:choose>
                                        <c:when test="${empty role.description}">
                                            <span class="text-muted">—</span>
                                        </c:when>
                                        <c:otherwise>
                                            ${role.description}
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="action-icons">

                                    <a href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}"
                                       class="text-primary"
                                       title="Edit">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>

                                    <a href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
                                       class="text-danger"
                                       title="Delete"
                                       onclick="return confirm('Are you sure you want to delete this role?');">
                                        <i class="bi bi-trash"></i>
                                    </a>

                                </td>

                            </tr>
                        </c:forEach>
                        </tbody>

                    </table>
                </div>
            </c:if>

        </div>

        <div class="card-footer text-center">

            <div class="d-flex justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/role/add" class="btn btn-success">
                    + Add New Role
                </a>
            </div>

            <div class="text-muted small mt-2">
                Role Management System
            </div>

        </div>

    </div>
</div>

</body>
</html>