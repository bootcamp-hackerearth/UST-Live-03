<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            max-width: 480px;
            margin-left: auto;
            margin-right: auto;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
        }

        .btn-gradient:hover {
            background: linear-gradient(to right, #134e4a, #0f766e);
            color: #ffffff;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header text-center">
    <h4 class="mb-0">Add Stock</h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/stock/add"
                  class="needs-validation"
                  novalidate>

                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}" />

                <div class="mb-3">
                    <label>Product</label>
                    <select name="productId"
                            id="productSelect"
                            class="form-select"
                            required>
                        <option value="">-- Select Product --</option>
                        <c:forEach items="${products}" var="product">
                            <option value="${product.id}"
                                    data-identifier="${product.identifier}">
                                ${product.identifier}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label>Warehouse</label>
                    <select name="warehouseId"
                            id="warehouseSelect"
                            class="form-select"
                            required>
                        <option value="">-- Select Warehouse --</option>
                        <c:forEach items="${warehouses}" var="warehouse">
                            <option value="${warehouse.id}"
                                    data-identifier="${warehouse.identifier}">
                                ${warehouse.identifier}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label>Stock Code</label>
                    <input type="text"
                           id="identifier"
                           class="form-control"
                           readonly>
                    <small class="text-muted">
                        Auto‑generated on save
                    </small>
                </div>

                <div class="mb-3">
                    <label>Quantity</label>
                    <input type="number"
                           name="quantity"
                           id="quantity"
                           class="form-control"
                           min="0"
                           required>
                </div>

                <div class="mb-3">
                    <label>Minimum Stock</label>
                    <input type="number"
                           name="minimumStock"
                           id="minimumStock"
                           class="form-control"
                           min="0"
                           required>
                </div>

                <div class="mb-3">
                    <label>Stock Level</label>
                    <input type="text"
                           id="stockLevel"
                           class="form-control"
                           readonly>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/stock/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        Save Stock
                    </button>
                </div>

            </form>
        </div>
    </div>
</div>

<script>
(() => {

    const productSelect = document.getElementById("productSelect");
    const warehouseSelect = document.getElementById("warehouseSelect");
    const identifier = document.getElementById("identifier");
    const quantity = document.getElementById("quantity");
    const minimumStock = document.getElementById("minimumStock");
    const stockLevel = document.getElementById("stockLevel");

    function updateIdentifier() {
        const p = productSelect.options[productSelect.selectedIndex];
        const w = warehouseSelect.options[warehouseSelect.selectedIndex];

        const pCode = p?.dataset.identifier || "";
        const wCode = w?.dataset.identifier || "";

        identifier.value = (pCode && wCode)
            ? "STK-" + pCode + "-" + wCode
            : "";
    }

    function updateStockLevel() {
        const q = Number(quantity.value || 0);
        const m = Number(minimumStock.value || 0);

        if (q === 0) stockLevel.value = "OUT OF STOCK";
        else if (q <= m) stockLevel.value = "LOW STOCK";
        else stockLevel.value = "IN STOCK";
    }

    productSelect.addEventListener("change", updateIdentifier);
    warehouseSelect.addEventListener("change", updateIdentifier);
    quantity.addEventListener("input", updateStockLevel);
    minimumStock.addEventListener("input", updateStockLevel);

})();
</script>

</body>
</html>