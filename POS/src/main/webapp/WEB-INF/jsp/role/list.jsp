<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.08), transparent 40%),
                radial-gradient(circle at bottom left, rgba(111,66,193,0.08), transparent 40%);
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
        }

        .card {
            border-radius: 12px;
            border: none;
            background: #ffffff;
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: linear-gradient(135deg, #4e73df, #6f42c1);
            color: #ffffff;
            font-weight: 600;
            padding: 18px;
        }

        table th {
            background-color: #4e73df;
            color: white;
            font-size: 14px;
            font-weight: 600;   /* headers bold */
        }

        table td {
            font-size: 14px;
            color: #555;
            font-weight: 400;
        }

        .table-hover tbody tr:hover {
            background-color: #eef3ff;
        }

        .btn-warning {
            background-color: #4e73df;
            border: none;
            color: #fff;
        }

        .btn-warning:hover {
            background-color: #2e59d9;
        }

        .btn-danger {
            background-color: #e74a3b;
            border: none;
        }

        .btn-danger:hover {
            background-color: #c0392b;
        }

        .btn-success {
            background-color: #1cc88a;
            border: none;
        }

        .btn-success:hover {
            background-color: #17a673;
        }

        .btn-home {
            background-color: #4e73df;
            color: #ffffff;
            border: none;
            font-size: 15px;
            padding: 8px 18px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-home i {
            color: #ffffff;
            font-size: 16px;
        }

        .btn-home:hover {
            background-color: #2e59d9;
        }

        .card-footer {
            background-color: #f8f9fc;
            border-top: none;
            padding: 16px;
        }

        .alert-warning {
            background-color: #fff3cd;
            border: none;
            color: #856404;
            border-radius: 8px;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header text-center">
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
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="role" items="${roles}">
                                    <tr>
                                        <td>${role.id}</td>
                                        <td>${role.identifier}</td>
                                        <td>${role.description}</td>
                                        <td class="d-flex justify-content-center gap-2">
                                            <a href="/role/get?identifier=${role.identifier}"
                                               class="btn btn-warning btn-sm">
                                                Update
                                            </a>
                                            <a href="/role/delete?identifier=${role.identifier}"
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
                <div class="card-footer text-center d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-home">
                        <i class="bi bi-house-fill"></i>
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