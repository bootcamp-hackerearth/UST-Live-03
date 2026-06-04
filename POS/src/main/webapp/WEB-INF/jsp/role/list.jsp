<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #ffffff;
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
        .status-label {
            font-size: 0.85rem;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-9">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Roles</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty roles}">
                        <div class="alert alert-warning text-center">
                            No roles found
                        </div>
                    </c:if>

                    <c:if test="${not empty roles}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="role" items="${roles}">
                                <tr>
                                    <td>${role.id}</td>
                                    <td>${role.identifier}</td>

                                    <td>
                                        <form method="get"
                                              action="${pageContext.request.contextPath}/role/toggle">

                                            <input type="hidden" name="identifier"
                                                   value="${role.identifier}" />
                                            <input type="hidden" name="status"
                                                   value="${!role.status}" />

                                            <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                                <input class="form-check-input"
                                                       type="checkbox"
                                                       ${role.status ? "checked" : ""}
                                                       onchange="this.form.submit()" />

                                                <span class="status-label ${role.status ? 'text-success' : 'text-danger'}">
                                                    ${role.status ? 'Active' : 'Inactive'}
                                                </span>
                                            </div>
                                        </form>
                                    </td>

                                    <td class="d-flex justify-content-center gap-2">
                                        <a href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}"
                                           class="btn btn-warning btn-sm">
                                            Edit
                                        </a>

                                        <a href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this role?');">
                                            Delete
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="${pageContext.request.contextPath}/role/add" class="btn btn-success">
                        + Add New Role
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>