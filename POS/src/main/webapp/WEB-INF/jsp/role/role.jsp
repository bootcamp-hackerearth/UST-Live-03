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
            margin: 0;
            min-height: 100vh;
            background-color: #f1f3f6;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", sans-serif;
            padding: 20px;
        }

        .card {
            width: 650px;
            border: none;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            text-align: center;
            padding: 25px;
        }

        .card-header h5 {
            margin: 0;
            font-size: 2rem;
            font-weight: 600;
        }

        .card-body {
            background-color: #ffffff;
            padding: 35px;
        }

        .form-label {
            font-size: 1rem;
            font-weight: 600;
            color: #212529;
            margin-bottom: 8px;
        }

        .form-control {
            height: 52px;
            border-radius: 8px;
            border: 1px solid #ced4da;
            font-size: 1rem;
        }

        .form-control:focus {
            border-color: #0d6efd;
            box-shadow: none;
        }

        .btn {
            border-radius: 8px;
            min-width: 120px;
            height: 48px;
        }

        .alert {
            border-radius: 8px;
        }

        .card-footer {
            background-color: #f8f9fa;
            text-align: center;
            padding: 15px;
            border-top: 1px solid #dee2e6;
            color: #6c757d;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Edit Role</h5>
    </div>

    <div class="card-body">

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
                        pattern=".{3,100}"
                        title="Description must be between 3 and 100 characters"
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

    <div class="card-footer">
        POS Management System
    </div>

</div>

</body>
</html>