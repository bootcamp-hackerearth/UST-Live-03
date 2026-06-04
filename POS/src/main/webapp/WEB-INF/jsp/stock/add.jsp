<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to right, #eef2f7, #f9fbfd);
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .card {
            width: 100%;
            max-width: 550px;
            border-radius: 14px;
            border: none;
            box-shadow: 0 12px 28px rgba(0,0,0,0.12);
        }

        .card-header {
            background: linear-gradient(to right, #000, #343a40);
            color: #fff;
            text-align: center;
            font-weight: 600;
            font-size: 18px;
            padding: 14px;
            border-top-left-radius: 14px;
            border-top-right-radius: 14px;
        }

        .card-body {
            padding: 25px;
        }

        .form-label {
            font-weight: 600;
            font-size: 13px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #4e73df;
            box-shadow: none;
        }

        .btn-success {
            border-radius: 8px;
            padding: 10px;
            background-color: #198754;
            border: none;
        }

        .btn-secondary {
            border-radius: 8px;
            padding: 10px;
        }

        .invalid-feedback {
            font-size: 12px;
        }
    </style>
</head>

<body>

<div class="card">
    <div class="card-header">
        Add Stock
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form id="stockForm"
              method="post"
              action="${pageContext.request.contextPath}/stock/add"
              class="needs-validation"
              novalidate>

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}" />

            <div class="mb-3">
                <label class="form-label">Product</label>
                <select name="productId" id="productSelect"
                        class="form-select" required>
                    <option value="">-- Select Product --</option>
                    <c:forEach items="${products}" var="product">
                        <option value="${product.id}"
                                data-identifier="${product.identifier}">
                            ${product.identifier}
                        </option>
                    </c:forEach>
                </select>
                <div class="invalid-feedback">Select a product</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Warehouse</label>
                <select name="warehouseId" id="warehouseSelect"
                        class="form-select" required>
                    <option value="">-- Select Warehouse --</option>
                    <c:forEach items="${warehouses}" var="warehouse">
                        <option value="${warehouse.id}"
                                data-identifier="${warehouse.identifier}">
                            ${warehouse.identifier}
                        </option>
                    </c:forEach>
                </select>
                <div class="invalid-feedback">Select a warehouse</div>
            </div>

            <input type="hidden" name="productIdentifier" id="productIdentifier">
            <input type="hidden" name="warehouseIdentifier" id="warehouseIdentifier">

            <div class="mb-3">
                <label class="form-label">Stock Code</label>
                <input type="text" id="identifier"
                       class="form-control" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label">Quantity</label>
                <input type="number"
                       name="quantity"
                       id="quantity"
                       class="form-control"
                       min="0"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Minimum Stock</label>
                <input type="number"
                       name="minimumStock"
                       id="minimumStock"
                       class="form-control"
                       min="0"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Stock Level</label>
                <input type="text" id="stockLevel"
                       class="form-control" readonly>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Save Stock
                </button>

                <a href="${pageContext.request.contextPath}/stock/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </div>
</div>

<script>
(() => {
    const productSelect = document.getElementById("productSelect");
    const warehouseSelect = document.getElementById("warehouseSelect");
    const productIdentifier = document.getElementById("productIdentifier");
    const warehouseIdentifier = document.getElementById("warehouseIdentifier");
    const identifier = document.getElementById("identifier");
    const quantity = document.getElementById("quantity");
    const minimumStock = document.getElementById("minimumStock");
    const stockLevel = document.getElementById("stockLevel");
    const form = document.getElementById("stockForm");

    function updateCode() {
        const p = productSelect.options[productSelect.selectedIndex];
        const w = warehouseSelect.options[warehouseSelect.selectedIndex];

        productIdentifier.value = p?.dataset.identifier || "";
        warehouseIdentifier.value = w?.dataset.identifier || "";

        identifier.value =
            productIdentifier.value && warehouseIdentifier.value
                ? "STK-" + productIdentifier.value + "-" + warehouseIdentifier.value
                : "";
    }

    function updateStockLevel() {
        const q = Number(quantity.value || 0);
        const m = Number(minimumStock.value || 0);

        if (q === 0) stockLevel.value = "OUT OF STOCK";
        else if (q <= m) stockLevel.value = "LOW STOCK";
        else stockLevel.value = "IN STOCK";
    }

    productSelect.addEventListener("change", updateCode);
    warehouseSelect.addEventListener("change", updateCode);
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