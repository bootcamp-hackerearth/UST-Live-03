<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 420px;
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-radius: 12px 12px 0 0;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-sm">

    <div class="card-header text-center py-3">
        <h5 class="mb-0">Edit Role</h5>
    </div>

    <div class="card-body p-4">

        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <c:if test="${empty role}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty role}">

            <form:form action="/role/update" method="post" modelAttribute="role">

                <form:hidden path="id"/>
                <form:hidden path="identifier"/>

                <div class="mb-3">
                    <label class="form-label">Role Name</label>
                    <input
                        type="text"
                        class="form-control"
                        value="${role.identifier}"
                        readonly
                    >
                </div>

                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <form:input
                        path="description"
                        cssClass="form-control"
                        required="true"
                        minlength="3"
                        maxlength="100"
                        placeholder="Enter description"
                    />
                </div>

                <div class="d-flex justify-content-between mt-4">
                    <a href="/role/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-primary">
                        Update Role
                    </button>
                </div>

            </form:form>

        </c:if>

    </div>

    <div class="card-footer text-muted small text-center">
        POS Management System
    </div>

</div>

</body>
</html>