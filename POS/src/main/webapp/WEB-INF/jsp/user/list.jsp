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

        a.user-link {
            text-decoration: none;
            font-weight: 500;
        }

        a.user-link:hover {
            text-decoration: underline;
        }

        .status-label {
            font-size: 0.85rem;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <h3 class="text-center mb-4">User Management</h3>

            <c:if test="${param.error == 'loggedInUser'}">
                <div class="alert alert-danger text-center">
                    Cannot disable or delete the currently logged‑in user
                </div>
            </c:if>

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
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>

                                <td>
                                    <a class="user-link"
                                       href="${pageContext.request.contextPath}/user/get?username=${user.username}">
                                        ${user.username}
                                    </a>
                                </td>

                                <td>${user.name}</td>
                                <td>${user.phoneNo}</td>
                                <td>${user.roles}</td>

                                <td>
                                    <form method="get"
                                          action="${pageContext.request.contextPath}/user/toggle"
                                          class="d-inline">

                                        <input type="hidden" name="id" value="${user.id}">
                                        <input type="hidden" name="status" value="${!user.status}">

                                        <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                            <input class="form-check-input"
                                                   type="checkbox"
                                                   ${user.status ? "checked" : ""}
                                                   onchange="this.form.submit()">

                                            <span class="status-label ${user.status ? 'text-success' : 'text-danger'}">
                                                ${user.status ? 'Active' : 'Inactive'}
                                            </span>
                                        </div>
                                    </form>
                                </td>

                                <td class="d-flex justify-content-center gap-2">

                                    <a class="btn btn-primary btn-sm"
                                       href="${pageContext.request.contextPath}/user/get?username=${user.username}">
                                        Edit
                                    </a>

                                    <a class="btn btn-danger btn-sm"
                                       href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
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
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Home</a>
                <a href="${pageContext.request.contextPath}/register" class="btn btn-success">Register</a>
            </div>

            <div class="text-muted small mt-2">
                User Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>