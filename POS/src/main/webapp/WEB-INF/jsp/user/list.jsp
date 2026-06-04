<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.12), transparent 45%),
                radial-gradient(circle at bottom left, rgba(111,66,193,0.12), transparent 45%);
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
        }

        .card {
            border-radius: 12px;
            border: none;
            background: #ffffff;
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
        }

        h3 {
            font-weight: 600;
            color: #4e73df;
        }

        .table {
            border-radius: 10px;
            overflow: hidden;
        }

        .table th {
            background-color: #4e73df;
            color: white;
            font-weight: 600;
            font-size: 14px;
        }

        .table td {
            font-size: 14px;
            color: #555;
            font-weight: 400;
        }

        .table-hover tbody tr:hover {
            background-color: #eef3ff;
        }

        /* BUTTONS */
        .btn-primary {
            background-color: #4e73df;
            border: none;
        }

        .btn-primary:hover {
            background-color: #2e59d9;
        }

        .btn-danger {
            background-color: #e74a3b;
            border: none;
        }

        .btn-danger:hover {
            background-color: #c0392b;
        }

        .btn-home {
            background-color: #4e73df;
            border: none;
            font-size: 15px;
            padding: 9px 20px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: #ffffff;
        }

        .btn-home i {
            color: #ffffff;
        }

        .btn-home:hover {
            background-color: #2e59d9;
        }

        .alert-warning {
            background-color: #fff3cd;
            border: none;
            color: #856404;
            border-radius: 8px;
        }

        .card-footer {
            background-color: #f8f9fc;
            border-top: none;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <div class="card shadow-lg">
        <div class="card-body">
            <h3 class="text-center mb-4">User Management</h3>
            <div class="mb-3 text-start">
                <a href="/" class="btn btn-home btn-sm">
                    <i class="bi bi-house-fill"></i>
                    Home
                </a>
            </div>
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
                                <td>${user.username}</td>
                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>
                                <td>
                                    <a class="btn btn-primary btn-sm me-1"
                                       href="/user/get?username=${user.username}">
                                        Update
                                    </a>
                                    <a class="btn btn-danger btn-sm"
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
        <div class="card-footer text-center">
            <div class="text-muted small">
                User Management System
            </div>
        </div>
    </div>
</div>
</body>
</html>