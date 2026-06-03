<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Role List</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
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
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-9">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Nodes</h4>
                </div>

                <div class="card-body">

                    <!-- NO DATA -->
                    <c:if test="${empty nodes}">
                        <div class="alert alert-warning text-center">
                            No nodes found
                        </div>
                    </c:if>

                    <!-- TABLE -->
                    <c:if test="${not empty nodes}">
                        <table class="table table-bordered table-hover text-center align-middle">
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

                                    <!-- ID (EDIT NAVIGATION) -->
                                    <td>
                                        <a href="/node/get?identifier=${node.identifier}"
                                           class="text-decoration-none fw-semibold">
                                            ${node.id}
                                        </a>
                                    </td>

                                    <td>${node.identifier}</td>
                                    <td>${node.path}</td>
                                    <td>${node.roles}</td>

                                    <!-- ACTION COLUMN -->
                                    <td class="d-flex justify-content-center gap-2">

                                        <!-- EDIT -->
                                        <a href="/node/get?identifier=${node.identifier}"
                                           class="btn btn-warning btn-sm">
                                            Edit
                                        </a>

                                        <!-- DELETE -->
                                        <a href="/node/delete?identifier=${node.identifier}"
                                           class="btn btn-danger btn-sm"
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

                <!-- FOOTER -->
                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="/node/add" class="btn btn-success">
                        + Add New Node
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>