<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Update Category</title>

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
    .form-control {
        border-radius: 10px;
    }
    .btn {
        border-radius: 10px;
    }
</style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Category Management</span>
        <a href="/category/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">

    <div class="card shadow p-4" style="width: 500px;">

        <h3 class="text-center mb-4 fw-bold">Update Category</h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="/category/update" method="post">

            <div class="mb-3">
                <label class="form-label">Identifier</label>
                <input type="text"
                       name="identifier"
                       class="form-control"
                       value="${category.identifier}"
                       readonly>
            </div>

            <div class="mb-3">
                <label class="form-label">Name</label>
                <input type="text"
                       name="name"
                       class="form-control"
                       value="${category.name}"
                       readonly>
            </div>

            <div class="mb-4">
                <label class="form-label">Super Category</label>

                <select name="superCategoryIdentifier" class="form-control">
                    <option value="">-- None (Top Level) --</option>

                    <c:forEach items="${categories}" var="c">
                        <option value="${c.identifier}"
                            ${c.identifier == category.superCategoryIdentifier ? 'selected' : ''}>
                            ${c.name} (${c.identifier})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">
                    Update
                </button>

                <a href="/category/list" class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>

        </form>

    </div>
</div>

</body>
</html>