<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Category List</title>

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
        <span class="navbar-brand fw-bold">Category Management</span>

        <div class="d-flex gap-2">
            <a href="/" class="btn btn-outline-light btn-sm">Home</a>
            <a href="/category/add" class="btn btn-light btn-sm fw-semibold">+ Add Category</a>
        </div>
    </div>
</nav>

<div class="container mt-5">

    <div class="card shadow p-3">

        <h3 class="fw-bold mb-3 text-center">Category List</h3>

        <div class="table-responsive">

            <table class="table table-hover table-striped mb-0">

                <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Identifier</th>
                        <th>Name</th>
                        <th>Super Category</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>

                <tbody>

                <c:if test="${empty categories}">
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">
                            No categories found.
                        </td>
                    </tr>
                </c:if>

                <c:forEach items="${categories}" var="cat">
                    <tr>
                        <td>${cat.id}</td>
                        <td class="fw-semibold">
                            ${cat.identifier}
                        </td>
                        <td>
                            ${cat.name}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${empty cat.superCategoryIdentifier}">
                                    <span class="text-secondary">-- None --</span>
                                </c:when>
                                <c:otherwise>
                                    ${cat.superCategoryIdentifier}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <a href="/category/get?identifier=${cat.identifier}"
                               class="btn btn-sm btn-outline-primary me-2">
                                Update
                            </a>
                            <a href="/category/delete?identifier=${cat.identifier}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Are you sure you want to delete this category?');">
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