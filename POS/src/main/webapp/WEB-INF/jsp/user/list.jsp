<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

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
            vertical-align: middle;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.03em;
        }

        .action-btns a {
            margin-right: 6px;
        }

        .role-badge {
            font-size: 12px;
            margin: 2px;
        }

        a.user-link {
            text-decoration: none;
            font-weight: 500;
            color: #0d6efd;
        }

        a.user-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-people-fill me-2"></i> User Management
    </h4>

    <div>
        <a href="${pageContext.request.contextPath}/"
           class="btn btn-light fw-semibold me-2">
            <i class="bi bi-house-door-fill me-1"></i> Home
        </a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th>Email</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Roles</th>
                <th class="text-center">Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <!-- Email -->
                    <td>
                        <a class="user-link"
                           href="${pageContext.request.contextPath}/user/get?username=${user.username}">
                            ${user.username}
                        </a>
                    </td>

                    <td>${user.name}</td>

                    <td>${user.phoneNo}</td>

                    <td>
                        <c:forEach var="role" items="${user.roles}">
                            <span class="badge bg-secondary role-badge">
                                ${role}
                            </span>
                        </c:forEach>
                    </td>

                    <td class="text-center action-btns">
                        <a href="${pageContext.request.contextPath}/user/get?username=${user.username}"
                           class="btn btn-sm btn-outline-primary"
                           title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </a>

                        <a href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                           class="btn btn-sm btn-outline-danger"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this user?');">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty users}">
                <tr>
                    <td colspan="5" class="text-center text-muted py-4">
                        <i class="bi bi-info-circle me-1"></i>
                        No users found
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>