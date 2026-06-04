<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Add Rack</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }

        .shelf-box {
            max-height: 180px;
            overflow-y: auto;
            border: 1px solid #ced4da;
            border-radius: 10px;
            padding: 10px;
            background: #fff;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">

        <span class="navbar-brand fw-bold">
            Rack Management
        </span>

        <a href="${pageContext.request.contextPath}/rack/list" class="btn btn-outline-light btn-sm">
            Back
        </a>

    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 500px;">

        <h3 class="text-center mb-4 fw-bold">
            Add Rack
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/rack/add"
                   modelAttribute="rackDto">

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Identifier
                    <span class="text-danger">*</span>
                </label>

                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Enter rack identifier"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Rack Name
                    <span class="text-danger">*</span>
                </label>

                <form:input path="name"
                            cssClass="form-control"
                            placeholder="Enter rack name"
                            required="true"/>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Shelves
                    <span class="text-danger">*</span>
                </label>

                <div class="shelf-box">
                    <c:forEach var="s" items="${shelves}">
                        <div class="form-check">

                            <form:checkbox path="shelfIdentifiers"
                                           value="${s.identifier}"
                                           cssClass="form-check-input"/>

                            <label class="form-check-label">
                                ${s.name}
                            </label>

                        </div>
                    </c:forEach>
                </div>

                <small class="text-danger">
                    Select at least one shelf
                </small>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Status
                </label>

                <form:select path="status" cssClass="form-select">

                    <form:option value="true">
                        Active
                    </form:option>

                    <form:option value="false">
                        Inactive
                    </form:option>

                </form:select>
            </div>

            <div class="d-flex gap-2">

                <button type="submit" class="btn btn-primary w-100">
                    Save Rack
                </button>

                <a href="${pageContext.request.contextPath}/rack/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>

            </div>

        </form:form>

    </div>
</div>

</body>

</html>