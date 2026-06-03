<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #f1f3f6;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
        }

        .card {
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        table th {
            background-color: #f8f9fa;
            font-weight: 600;
            font-size: 0.95rem;
        }

        table td {
            font-size: 0.9rem;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card shadow-sm">
                <div class="card-header text-center py-3">
                    <h5 class="mb-0">User Management</h5>
                </div>

                <div class="card-body">

                    <c:if test="${empty users}">
                        <div class="alert alert-warning text-center">
                            No users found
                        </div>
                    </c:if>

                    <c:if test="${not empty users}">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover text-center align-middle">
                                <thead>
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
                                        <td>${user.username}</td>
                                        <td>${user.name}</td>
                                        <td>${user.phoneNo}</td>
                                        <td>${user.roles}</td>

                                        <td>
                                            <div class="d-flex justify-content-center gap-2">
                                                <a class="btn btn-sm btn-warning"
                                                   href="/user/get?username=${user.username}">
                                                    Edit
                                                </a>

                                                <a class="btn btn-sm btn-danger"
                                                   href="/user/delete?username=${user.username}"
                                                   onclick="return confirm('Are you sure you want to delete this user?');">
                                                    Delete
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>

                </div>

                <div class="card-footer bg-light text-center">

                    <a href="/" class="btn btn-secondary mb-2">
                        Home
                    </a>

                    <div class="text-muted small">
                        POS Management System
                    </div>

                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>