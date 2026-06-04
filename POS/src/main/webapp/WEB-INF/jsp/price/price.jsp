<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Price</title>

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

        <h4 class="text-center mb-4">Edit Price</h4>

        <c:if test="${empty priceDto}">
            <div class="alert alert-danger text-center">
                Price not found
            </div>
        </c:if>

        <c:if test="${not empty priceDto}">
            <form:form action="/price/update"
                       method="post"
                       modelAttribute="priceDto">

                <form:hidden path="id" value="${priceDto.id}"/>
                <form:hidden path="identifier" value="${priceDto.identifier}"/>

                <div class="mb-3">
                    <label for="product" class="form-label fw-semibold">Product</label>
                    <form:input id="product"
                                path="product"
                                cssClass="form-control" readonly="true"/>
                </div>

                <div class="mb-3">
                    <label for="priceType" class="form-label fw-semibold">Price Type</label>
                    <form:input id="priceType"
                                path="priceType"
                                cssClass="form-control" readonly="true"/>
                </div>

                <div class="mb-3">
                    <label for="value" class="form-label fw-semibold">Price</label>
                    <form:input id="value"
                                path="value"
                                cssClass="form-control"
                                placeholder="Enter price" />
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/price/list" class="btn btn-outline-secondary">
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

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

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