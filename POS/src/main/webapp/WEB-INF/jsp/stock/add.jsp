<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.5;
            animation: float 8s ease-in-out infinite;
        }

        .blob1 {
            width: 280px;
            height: 280px;
            background: #3b82f6;
            top: -80px;
            left: -80px;
        }

        .blob2 {
            width: 240px;
            height: 240px;
            background: #60a5fa;
            bottom: -80px;
            right: -80px;
            animation-delay: 2s;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(25px); }
        }

        .card-glass {
            width: 460px;
            padding: 35px 40px;
            border-radius: 18px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px);

            box-shadow: 0 25px 50px rgba(0,0,0,0.15);

            animation: fadeIn 0.6s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h4 {
            text-align: center;
            color: #3b82f6;
            font-weight: 700;
            margin-bottom: 25px;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            padding: 10px;
            transition: 0.2s;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .checkbox-list {
            display: flex;
            flex-direction: column;
            gap: 7px;
            margin-top: 6px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 12px;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            cursor: pointer;
            transition: border-color 0.2s, background 0.2s;
            font-weight: 500;
        }

        .checkbox-item:hover {
            border-color: #3b82f6;
            background: #eff6ff;
        }

        .checkbox-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            min-width: 16px;
            margin: 0;
            padding: 0;
            accent-color: #3b82f6;
            cursor: pointer;
        }

        .checkbox-item span {
            font-size: 13px;
            color: #374151;
            font-weight: 500;
        }

        .checkbox-item:has(input:checked) {
            border-color: #3b82f6;
            background: #eff6ff;
        }

        .btn-primary-custom {
            width: 100%;
            padding: 12px;
            border-radius: 10px;
            border: none;

            background: linear-gradient(135deg, #93c5fd, #3b82f6);
            color: white;
            font-weight: 600;

            box-shadow: 0 10px 25px rgba(37,99,235,0.25);
            transition: 0.2s ease;
        }

        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 15px 30px rgba(37,99,235,0.35);
        }

        a {
            color: #3b82f6;
            text-decoration: none;
            font-weight: 500;
        }

        a:hover {
            text-decoration: underline;
        }

        .alert {
            border-radius: 10px;
        }
    </style>
</head>

<body>

<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="card-glass">

    <h4>Add New Stock</h4>

    <c:if test="${not empty stock}">
        <div class="alert alert-success text-center">
            ${stock}
        </div>
    </c:if>

    <form:form method="post"
               action="/stock/add"
               modelAttribute="stockDto">

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

        <div class="mb-3">
            <label for="quantity">Stock Quantity</label>
            <form:input id="quantity" path="quantity"
                        cssClass="form-control"
                        placeholder="Enter quantity"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label for="reorderLevel">Reorder Level</label>
            <form:input id="reorderLevel" path="reorderLevel"
                        cssClass="form-control"
                        placeholder="Enter reorder level"
                        required="true"/>
        </div>

        <div class="warehouse-group">
                <span class="warehouse-label">SELECT WAREHOUSE</span>

                <form:select path="warehouse" cssClass="form-control">

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

        <button type="submit" class="btn-primary-custom">
            Save
        </button>

    </form:form>

    <div class="text-center mt-3">
        <a href="/stock/list">← Back to List</a>
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
