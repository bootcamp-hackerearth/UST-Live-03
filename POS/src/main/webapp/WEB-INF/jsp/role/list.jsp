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
            background: #f8fafc;
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            border-radius: 16px;
            border: none;
        }

        .card-header {
            background: #0f172a;
            color: #e2e8f0;
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }

        table th {
            background-color: #1e293b;
            color: #e2e8f0;
        }

        table {
            background: white;
        }

        .btn-edit {
            background: #0f766e;
            color: white;
            border: none;
        }

        .btn-edit:hover {
            background: #0d5f59;
        }

        .btn-delete {
            background: #dc2626;
            color: white;
            border: none;
        }

        .btn-delete:hover {
            background: #b91c1c;
        }

        .btn-add {
            background: #16a34a;
            color: white;
            border: none;
        }

        .btn-add:hover {
            background: #15803d;
        }

        .btn-home {
            background: #334155;
            color: white;
            border: none;
        }

        .btn-home:hover {
            background: #1e293b;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-9">

            <div class="card shadow-lg">

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
                                <th>Action</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="role" items="${roles}">
                                <tr>
                                    <td>
                                        <a href="/role/get?identifier=${role.identifier}">
                                            ${role.id}
                                        </a>
                                    </td>

                                    <td>${role.identifier}</td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${empty role.description}">
                                                <span class="text-muted">—</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${role.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td class="d-flex justify-content-center gap-2">

                                        <a class="btn btn-sm btn-edit"
                                           href="${pageContext.request.contextPath}/role/get?identifier=${role.identifier}">
                                            Edit
                                        </a>

                                        <a class="btn btn-sm btn-delete"
                                           href="${pageContext.request.contextPath}/role/delete?identifier=${role.identifier}"
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

                    <a href="/" class="btn btn-home">Home</a>

                    <a href="/role/add" class="btn btn-add">
                        + Add New Role
                    </a>

                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>