<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

        .card {
            border-radius: 12px;
            border: 1px solid #ddd;
            max-width: 480px;
            margin: 60px auto;
            box-shadow: 0 6px 18px rgba(0,0,0,.08);
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
            border-radius: 12px 12px 0 0;
        }

        .form-control {
            border-radius: 6px;
        }

        .form-label {
            font-weight: 500;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
        }

        .btn {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Add Role
    </div>

    <div class="card-body">

        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/role/add" method="post">

            <div class="mb-3">
                <label class="form-label">Role Identifier</label>
                <input type="text"
                       name="identifier"
                       value="${param.identifier}"
                       class="form-control ${not empty error && error.contains('already') ? 'input-error' : ''}"
                       placeholder="e.g. ADMIN, USER"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Description</label>
                <textarea name="description"
                          class="form-control"
                          rows="3"
                          placeholder="Describe role responsibilities">${param.description}</textarea>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Add Role
                </button>

                <a href="${pageContext.request.contextPath}/user/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </div>

    <div class="card-footer text-center text-muted small">
        Role Management System
    </div>

</div>

</body>
</html>
