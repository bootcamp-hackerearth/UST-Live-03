<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Stock Form</title>
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
    </style>
</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">
            Stock Management
        </span>

        <a href="${pageContext.request.contextPath}/stock/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 520px;">
        <h3 class="text-center mb-4 fw-bold">
            Add Stock
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/stock/add"
                   modelAttribute="stockDto">
            <form:hidden path="identifier"/>
            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Product
                </label>

                <form:select path="productIdentifier"
                             cssClass="form-select"
                             required="true">

                    <form:option value="" label="Select Product"/>
                    <c:forEach var="p" items="${product}">
                        <form:option value="${p.identifier}">
                            ${p.productName}(${p.identifier})
                        </form:option>
                    </c:forEach>
                </form:select>

            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Warehouse
                </label>

                <form:select path="warehouseIdentifier"
                             cssClass="form-select"
                             required="true">

                    <form:option value="" label="Select Warehouse"/>
                    <c:forEach var="w" items="${warehouse}">
                        <form:option value="${w.identifier}">
                            ${w.warehouseName}(${w.identifier})
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Available Quantity
                </label>
                <form:input path="availableQuantity"
                            type="number"
                            cssClass="form-control"
                            min="0"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Reorder Level
                </label>

                <form:input path="reorderLevel"
                            type="number"
                            cssClass="form-control"
                            min="0"
                            required="true"/>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Status
                </label>

                <form:select path="status"
                             cssClass="form-select">
                    <form:option value="true">
                        ACTIVE
                    </form:option>

                    <form:option value="false">
                        INACTIVE
                    </form:option>

                </form:select>

            </div>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary w-100">
                    Add Stock
                </button>

                <a href="${pageContext.request.contextPath}/stock/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>

            </div>
        </form:form>
    </div>

</div>
</body>
</html>