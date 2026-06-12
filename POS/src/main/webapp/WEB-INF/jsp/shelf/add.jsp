<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Shelf</title>

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

        .form-control, .form-select {
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
        <span class="navbar-brand fw-bold">Shelf Management</span>

        <a href="${pageContext.request.contextPath}/shelf/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">
        <h3 class="text-center mb-4 fw-bold">Add Shelf</h3>
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/shelf/add"
                   modelAttribute="shelfDto">

            <div class="mb-3">
                <label class="form-label fw-semibold">Shelf Name</label>

                <form:input path="name"
                            cssClass="form-control"
                            placeholder="Enter shelf name"/>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Status</label>

                <form:select path="status" cssClass="form-select">
                    <form:option value="true">Active</form:option>
                    <form:option value="false">Inactive</form:option>
                </form:select>
            </div>

            <div class="d-flex gap-2">

                <button type="submit" class="btn btn-primary w-100">
                    Save Shelf
                </button>

                <a href="${pageContext.request.contextPath}/shelf/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>
        </form:form>
    </div>

</div>

</body>
</html>