<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background: #F6F7F9;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
            background: #FFFFFF;
            border: 1px solid #E5E7EB;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
        }

        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
            background: #2B2B2B;
        }

        .card-header h4 {
            color: #FFFFFF;
            font-weight: 600;
        }

        table th {
            background: #2B2B2B !important;
            color: white !important;
        }

        table td {
            vertical-align: middle;
            color: #111827;
        }

        .msg {
            color: #166534;
            background: #DCFCE7;
            padding: 10px 14px;
            border-radius: 10px;
            text-align: center;
            width: fit-content;
            margin: 0 auto 16px auto;
            font-size: 14px;
            font-weight: 500;
        }

        .btn-danger {
            background: #B91C1C;
            border: none;
        }

        .btn-danger:hover {
            background: #7F1D1D;
        }

        .btn-success {
            background: #2B2B2B;
            border: none;
        }

        .btn-success:hover {
            background: #111111;
        }

        .btn-secondary {
            border-radius: 8px;
        }

        .card-footer {
            background: #F9FAFB;
            border-bottom-left-radius: 16px;
            border-bottom-right-radius: 16px;
        }
        .edit-btn {
            background: #2B2B2B;
            color: white;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="card">

                <div class="card-header text-center">
                    <h4 class="mb-0">List of Roles</h4>
                </div>

                <c:if test="${not empty message}">
                    <div class="msg">${message}</div>
                </c:if>

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
                                    <th>Action</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="role" items="${roles}">
                                    <tr>
                                        <td>${role.id}</td>
                                        <td>${role.identifier}</td>
                                        <td>${role.description}</td>
                                        <td>
                                            <a href="/role/get?identifier=${role.identifier}">
                                               <button class="btn edit-btn">Edit</button>
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
                    <a href="/" class="btn btn-secondary">Home</a>
                    <a href="/role/add" class="btn btn-success">+ Add New Role</a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>
