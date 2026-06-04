<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Unit</title>

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

        .form-switch .form-check-input {
            width: 45px;
            height: 22px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Unit Management</span>
        <a href="${pageContext.request.contextPath}/unit/list"
           class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 500px;">
        <h3 class="text-center mb-4 fw-bold">Add Unit</h3>
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/unit/add" method="post">

            <div class="mb-3">
                <label class="form-label">Unit Name</label>
                <input type="text"
                       name="unitName"
                       class="form-control"
                       placeholder="Enter unit name"
                       required>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Status</label>

                <div class="form-check form-switch">
                    <input class="form-check-input"
                           type="checkbox"
                           name="status"
                           value="true"
                           id="status"
                           checked>

                    <label class="form-check-label" for="status">
                        Active / Inactive
                    </label>
                </div>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">
                    Save
                </button>

                <a href="${pageContext.request.contextPath}/unit/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>