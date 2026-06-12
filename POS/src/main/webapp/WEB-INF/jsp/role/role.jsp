<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Role</title>

    https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css

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

        .btn {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Edit Role
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/role/update"
                   modelAttribute="role">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Role Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true"/>
                <small class="text-muted">
                    Role name cannot be changed
                </small>
            </div>

            <div class="mb-3">
                <label class="form-label">Description</label>
                <form:textarea path="description"
                               cssClass="form-control"
                               rows="3"
                               placeholder="Update role description"/>
            </div>

            <div class="d-grid gap-2 mt-4">

                <button type="submit" class="btn btn-success">
                    Update Role
                </button>

                <a href="${pageContext.request.contextPath}/role/list"
                   class="btn btn-secondary">
                    Cancel
                </a>

            </div>

        </form:form>

    </div>

    <div class="card-footer text-center text-muted small">
        Role Management System
    </div>

</div>

</body>
</html>