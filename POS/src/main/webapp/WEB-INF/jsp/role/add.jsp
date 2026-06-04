<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <!-- Bootstrap -->
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
        <span class="navbar-brand fw-bold">Role Management</span>
        <a href="/role/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">

        <h3 class="text-center mb-4 fw-bold">Add Role</h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="/role/add"
                   modelAttribute="roleDto">

            <div class="mb-3">
                <label class="form-label fw-semibold">Role Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Enter role identifier"
                            required="true"/>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Description</label>
                <form:input path="description"
                            cssClass="form-control"
                            placeholder="Enter role description"
                            required="true"/>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">
                    Add Role
                </button>

                <a href="/role/list" class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>