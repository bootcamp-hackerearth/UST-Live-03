<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"rel="stylesheet">

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
        <span class="navbar-brand fw-bold">Role Management</span>
        <a href="/role/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">

        <h3 class="text-center mb-4 fw-bold">Edit Role</h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <c:if test="${empty roleDto}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty roleDto}">
            <form action="/role/update" method="post">

                <input type="hidden" name="id" value="${roleDto.id}"/>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Identifier</label>
                    <input type="text"
                           class="form-control"
                           name="identifier"
                           value="${roleDto.identifier}"
                           readonly>
                </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Description</label>
                    <input type="text"
                           class="form-control"
                           name="description"
                           value="${roleDto.description}"
                           placeholder="Enter role description"
                           required>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100">
                        Update
                    </button>

                    <a href="/role/list" class="btn btn-outline-secondary w-100">
                        Cancel
                    </a>
                </div>

            </form>
        </c:if>

    </div>
</div>

</body>
</html>