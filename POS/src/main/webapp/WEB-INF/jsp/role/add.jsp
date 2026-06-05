<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
        }

        .card {
            width: 450px;
            border: none;
            border-radius: 12px;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }

        .card-footer {
            background-color: #ffffff;
        }
    </style>
</head>

<body>
<div class="card shadow-sm">

    <div class="card-header text-center py-3">
        <h5 class="mb-0">Add New Role</h5>
    </div>

    <div class="card-body p-4">

        <c:if test="${not empty role}">
            <div class="alert alert-success text-center">
                ${role}
            </div>
        </c:if>

        <form:form method="post"
                   action="/role/add"
                   modelAttribute="roleDto">

            <div class="mb-3">
                <form:label path="identifier" cssClass="form-label">
                    Role Name
                </form:label>

                <form:input path="identifier"
                            cssClass="form-control"
                            required="true"/>
            </div>

            <div class="mb-3">
                <form:label path="description" cssClass="form-label">
                    Description
                </form:label>

                <form:input path="description"
                            cssClass="form-control"
                            required="true"/>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary">
                    Add Role
                </button>
            </div>

        </form:form>

    </div>

    <div class="card-footer text-center">
        <a href="/" class="btn btn-secondary mb-2">Home</a>
        <div class="text-muted small">POS Management System</div>
    </div>

</div>
</body>
</html>