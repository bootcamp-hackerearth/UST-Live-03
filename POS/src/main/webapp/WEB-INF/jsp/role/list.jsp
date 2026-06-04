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
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
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

        h4 {
            background-color: #ffffff;
        }

        .btn-pos-update {
            background-color: #4b6cb7;
            border-color: #4b6cb7;
            color: #fff;
        }

        .btn-pos-update:hover {
            background-color: #3f5fa7;
            border-color: #3f5fa7;
            color: #fff;
        }

        .btn-pos-delete {
            background-color: #f5f7fa;
            border: 1px solid #dc3545;
            color: #dc3545;
        }

        .btn-pos-delete:hover {
            background-color: #dc3545;
            color: #fff;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card shadow-lg">
                <div class="card-header text-black text-center">
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
                                    <th>Description</th>
                                    <th>Delete</th>
                                    <th>Update</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="role" items="${roles}">
                                    <tr>
                                        <td>
                                            <a href="/role/get?identifier=${role.identifier}"
                                               class="text-decoration-none fw-semibold">
                                                ${role.id}
                                            </a>
                                        </td>

                                        <td>${role.identifier}</td>
                                        <td>${role.description}</td>

                                        <td>
                                            <a href="/role/delete?identifier=${role.identifier}"
                                               class="btn btn-pos-delete btn-sm"
                                               onclick="return confirm('Are you sure you want to delete this role?');">
                                                Delete
                                            </a>
                                        </td>

                                        <td>
                                            <a class="btn btn-pos-update btn-sm"
                                               href="/role/get?identifier=${role.identifier}">
                                                Update
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="/role/add" class="btn btn-success">
                        + Add New Role
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>
