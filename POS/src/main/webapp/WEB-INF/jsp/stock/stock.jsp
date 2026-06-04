<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Stock</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to right, #eef2f7, #f9fbfd);
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .card {
            max-width: 550px;
            width: 100%;
            border-radius: 12px;
        }
    </style>
</head>

<body>

<div class="card shadow p-4">

    <h4 class="text-center mb-3">Edit Stock</h4>

    <c:if test="${not empty message}">
        <div class="alert alert-danger text-center">
            ${message}
        </div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/stock/update">

        <input type="hidden" name="id" value="${stock.id}" />

        <div class="mb-3">
            <label>Product</label>
            <input class="form-control"
                   value="${stock.productIdentifier}" readonly>

            <input type="hidden"
                   name="productIdentifier"
                   value="${stock.productIdentifier}">
        </div>

        <div class="mb-3">
            <label>Warehouse</label>
            <input class="form-control"
                   value="${stock.warehouseIdentifier}" readonly>

            <input type="hidden"
                   name="warehouseIdentifier"
                   value="${stock.warehouseIdentifier}">
        </div>

        <div class="mb-3">
            <label>Stock Code</label>
            <input class="form-control"
                   value="STK-${stock.productIdentifier}-${stock.warehouseIdentifier}"
                   readonly>
        </div>

        <div class="mb-3">
            <label>Quantity</label>
            <input type="number"
                   name="quantity"
                   value="${stock.quantity}"
                   class="form-control"
                   min="0"
                   required>
        </div>

        <div class="mb-3">
            <label>Minimum Stock</label>
            <input type="number"
                   name="minimumStock"
                   value="${stock.minimumStock}"
                   class="form-control"
                   min="0"
                   required>
        </div>

        <div class="mb-3">
            <label>Status</label>
            <input type="text"
                   class="form-control"
                   value="${stock.status ? 'IN STOCK' : 'LOW / OUT'}"
                   readonly>
        </div>

        <div class="d-grid gap-2">
            <button type="submit" class="btn btn-success">
                Update
            </button>

            <a href="${pageContext.request.contextPath}/stock/list"
               class="btn btn-secondary">
                Cancel
            </a>
        </div>

    </form>

</div>

</body>
</html>
