<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #f8fafc;
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            border-radius: 16px;
            border: none;
        }

        .card-header {
            background: #0f172a;
            color: #e2e8f0;
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }

        .table th {
            background-color: #1e293b;
            color: #e2e8f0;
        }

        table {
            background: white;
        }

        a.user-link {
            text-decoration: none;
            font-weight: 500;
            color: #0f766e;
        }

        a.user-link:hover {
            text-decoration: underline;
        }

        .btn-edit {
            background: #0f766e;
            color: white;
            border: none;
        }

        .btn-edit:hover {
            background: #0d5f59;
        }

        .btn-delete {
            background: #dc2626;
            color: white;
            border: none;
        }

        .btn-delete:hover {
            background: #b91c1c;
        }

        .btn-home {
            background: #334155;
            color: white;
            border: none;
        }

        .btn-home:hover {
            background: #1e293b;
        }

        .btn-add {
            background: #16a34a;
            color: white;
            border: none;
        }

        .btn-add:hover {
            background: #15803d;
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <div class="card shadow-lg">

        <div class="card-header text-center">
            <h3 class="mb-0">User Management</h3>
        </div>

        <div class="card-body">

            <c:if test="${empty users}">
                <div class="alert alert-warning text-center">
                    No users found
                </div>
            </c:if>

            <c:if test="${not empty users}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
                        <thead>
                        <tr>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Phone</th>
                            <th>Roles</th>
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>
                                    <a class="user-link"
                                       href="/user/get?username=${user.username}">
                                        ${user.username}
                                    </a>
                                </td>

                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>

                                <td>

                                    <a class="btn btn-sm btn-edit me-2"
                                       href="/user/get?username=${user.username}">
                                        Edit
                                    </a>

                                    <a class="btn btn-sm btn-delete"
                                       href="/user/delete?username=${user.username}"
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

        <div class="card-footer text-center bg-light">
            <div class="d-flex justify-content-center gap-3">

                <a href="/" class="btn btn-home">Home</a>

            </div>

            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>

    </div>

</div>

</body>
</html>
