<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            min-height: 100vh;
        }

        .card {
            border-radius: 20px;
            border: 1px solid rgba(148,163,184,0.2);

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(12px);

            box-shadow: 0 20px 40px rgba(0,0,0,0.08);
        }

        .card-body {
            padding: 25px;
        }

        h3 {
            color: #111827;
            font-weight: 700;
        }

        .table {
            margin-top: 15px;
            border-radius: 10px;
            overflow: hidden;
            color: #111827;
        }

        .table th {
            background: #e5e7eb !important;
            color: #111827 !important;
            font-weight: 700;
            border: 1px solid #d1d5db !important;
        }

        .table td {
            background: #f9fafb !important;
            color: #111827 !important;
            border: 1px solid #e5e7eb !important;
        }

        .table tbody tr:hover td {
            background: #eef2ff !important;
        }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.2s ease;
            color: white;
            font-size: 14px;
        }

        .icon-btn:hover {
            transform: translateY(-2px);
        }

        .edit-btn {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
        }

        .delete-btn {
            background: linear-gradient(135deg, #ef4444, #f43f5e);
        }

        .card-footer {
            background: transparent;
            border-top: 1px solid #e5e7eb;
        }

        .btn-secondary {
            background: #e5e7eb;
            border: none;
            color: #111827;
        }

        .btn-success {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            border: none;
        }

        .btn-success:hover {
            transform: translateY(-2px);
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <h3 class="text-center mb-4">User Management</h3>

            <c:if test="${empty users}">
                <div class="alert alert-warning text-center">
                    No users found
                </div>
            </c:if>

            <c:if test="${not empty users}">
                <div class="table-responsive">
                    <table class="table table-bordered text-center align-middle">

                        <thead>
                        <tr>
                            <th>Id</th>
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

                                <td>${user.id}</td>

                                <!-- EMAIL (NO HYPERLINK) -->
                                <td>
                                    ${user.username}
                                </td>

                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>

                                <td class="d-flex justify-content-center gap-2">

                                    <!-- EDIT ICON -->
                                    <a href="/user/get?username=${user.username}"
                                       class="icon-btn edit-btn"
                                       title="Edit">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>

                                    <!-- DELETE ICON -->
                                    <a href="/user/delete?username=${user.username}"
                                       class="icon-btn delete-btn"
                                       onclick="return confirm('Are you sure?');"
                                       title="Delete">
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
                <a href="/" class="btn btn-secondary">Home</a>
                <a href="/register" class="btn btn-success">Register</a>
            </div>

            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>

    </div>
</div>
</body>
</html>