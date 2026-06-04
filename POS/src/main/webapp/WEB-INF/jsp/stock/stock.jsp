<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            height: 100vh;
            overflow: hidden;
            position: relative;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(60px);
            opacity: 0.5;
            animation: float 10s infinite ease-in-out;
        }

        .blob1 {
            width: 300px;
            height: 300px;
            background: #3b82f6;
            top: -80px;
            left: -80px;
        }

        .blob2 {
            width: 250px;
            height: 250px;
            background: #93c5fd;
            bottom: -80px;
            right: -80px;
            animation-delay: 3s;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0px) scale(1);
            }
            50% {
                transform: translateY(-30px) scale(1.05);
            }
        }

        .main-container {
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            z-index: 2;
        }

        .card {
            width: 420px;
            padding: 25px;
            border-radius: 18px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            border: 1px solid rgba(255,255,255,0.4);

            box-shadow:
                0 20px 50px rgba(0,0,0,0.15),
                inset 0 1px 0 rgba(255,255,255,0.6);

            animation: fadeInUp 0.5s ease;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(40px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        h4 {
            color: #3b82f6;
            font-weight: 700;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            transition: 0.25s ease;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .btn-success {
            background: #3b82f6;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            box-shadow: 0 8px 20px rgba(37,99,235,0.25);
            transition: 0.2s;
        }

        .btn-success:hover {
            background: #2563eb;
            transform: translateY(-2px);
        }

        .btn-outline-secondary {
            border-radius: 10px;
        }

        .alert {
            border-radius: 10px;
        }
    </style>
</head>

<body>
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="main-container">

    ${message}

    <div class="card">

        <h4 class="text-center mb-4">Edit Stock</h4>

        <c:if test="${empty stockDto}">
            <div class="alert alert-danger text-center">
                Stock not found
            </div>
        </c:if>

        <c:if test="${not empty stockDto}">
            <form:form action="/stock/update"
                       method="post"
                       modelAttribute="stockDto">

                <form:hidden path="id" value="${stockDto.id}"/>

                <div class="product-group">
                        <span class="product-label">SELECT PRODUCT</span>

                        <form:select path="product" cssClass="form-control">

                            <form:option value="" label="-- Select Product --"/>

                            <c:forEach var="product" items="${products}">
                                <form:option value="${product.identifier}">
                                    ${product.name}
                                </form:option>
                            </c:forEach>

                        </form:select>
                </div>

                <div class="mb-4">
                    <label for="quantity" class="form-label">Quantity</label>
                    <form:input id="quantity"
                                path="quantity"
                                cssClass="form-control"
                                placeholder="Enter quantity"
                                required="true"/>
                </div>

                <div class="mb-3">
                    <label for="reorderLevel">Reorder Level</label>
                    <form:input id="reorderLevel"
                                path="reorderLevel"
                                cssClass="form-control"
                                placeholder="Enter reorder level"
                                required="true"/>
                </div>

                <div class="warehouse-group">
                    <form:select path="warehouse" cssClass="form-control">
                        <span class="warehouse-label">SELECT WAREHOUSE</span>

                        <form:option value="" label="-- Select Warehouse --"/>

                        <c:forEach var="warehouse" items="${warehouses}">
                            <form:option value="${warehouse.identifier}">
                                ${warehouse.identifier}
                            </form:option>
                        </c:forEach>

                    </form:select>
                </div>

                <div class="rack-group">
                        <span class="rack-label">SELECT RACK</span>

                        <form:select path="rack" cssClass="form-control">

                            <form:option value="" label="-- Select Rack --"/>

                            <c:forEach var="rack" items="${racks}">
                                <form:option value="${rack.identifier}">
                                    ${rack.identifier}
                                </form:option>
                            </c:forEach>

                        </form:select>
                </div>

                <div class="shelf-group">
                        <span class="shelf-label">SELECT SHELF</span>

                        <form:select path="shelf" cssClass="form-control">

                            <form:option value="" label="-- Select Shelf --"/>

                            <c:forEach var="shelf" items="${shelfs}">
                                <form:option value="${shelf.identifier}">
                                    ${shelf.identifier}
                                </form:option>
                            </c:forEach>

                        </form:select>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/stock/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-success">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>

    </div>

</div>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    if (!form) return;

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const product =
            document.querySelector('select[name="product"]');

        const quantity =
            document.querySelector('input[name="quantity"]');

        const reorderLevel =
            document.querySelector('input[name="reorderLevel"]');

        const warehouse =
            document.querySelector('select[name="warehouse"]');

        const rack =
            document.querySelector('select[name="rack"]');

        const shelf =
            document.querySelector('select[name="shelf"]');

        function showError(element, message) {

            const small = document.createElement("small");

            small.className = "validation-error";
            small.style.color = "red";
            small.style.fontSize = "13px";
            small.style.display = "block";
            small.style.marginTop = "5px";

            small.innerText = message;

            element.parentNode.appendChild(small);

            element.focus();

            e.preventDefault();

            return false;
        }

        if (product.value.trim() === "") {
            return showError(
                product,
                "Please select a product"
            );
        }

        if (quantity.value.trim() === "") {
            return showError(
                quantity,
                "Stock quantity is required"
            );
        }

        const quantityRegex = /^\d+$/;

        if (!quantityRegex.test(quantity.value.trim())) {
            return showError(
                quantity,
                "Quantity must be a valid whole number"
            );
        }

        if (parseInt(quantity.value.trim()) < 0) {
            return showError(
                quantity,
                "Quantity cannot be negative"
            );
        }

        if (reorderLevel.value.trim() === "") {
            return showError(
                reorderLevel,
                "Reorder level is required"
            );
        }

        if (!quantityRegex.test(reorderLevel.value.trim())) {
            return showError(
                reorderLevel,
                "Reorder level must be a valid whole number"
            );
        }

        if (parseInt(reorderLevel.value.trim()) < 0) {
            return showError(
                reorderLevel,
                "Reorder level cannot be negative"
            );
        }

        if (
            parseInt(reorderLevel.value.trim()) >
            parseInt(quantity.value.trim())
        ) {
            return showError(
                reorderLevel,
                "Reorder level cannot exceed stock quantity"
            );
        }

        if (warehouse.value.trim() === "") {
            return showError(
                warehouse,
                "Please select a warehouse"
            );
        }

        if (rack.value.trim() === "") {
            return showError(
                rack,
                "Please select a rack"
            );
        }

        if (shelf.value.trim() === "") {
            return showError(
                shelf,
                "Please select a shelf"
            );
        }

    });
});
</script>
</body>
</html>