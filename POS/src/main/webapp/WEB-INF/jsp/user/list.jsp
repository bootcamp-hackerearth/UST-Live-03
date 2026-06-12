<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Management</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background-color: #FFF8F0;
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
            padding-top: 40px;
        }

        .card {
            border-radius: 16px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
            border: none;
        }

        .card-body {
            padding: 30px;
        }

        h3 {
            color: #4B2E2B;
            font-weight: 600;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border-color: #4B2E2B;
            font-weight: 600;
        }

        .table td {
            vertical-align: middle;
        }

        .table-hover tbody tr:hover {
            background-color: #fff3eb;
        }

        a.user-link {
            text-decoration: none;
            font-weight: 600;
            color: #4B2E2B;
        }

        a.user-link:hover {
            text-decoration: underline;
            color: #3a2421;
        }

        .btn-danger {
            background-color: #8d3c36;
            border: none;
        }

        .btn-danger:hover {
            background-color: #702f2a;
        }

        .btn-secondary {
            background-color: #4B2E2B;
            border: none;
        }

        .btn-secondary:hover {
            background-color: #3a2421;
        }

        .card-footer {
            background-color: #fff3eb;
            border-top: none;
        }

        .text-muted {
            color: #6b4a46 !important;
        }

        .btn-edit {
            background-color: #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 6px 14px;
            font-size: 13px;
            font-weight: 600;
            border-radius: 6px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-edit:hover {
            background-color: #3a2421;
            color: #FFF8F0;
        }

        .btn-icon {
            width: 36px;
            height: 30px;
            padding: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 6px;
        }

        .register-btn {
            background-color:  #4B2E2B;
            color: #FFF8F0;
            border: none;
            padding: 8px 18px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 8px;
            text-decoration: none;
            display: inline-block;
        }

        .btn-delete {
            background-color: #8d3c36;
            color: #FFF8F0;
        }

        .btn-delete:hover {
            background-color: #702f2a;
            color: #FFF8F0;
        }

        .register-btn:hover {
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn-icon i {
            font-size: 13px;
            color: #FFF8F0;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <div class="card-body">
            <h3 class="text-center mb-4">User Management</h3>
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
                            <th>Edit</th>
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
                                    <a href="/user/get?identifier=${user.identifier}"
                                       class="btn-edit btn-icon"
                                       title="Edit User">
                                       <i class="fa-solid fa-pen"></i>
                                    </a>
                                </td>
                                <td>
                                    <a href="/user/delete?identifier=${user.identifier}"
                                       class="btn-delete btn-icon"
                                       title="Delete User"
                                       onclick="return confirm('Are you sure you want to delete this user?');">
                                       <i class="fa-solid fa-trash"></i>
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
                <a href="/" class="btn btn-secondary">
                    Home
                </a>
                <a href="/register" class="register-btn">
                            Register
                </a>
            </div>
            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>
    </div>
</div>
</body>
</html>
