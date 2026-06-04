<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">
    <title>Edit Stock</title>

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
            Edit Stock
        </h3>
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/stock/update"
              method="post">

            <input type="hidden"
                   name="identifier"
                   value="${stockDto.identifier}" />

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Product
                </label>

                <input type="hidden"
                       name="productIdentifier"
                       value="${stockDto.productIdentifier}" />

                <input type="text"
                       class="form-control"
                       value="<c:forEach var='p' items='${product}'><c:if test='${p.identifier == stockDto.productIdentifier}'>${p.productName}</c:if></c:forEach>"
                       readonly />
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Warehouse
                </label>

                <input type="hidden"
                       name="warehouseIdentifier"
                       value="${stockDto.warehouseIdentifier}" />

                <input type="text"
                       class="form-control"
                       value="<c:forEach var='w' items='${warehouse}'><c:if test='${w.identifier == stockDto.warehouseIdentifier}'>${w.warehouseName}</c:if></c:forEach>"
                       readonly />
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Available Quantity
                </label>

                <input type="number"
                       name="availableQuantity"
                       value="${stockDto.availableQuantity}"
                       class="form-control"
                       min="0"
                       required />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">
                    Reorder at this quantity
                </label>

                <input type="number"
                       name="reorderLevel"
                       value="${stockDto.reorderLevel}"
                       class="form-control"
                       min="0"
                       required />
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Status
                </label>

                <select name="status" class="form-select">
                    <option value="true" ${stockDto.status ? 'selected' : ''}>
                        ACTIVE
                    </option>

                    <option value="false" ${!stockDto.status ? 'selected' : ''}>
                        INACTIVE
                    </option>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">
                    Update Stock
                </button>

                <a href="${pageContext.request.contextPath}/stock/list"
                   class="btn btn-secondary w-100">
                    Cancel
                </a>
            </div>
        </form>
    </div>

</div>
</body>
</html>