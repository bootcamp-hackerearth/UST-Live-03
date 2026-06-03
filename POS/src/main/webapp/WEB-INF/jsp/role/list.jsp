<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Role List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body {
            background-color: #f1f3f6;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
        }

        .card {
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }

        .action-btns a {
            margin-right: 6px;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-sm">

                <div class="card-header text-center py-3">
                    <h5 class="mb-0">List of Roles</h5>
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
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="role" items="${roles}">
                                <tr>
                                    <td>${role.id}</td>

                                    <td class="fw-semibold">
                                        ${role.identifier}
                                    </td>
                                    <td>${role.description}</td>

                                    <td class="action-btns">
                                        <a href="/role/get?identifier=${role.identifier}"
                                           class="btn btn-sm btn-warning">
                                            Update
                                        </a>

                                        <a href="/role/delete?identifier=${role.identifier}"
                                           class="btn btn-sm btn-danger"
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

                <div class="card-footer bg-light text-center d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>
                    <a href="/role/add" class="btn btn-primary">
                        Add New Role
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>