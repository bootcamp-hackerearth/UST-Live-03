<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <title>Edit Warehouse</title>
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
        <span class="navbar-brand fw-bold">
            Warehouse Management
        </span>

        <a href="${pageContext.request.contextPath}/warehouse/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>
    </div>

</nav>

<div class="container d-flex justify-content-center align-items-center"
     style="min-height: 100vh;">

    <div class="card shadow p-4" style="width: 450px;">
        <h3 class="text-center mb-4 fw-bold">
            Edit Warehouse
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/warehouse/update"
                   modelAttribute="warehouseDto">

            <form:hidden path="identifier"/>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Identifier
                </label>

                <input type="text"
                       class="form-control"
                       value="${warehouseDto.identifier}"
                       readonly/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Warehouse Name
                </label>
                <form:input path="warehouseName"
                            cssClass="form-control"
                            maxlength="100"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    City
                </label>

                <form:input path="cityName"
                            cssClass="form-control"
                            maxlength="100"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    State
                </label>

                <form:input path="state"
                            cssClass="form-control"
                            maxlength="100"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Country
                </label>

                <form:input path="country"
                            cssClass="form-control"
                            maxlength="100"
                            required="true"/>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Location
                </label>

                <form:input path="location"
                            cssClass="form-control"
                            maxlength="100"
                            required="true"/>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">
                    Update
                </button>

                <a href="${pageContext.request.contextPath}/warehouse/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>
        </form:form>
    </div>
</div>

</body>

</html>