<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Nodes</title>

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
            margin-right: 5px;
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <div class="card shadow-sm">

        <div class="card-header text-center py-3">
            <h5 class="mb-0">List of Nodes</h5>
        </div>

        <div class="card-body">

            <c:if test="${empty node}">
                <div class="alert alert-warning text-center">
                    No nodes found
                </div>
            </c:if>

            <c:if test="${not empty node}">
                <table class="table table-bordered table-hover text-center align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Node Name</th>
                        <th>Path</th>
                        <th>Roles</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="node" items="${node}">
                        <tr>
                            <td>${node.id}</td>
                            <td class="fw-semibold">${node.identifier}</td>
                            <td>${node.path}</td>
                            <td>${node.roles}</td>
                            <td class="action-btns">

                                <a href="/node/get?identifier=${node.identifier}"
                                   class="btn btn-sm btn-warning">
                                    Update
                                </a>

                                <a href="/node/delete?identifier=${node.identifier}"
                                   class="btn btn-sm btn-danger"
                                   onclick="return confirm('Are you sure you want to delete this node?');">
                                    Delete
                                </a>

                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <div class="d-flex justify-content-center gap-3 mt-4">
                <a href="/" class="btn btn-secondary">
                    Home
                </a>

                <a href="/node/add" class="btn btn-primary">
                    Add New Node
                </a>
            </div>
        </div>
        <div class="card-footer text-muted small text-center">
            POS Management System
        </div>

    </div>

</div>

</body>
</html>