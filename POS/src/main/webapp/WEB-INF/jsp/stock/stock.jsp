<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .card {
            border-radius: 14px;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 10px;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-pencil-square me-2"></i> Update Stock
    </h4>

    <a href="${pageContext.request.contextPath}/stock/list"
       class="btn btn-light btn-sm">
        <i class="bi bi-arrow-left-circle me-1"></i> Back
    </a>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form id="updateStockForm"
              action="${pageContext.request.contextPath}/stock/update"
              method="post"
              class="needs-validation"
              novalidate>

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}" />

            <input type="hidden" name="id" value="${stock.id}">
            <input type="hidden" name="productId" value="${stock.productId}">
            <input type="hidden" name="warehouseId" value="${stock.warehouseId}">
            <input type="hidden" name="status" value="${stock.status}">

            <div class="row g-3">

                <div class="col-md-6">
                    <label class="form-label">Product</label>
                    <input type="text"
                           class="form-control"
                           value="${stock.productIdentifier}"
                           readonly>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Warehouse</label>
                    <input type="text"
                           class="form-control"
                           value="${stock.warehouseIdentifier}"
                           readonly>
                </div>

                <div class="col-md-12">
                    <label class="form-label">Stock Code</label>
                    <input type="text"
                           class="form-control"
                           value="STK-${stock.productIdentifier}-${stock.warehouseIdentifier}"
                           readonly>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Quantity</label>
                    <input type="number"
                           id="quantity"
                           name="quantity"
                           class="form-control"
                           min="0"
                           value="${stock.quantity}"
                           required>
                    <div class="invalid-feedback">
                        Quantity must be zero or greater
                    </div>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Minimum Stock</label>
                    <input type="number"
                           id="minimumStock"
                           name="minimumStock"
                           class="form-control"
                           min="0"
                           value="${stock.minimumStock}"
                           required>
                    <div class="invalid-feedback">
                        Minimum stock must be zero or greater
                    </div>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Stock Level</label>
                    <input type="text"
                           id="stockLevel"
                           class="form-control"
                           readonly>
                </div>

            </div>

            <div class="mt-4 d-flex justify-content-end gap-2">
                <a href="${pageContext.request.contextPath}/stock/list"
                   class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-success px-4">
                    <i class="bi bi-check-circle me-1"></i> Update Stock
                </button>
            </div>
        </form>

    </div>
</div>

<script>
(() => {
    const quantity = document.getElementById("quantity");
    const minimumStock = document.getElementById("minimumStock");
    const stockLevel = document.getElementById("stockLevel");
    const form = document.getElementById("updateStockForm");

    function updateStockLevel() {
        const q = Number(quantity.value || 0);
        const m = Number(minimumStock.value || 0);

        if (q === 0) stockLevel.value = "OUT OF STOCK";
        else if (q <= m) stockLevel.value = "LOW STOCK";
        else stockLevel.value = "IN STOCK";
    }

    updateStockLevel();

    quantity.addEventListener("input", updateStockLevel);
    minimumStock.addEventListener("input", updateStockLevel);

    form.addEventListener("submit", e => {
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        form.classList.add("was-validated");
    });
})();
</script>

</body>
</html>