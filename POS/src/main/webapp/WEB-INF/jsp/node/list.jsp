<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Node List</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
    body {
        background-color: #E9EEF5;
        min-height: 100vh;
    }

    .card {
        border-radius: 16px;
    }

    .btn {
        border-radius: 10px;
    }

    .table th, .table td {
        vertical-align: middle;
    }
</style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Node Management</span>

        <div class="d-flex gap-2">
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>
            <a href="/node/add" class="btn btn-light btn-sm fw-semibold">+ Add Node</a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="card shadow p-3">
        <h3 class="fw-bold mb-3 text-center">Node List</h3>
        <div class="table-responsive">
            <table class="table table-hover table-striped mb-0">

                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Identifier</th>
                    <th>Path</th>
                    <th>Roles</th>
                    <th class="text-center">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:if test="${empty nodes}">
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">
                            No nodes found.
                        </td>
                    </tr>
                </c:if>

                <c:forEach items="${nodes}" var="node">
                    <tr>
                        <td>${node.id}</td>
                        <td>
                            <a href="/node/get?identifier=${node.identifier}"
                               class="fw-semibold text-decoration-none">
                                ${node.identifier}
                            </a>
                        </td>

                        <td class="text-muted">
                            ${node.path}
                        </td>

                        <td>
                            <c:forEach items="${node.roles}" var="role" varStatus="s">
                                ${role}<c:if test="${!s.last}">, </c:if>
                            </c:forEach>
                        </td>

                        <td class="text-center">
                            <a href="/node/get?identifier=${node.identifier}"
                               class="btn btn-sm btn-outline-primary me-2">
                                Update
                            </a>

                            <a href="/node/delete?identifier=${node.identifier}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Are you sure you want to delete this node?');">
                                Delete
                            </a>
                        </td>

                    </tr>
                </c:forEach>

                </tbody>

            </table>

        </div>

    </div>

</div>

</body>
</html>
