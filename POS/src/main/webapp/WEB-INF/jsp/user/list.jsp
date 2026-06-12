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
            background: linear-gradient(135deg, #0f766e, #022c43);
            min-height: 100vh;
        }
        .card {
            border-radius: 15px;
        }
        .table th {
            background-color: #343a40;
            color: white;
        }
        a.user-link {
            text-decoration: none;
            font-weight: 500;
        }
        a.user-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <h3 class="text-center mb-4">User Management</h3>

            <!-- NO USERS -->
            <c:if test="${empty users}">
                <div class="alert alert-warning text-center">
                    No users found
                </div>
            </c:if>

            <!-- USERS TABLE -->
            <c:if test="${not empty users}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">
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

                                <!-- Roles -->
                                <td>
                                    <c:forEach var="role" items="${user.roles}" varStatus="status">
                                        ${role}
                                        <c:if test="${!status.last}">, </c:if>
                                    </c:forEach>
                                </td>

                                <!-- Actions -->
                                <td>
                                    <div class="d-flex justify-content-center gap-2">
                                        <a class="btn btn-warning btn-sm"
                                           href="${pageContext.request.contextPath}/user/get?username=${user.username}">
                                            Edit
                                        </a>

                                        <a class="btn btn-danger btn-sm"
                                           href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
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

        <!-- FOOTER -->
        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-success">
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