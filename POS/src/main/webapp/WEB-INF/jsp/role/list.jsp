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
            margin: 0;
            min-height: 100vh;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
            padding: 30px;
        }

        .card {
            border: none;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            text-align: center;
            padding: 25px;
        }

        .card-header h5 {
            margin: 0;
            font-size: 2rem;
            font-weight: 600;
        }

        .card-body {
            background-color: #ffffff;
            padding: 30px;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #f8f9fa;
            font-weight: 600;
            font-size: 1rem;
            vertical-align: middle;
        }

        .table td {
            font-size: 0.95rem;
            vertical-align: middle;
        }

        .btn {
            border-radius: 8px;
            min-width: 90px;
        }

        .btn-warning {
            color: #ffffff;
        }

        .alert {
            border-radius: 8px;
        }

        .card-footer {
            background-color: #f8f9fa;
            text-align: center;
            padding: 15px;
            border-top: 1px solid #dee2e6;
        }

        .text-muted {
            margin-top: 8px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-10">

            <div class="card">

                <div class="card-header">
                    <h5>Role Management</h5>
                </div>

                <div class="card-body">
                    <c:if test="${empty roles}">
                        <div class="alert alert-warning text-center">
                            No roles found
                        </div>
                    </c:if>
                    <c:if test="${not empty roles}">
                        <div class="table-responsive">
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
                                        <td class="fw-semibold">${role.identifier}</td>
                                        <td>${role.description}</td>
                                        <td>
                                            <div class="d-flex justify-content-center gap-2">
                                                <a href="/role/get?identifier=${role.identifier}"
                                                   class="btn btn-warning btn-sm">
                                                    Update
                                                </a>
                                                <a href="/role/delete?identifier=${role.identifier}"
                                                   class="btn btn-danger btn-sm"
                                                   onclick="return confirm('Are you sure you want to delete this role?');">
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

                <div class="card-footer">
                    <div class="d-flex justify-content-center gap-3 mb-2">
                        <a href="/" class="btn btn-secondary"> Home </a>
                        <a href="/role/add" class="btn btn-primary"> Add New Role</a>
                    </div>
                    <div class="text-muted small">
                        POS Management System
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>