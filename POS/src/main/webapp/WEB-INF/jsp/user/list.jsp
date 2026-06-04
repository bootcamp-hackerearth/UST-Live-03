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
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }

        .page-container {
            max-width: 1300px;
            margin: 50px auto;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .page-title {
            font-size: 38px;
            font-weight: 700;
            color: #2c3e50;
            margin: 0;
        }

        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 25px;
        }

        .card-header h4 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #f8f9fa;
            color: #495057;
            font-weight: 600;
            text-align: center;
            border-bottom: 2px solid #dee2e6;
        }

        .table td {
            text-align: center;
            vertical-align: middle;
        }

        .table tbody tr:hover {
            background-color: #f8f9fa;
        }

        .card-footer {
            background: white;
            border-top: 1px solid #e9ecef;
            padding: 20px;
        }

        .btn-add {
            padding: 10px 20px;
            font-weight: 500;
        }

        .btn-back {
            min-width: 100px;
        }

        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 8px;
        }

        .empty-box {
            padding: 50px;
            text-align: center;
            color: #6c757d;
            font-size: 18px;
        }

        .role-badge {
            background-color: #e7f1ff;
            color: #0d6efd;
            border: 1px solid #b6d4fe;
            font-weight: 500;
            padding: 6px 10px;
            border-radius: 20px;
            display: inline-block;
            margin: 2px;
        }

        .muted-text {
            color: #6c757d;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="container-fluid">

    <div class="page-container">

        <div class="page-header">

            <h1 class="page-title">
                User Management
            </h1>
        </div>
        <div class="card">

            <div class="card-header">
                <h4>User List</h4>
            </div>

            <div class="card-body p-0">

                <c:if test="${empty users}">
                    <div class="empty-box">
                        No Users Found
                    </div>
                </c:if>

                <c:if test="${not empty users}">
                    <div class="table-responsive">
                        <table class="table table-hover">

                            <thead>
                            <tr>
                                <th style="width: 25%">Email</th>
                                <th style="width: 20%">Name</th>
                                <th style="width: 15%">Phone</th>
                                <th style="width: 20%">Roles</th>
                                <th style="width: 20%">Actions</th>
                            </tr>
                            </thead>

                            <tbody>

                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td>${user.username}</td>

                                    <td>${user.name}</td>

                                    <td>${user.phoneNo}</td>

                                    <td>
                                        <c:if test="${not empty user.roles}">
                                            <c:forEach var="role" items="${user.roles}">
                                                <span class="role-badge">
                                                    ${role}
                                                </span>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${empty user.roles}">
                                            <span class="muted-text">No Roles</span>
                                        </c:if>
                                    </td>

                                    <td>
                                        <div class="action-buttons">

                                            <a href="/user/get?username=${user.username}"
                                               class="btn btn-outline-primary btn-sm">
                                                Edit
                                            </a>

                                            <a href="/user/delete?username=${user.username}"
                                               class="btn btn-outline-danger btn-sm"
                                               onclick="return confirm('Are you sure you want to delete ${user.name}?');">
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

            <div class="card-footer d-flex justify-content-between align-items-center">

                <a href="/" class="btn btn-secondary btn-back">
                    Back
                </a>

                <span class="muted-text">
                    User Management System
                </span>

            </div>

        </div>

    </div>

</div>

</body>
</html>