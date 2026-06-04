<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Price</title>

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

        .form-card {
            width: 380px;
            padding: 40px;

            border-radius: 18px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            border: 1px solid rgba(255, 255, 255, 0.4);

            box-shadow:
                0 20px 50px rgba(0,0,0,0.15),
                inset 0 1px 0 rgba(255,255,255,0.6);

            animation: fadeInUp 0.6s ease;
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

        .form-card h4 {
            text-align: center;
            margin-bottom: 25px;
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

        .btn-primary-custom {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;

            background: #3b82f6;
            color: white;
            font-weight: 600;

            box-shadow: 0 10px 25px rgba(37,99,235,0.25);

            transition: all 0.2s ease;
        }

        .btn-primary-custom:hover {
            background: #2563eb;
            transform: translateY(-2px);
            box-shadow: 0 14px 30px rgba(37,99,235,0.35);
        }

        .back-link {
            text-align: center;
            margin-top: 15px;
        }

        .back-link a {
            color: #3b82f6;
            text-decoration: none;
            font-weight: 500;
        }

        .back-link a:hover {
            color: #2563eb;
            text-decoration: underline;
        }

        .alert {
            border-radius: 10px;
        }

        .custom-toast {
            position: fixed;
            bottom: 30px;
            left: 50%;
            transform: translateX(-50%) translateY(20px);

            min-width: 260px;
            max-width: 80%;

            padding: 14px 18px;
            border-radius: 14px;

            text-align: center;

            font-size: 14px;
            font-weight: 500;
            color: rgba(31, 59, 59, 0.9);

            background: rgba(255, 255, 255, 0.18);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            background-image: linear-gradient(
                135deg,
                rgba(255, 255, 255, 0.25),
                rgba(255, 255, 255, 0.08)
            );

            border: 1px solid rgba(255, 255, 255, 0.35);
            box-shadow:
                0 12px 30px rgba(0, 0, 0, 0.12),
                inset 0 1px 0 rgba(255, 255, 255, 0.4);

            z-index: 9999;
            opacity: 0;
            animation: toastIn 0.4s ease forwards;
        }

        .custom-toast::before {
            content: "";
            position: absolute;
            inset: -2px;
            border-radius: inherit;
            background: radial-gradient(
                circle at center,
                rgba(74, 166, 163, 0.25),
                transparent 70%
            );
            z-index: -1;
            filter: blur(12px);
        }

        @keyframes toastIn {
            from {
                opacity: 0;
                transform: translateX(-50%) translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateX(-50%) translateY(0);
            }
        }

        .custom-toast.hide {
            opacity: 0;
            transform: translateX(-50%) translateY(20px);
            transition: all 0.4s ease;
        }

    </style>
</head>
<body>
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="main-container">

    <div class="form-card">

        <h4>Add New Price</h4>
            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
            <c:remove var="message" scope="session"/>
        </c:if>

        <form:form method="post"
                   action="/price/add"
                   modelAttribute="priceDto">

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
                <label for="priceType" class="form-label fw-semibold">Price Type</label>
                <form:select path="priceType" cssClass="form-control" id="priceType">
                    <form:option value="" label="-- Select Price Type --"/>
                    <form:option value="Mrp" label="MRP"/>
                    <form:option value="Selling" label="Selling Price"/>
                </form:select>
            </div>

            <div class="mb-3">
                <label for="value" class="form-label fw-semibold">Price</label>
                <form:input id="value"
                            path="value"
                            cssClass="form-control"
                            placeholder="Enter price" />
            </div>

            <button type="submit" class="btn-primary-custom">
                Add Price
            </button>

        </form:form>

        <div class="back-link">
            <a href="/price/list">← Back to List</a>
        </div>

    </div>

</div>
<script>
document.addEventListener("mousemove", (e) => {
    const x = (window.innerWidth / 2 - e.clientX) / 30;
    const y = (window.innerHeight / 2 - e.clientY) / 30;

    document.querySelector(".blob1").style.transform =
        `translate(${x}px, ${y}px)`;

    document.querySelector(".blob2").style.transform =
        `translate(${x * -1}px, ${y * -1}px)`;
});
</script>
<script>
window.addEventListener("DOMContentLoaded", function () {
    const toast = document.getElementById("customToast");

    if (toast) {
        setTimeout(() => {
            toast.classList.add("hide");
            setTimeout(() => toast.remove(), 400);
        }, 3500);
    }
});
</script>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const product =
            document.querySelector('select[name="product"]');

        const priceType =
            document.querySelector('select[name="priceType"]');

        const value =
            document.querySelector('input[name="value"]');

        function showError(element, message) {

            const small = document.createElement("small");

            small.className = "validation-error";
            small.style.color = "red";
            small.style.fontSize = "13px";
            small.style.display = "block";
            small.style.marginTop = "5px";
            small.innerText = message;

            if (
                element.tagName === "INPUT" ||
                element.tagName === "SELECT" ||
                element.tagName === "TEXTAREA"
            ) {
                element.parentNode.appendChild(small);
                element.focus();
            }
            else {
                element.appendChild(small);
            }

            e.preventDefault();

            return false;
        }

        if (product.value.trim() === "") {
            return showError(
                product,
                "Please select a product"
            );
        }

        if (priceType.value.trim() === "") {
            return showError(
                priceType,
                "Please select a price type"
            );
        }

        if (value.value.trim() === "") {
            return showError(
                value,
                "Price is required"
            );
        }

        const priceRegex = /^\d+(\.\d{1,2})?$/;

        if (!priceRegex.test(value.value.trim())) {
            return showError(
                value,
                "Enter a valid price (up to 2 decimal places)"
            );
        }

        if (parseFloat(value.value.trim()) <= 0) {
            return showError(
                value,
                "Price must be greater than 0"
            );
        }

        if (parseFloat(value.value.trim()) > 1000000) {
            return showError(
                value,
                "Price is too large"
            );
        }

    });
});
</script>
</body>
</html>