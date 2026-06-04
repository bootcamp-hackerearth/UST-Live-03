<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #dbeafe, #93c5fd);
            font-family: 'Poppins', sans-serif;
        }

        h3 {
            color: white;
            font-weight: 700;
        }

        .table-wrapper {
            background: rgba(255, 255, 255, 0.92);
            padding: 20px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
        }

        .table {
            margin-bottom: 0;
            background: transparent;
        }

        .table thead th {
            background: transparent;
            border-bottom: 1.5px solid rgb(217 217 217 / 60%) !important;
            font-weight: 600;
            color: #111;
        }

        .table tbody tr {
            transition: 0.2s ease;
        }

        .table tbody tr:hover {
            background: rgba(147, 197, 253, 0.15);
            transform: scale(1.01);
        }

        .table td,
        .table th {
            border: none !important;
        }

        .btn-primary {
            background: #3b82f6;
            border: none;
        }

        .btn-primary:hover {
            background: #2563eb;
        }

        .btn-danger {
            background: #ef4444;
            border: none;
        }

        .btn-danger:hover {
            background: #dc2626;
        }

        .btn-secondary {
            background: #94a3b8;
            border: none;
        }

        .btn-secondary:hover {
            background: #64748b;
        }

        .btn-success {
            background: #3b82f6;
            border: none;
        }

        .btn-success:hover {
            background: #2563eb;
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <h3 class="text-center mb-4">User Management</h3>

    <c:if test="${empty users}">
        <div class="alert alert-warning text-center">
            No users found
        </div>
    </c:if>

    <c:if test="${not empty users}">
        <div class="table-wrapper">
            <table class="table align-middle">
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
                        <td class="d-flex justify-content-center gap-2">
                            <a class="btn btn-primary btn-sm"
                               href="/user/get?username=${user.username}">
                                Edit
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

    <div class="d-flex justify-content-center gap-3 mt-4">
        <a href="/" class="btn btn-secondary">Home</a>
    </div>

</div>

</body>
</html>