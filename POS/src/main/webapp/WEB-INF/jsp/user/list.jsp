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
            background: #ffffff;
            min-height: 100vh;
        }

        .card {
            border-radius: 15px;
        }

        .table th {
            background-color: #343a40;
            color: white;
        }

        /* Top navigation */
        .top-actions {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }

        .top-link {
            text-decoration: none;
            font-size: 18px;
            font-weight: 500;
            color: #333;
        }

        .top-link:hover {
            text-decoration: none;
            color: #000;
        }

        /* Action icons */
        .action-link {
            text-decoration: none;
            font-size: 18px;
            margin: 0 6px;
        }

        .action-link:hover {
            text-decoration: none;
            opacity: 0.8;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <!-- 🔙 Back Arrow + 🏠 Home -->
            <div class="top-actions">
                <a href="javascript:history.back()" class="top-link">← </a>
                <a href="/" class="btn btn-secondary btn-sm">Home</a>
            </div>

            <h3 class="text-center mb-4">User Management</h3>

            <!-- NO USERS MESSAGE -->
            <c:if test="${empty user}">
                <div class="alert alert-warning text-center">
                    No users found
                </div>
            </c:if>

            <c:if test="${not empty user}">
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
                        <c:forEach var="user" items="${user}">
                            <tr>
                                <td>${user.username}</td>
                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>
                                <td>
                                    <!-- ✏️ Edit -->
                                    <a href="/user/get?username=${user.username}"
                                       class="action-link">✏️</a>

                                    <!-- 🗑 Delete -->
                                    <a href="/user/delete?username=${user.username}"
                                       class="action-link"
                                       onclick="return confirm('Are you sure you want to delete this user?');">
                                        🗑
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
            <div class="text-muted small mt-2">
                User Management System
            </div>

    </div>
</div>

</body>
</html>
