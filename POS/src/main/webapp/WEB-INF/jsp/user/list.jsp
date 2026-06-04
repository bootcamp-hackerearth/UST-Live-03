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
            background-color: #f4f6f9;
            min-height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        }

        .page-container {
            margin-top: 40px;
        }

        .card {
            border: none;
            border-radius: 15px;
            overflow: hidden;
        }

        .card-header {
            background: #0d6efd;
            color: white;
            padding: 18px 25px;
        }

        .card-header h3 {
            margin: 0;
            font-size: 24px;
            font-weight: 600;
        }

        .home-btn {
            font-weight: 500;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #343a40;
            color: white;
            font-weight: 600;
            border: none;
        }

        .table td {
            vertical-align: middle;
        }

        .table-hover tbody tr:hover {
            background-color: #f8f9fa;
        }

        .btn-action {
            min-width: 80px;
            margin: 2px;
        }

        .card-footer {
            background: #fff;
            border-top: 1px solid #dee2e6;
        }

        .footer-text {
            color: #6c757d;
            font-size: 14px;
        }

        .empty-message {
            margin: 20px;
        }

    </style>
</head>

<body>

<div class="container page-container">

    <div class="card shadow">

        <!-- Header -->
        <div class="card-header">
            <div class="d-flex justify-content-between align-items-center">

                <h3>User Management</h3>

                <!-- Home Button -->
                <a href="/" class="btn btn-light home-btn">
                    🏠 Home
                </a>

            </div>
        </div>

        <!-- Body -->
        <div class="card-body">

            <c:if test="${empty users}">
                <div class="alert alert-warning text-center empty-message">
                    No users found.
                </div>
            </c:if>

            <c:if test="${not empty users}">

                <div class="table-responsive">

                    <table class="table table-bordered table-hover align-middle text-center">

                        <thead>
                        <tr>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Phone Number</th>
                            <th>Roles</th>
                            <th width="180">Actions</th>
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

                                    <a href="/user/get?username=${user.username}"
                                       class="btn btn-sm btn-primary btn-action">
                                        Edit
                                    </a>

                                    <a href="/user/delete?username=${user.username}"
                                       class="btn btn-sm btn-danger btn-action"
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

        <!-- Footer -->
        <div class="card-footer text-center">
            <span class="footer-text">
                User Management System
            </span>
        </div>

    </div>

</div>

</body>
</html>