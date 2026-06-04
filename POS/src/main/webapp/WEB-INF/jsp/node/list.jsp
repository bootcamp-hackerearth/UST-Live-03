<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <!-- Bootstrap CSS -->
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

        .role-badge {
            font-size: 12px;
            margin: 2px;
        }

        /* ✅ DULL ICON STYLES */
        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #6c757d; /* dull gray */
        }

        .action-icon:hover {
            color: #495057; /* slightly darker on hover */
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-10">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Nodes</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty nodes}">
                        <div class="alert alert-warning text-center">
                            No nodes found
                        </div>
                    </c:if>

                    <c:if test="${not empty nodes}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Node</th>
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
                                            <span class="badge bg-secondary role-badge">
                                                ${role}
                                            </span>
                                        </c:forEach>
                                    </td>

                                    <!-- ✅ DULL ICON ACTIONS -->
                                    <td>
                                        <!-- ✏️ Edit -->
                                        <a href="/node/get?identifier=${node.identifier}"
                                           class="action-icon"
                                           title="Edit">
                                            ✏️
                                        </a>

                                        <!-- 🗑 Delete -->
                                        <a href="/node/delete?identifier=${node.identifier}"
                                           class="action-icon"
                                           title="Delete"
                                           onclick="return confirm('Are you sure you want to delete this node?');">
                                            🗑
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="/" class="btn btn-secondary">Home</a>
                    <a href="/node/add" class="btn btn-success">+ Add New Node</a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>