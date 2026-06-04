<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

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

        .badge-role {
            background-color: #475569;
            color: #e2e8f0;
            margin: 2px;
            padding: 5px 8px;
            border-radius: 6px;
            font-size: 12px;
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
        <div class="col-md-10">

            <div class="card shadow-lg">

                <div class="card-header text-center">
                    <h4 class="mb-0">List of Nodes</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty nodes}">
                        <div class="alert alert-warning text-center">
                            No nodes found
                        </div>
                    </c:if>

                    <c:if test="${not empty nodes}">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Identifier</th>
                                <th>Path</th>
                                <th>Roles</th>
                                <th>Action</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="node" items="${nodes}">
                                <tr>
                                    <td>${node.id}</td>

                                    <td>${node.identifier}</td>

                                    <td>${node.path}</td>

                                    <td>
                                        <c:if test="${empty node.roles}">
                                            <span class="text-muted">No roles</span>
                                        </c:if>

                                        <c:forEach var="role" items="${node.roles}">
                                            <span class="badge-role">${role}</span>
                                        </c:forEach>
                                    </td>

                                    <td class="d-flex justify-content-center gap-2">

                                        <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                           class="btn btn-sm btn-edit">
                                            Edit
                                        </a>

                                        <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                           class="btn btn-sm btn-delete"
                                           onclick="return confirm('Are you sure you want to delete this node?');">
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

                    <a href="${pageContext.request.contextPath}/"
                       class="btn btn-home">
                        Home
                    </a>

                    <a href="${pageContext.request.contextPath}/node/add"
                       class="btn btn-add">
                        + Add New Node
                    </a>

                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>