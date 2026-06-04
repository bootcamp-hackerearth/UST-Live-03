<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 950px;
        }

        .card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        .card h3 {
            font-weight: 600;
            color: #333;
        }

        .table th {
            background-color: #f4f6fb;
            color: #555;
            border: none;
            font-weight: 600;
            text-align: center;
        }

        .table td {
            vertical-align: middle;
            text-align: center;
            border-color: #eee;
        }

        .edit-btn {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: #fff;
            border-radius: 20px;
            padding: 6px 16px;
            font-size: 0.85rem;
            border: none;
            transition: all 0.3s ease;
        }

        .edit-btn:hover {
            background: linear-gradient(90deg, #00c6ff, #0072ff);
            transform: translateY(-1px);
            color: #fff;
        }

        .btn-danger {
            border-radius: 20px;
            padding: 6px 16px;
            font-size: 0.85rem;
        }

        .card-footer {
            border-top: none;
            background: transparent;
        }

        .btn-success {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            border: none;
            border-radius: 25px;
            padding: 8px 24px;
        }

        .btn-secondary {
            border-radius: 25px;
            padding: 8px 24px;
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
                    <table class="table table-bordered table-hover align-middle">
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
                                    <a href="/user/get?username=${user.username}"
                                       class="btn edit-btn me-2">Edit</a>

                                    <a href="/user/delete?username=${user.username}"
                                       class="btn btn-danger"
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
            <div class="d-flex justify-content-center gap-3">
                <a href="/" class="btn btn-secondary">Home</a>
                <a href="/register" class="btn btn-success">Create New User</a>
            </div>

            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>