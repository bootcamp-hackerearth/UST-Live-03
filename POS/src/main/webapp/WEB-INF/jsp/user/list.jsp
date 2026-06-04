<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .btn {
            border-radius: 10px;
        }
        .table th, .table td {
            vertical-align: middle;
        }
        a.user-link {
            text-decoration: none;
            font-weight: 600;
        }
        a.user-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">User Management</span>

        <div class="d-flex gap-2">
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">
        <h3 class="fw-bold mb-3 text-center">User List</h3>

        <c:if test="${empty users}">
            <div class="text-center py-4 text-light">
                No users found
            </div>
        </c:if>

        <c:if test="${not empty users}">
            <div class="table-responsive">
                <table class="table table-hover table-striped mb-0 text-center">

                    <thead class="table-dark">
                    <tr>
                        <th>Email</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Roles</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>

                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>
                                    ${user.username}
                                </a>
                            </td>
                            <td>${user.name}</td>
                            <td class="text-muted">${user.phoneNo}</td>
                            <td>
                                <c:forEach var="role" items="${user.roles}">
                                    <span class="badge bg-secondary me-1">
                                        ${role}
                                    </span>
                                </c:forEach>
                            </td>

                            <td>
                                <a href="/user/get?username=${user.username}"
                                   class="btn btn-sm btn-outline-primary me-2">
                                    Edit
                                </a>

                                <a href="/user/delete?username=${user.username}"
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('Are you sure you want to delete this user?');">
                                    Delete
                                </a>
                            </td>
                        </tr>
                    </c:forEach>

                    </tbody>

                </table>
            </div>
        </c:if>

    </div>

</div>

</body>
</html>
