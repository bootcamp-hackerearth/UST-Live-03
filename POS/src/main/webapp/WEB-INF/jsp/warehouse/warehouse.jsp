<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Warehouse</title>

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

        <h4 class="text-center mb-4">Edit Warehouse</h4>

        <c:if test="${empty warehouseDto}">
            <div class="alert alert-danger text-center">
                Warehouse not found
            </div>
        </c:if>

        <c:if test="${not empty warehouseDto}">
            <form:form action="/warehouse/update"
                       method="post"
                       modelAttribute="warehouseDto">

                <form:hidden path="id" value="${warehouseDto.id}"/>

                <div class="mb-3">
                    <label for="identifier" class="form-label fw-semibold">Identifier</label>
                    <form:input id="identifier"
                                path="identifier"
                                cssClass="form-control"
                                placeholder="Enter warehouse identifier" />
                </div>

                <div class="mb-3">
                    <label for="region" class="form-label fw-semibold">Region</label>
                    <form:input id="region"
                                path="region"
                                cssClass="form-control"
                                placeholder="Enter region" />
                </div>

                <div class="mb-3">
                    <label for="country" class="form-label fw-semibold">Country</label>
                    <form:input id="country"
                                path="country"
                                cssClass="form-control"
                                placeholder="Enter country" />
                </div>

                <div class="mb-3">
                    <label for="contactName" class="form-label fw-semibold">Contact Name</label>
                    <form:input id="contactName"
                                path="contactName"
                                cssClass="form-control"
                                placeholder="Enter contact name" />
                </div>

                <div class="mb-3">
                    <label for="contactNumber" class="form-label fw-semibold">Contact Number</label>
                    <form:input id="contactNumber"
                                path="contactNumber"
                                cssClass="form-control"
                                placeholder="Enter contact number" />
                </div>

                <div class="mb-3">
                    <label for="location" class="form-label fw-semibold">Location</label>
                    <form:input id="location"
                                path="location"
                                cssClass="form-control"
                                placeholder="Enter location" />
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/warehouse/list" class="btn btn-outline-secondary">
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

        const identifier =
            document.querySelector('input[name="identifier"]');

        const region =
            document.querySelector('input[name="region"]');

        const country =
            document.querySelector('input[name="country"]');

        const contactName =
            document.querySelector('input[name="contactName"]');

        const contactNumber =
            document.querySelector('input[name="contactNumber"]');

        const location =
            document.querySelector('input[name="location"]');

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

        if (identifier.value.trim() === "") {
            return showError(identifier, "Warehouse identifier is required");
        }

        if (identifier.value.trim().length < 2) {
            return showError(identifier, "Identifier must be at least 2 characters");
        }

        const idRegex = /^[A-Za-z0-9_-]+$/;

        if (!idRegex.test(identifier.value.trim())) {
            return showError(identifier, "Only letters, numbers, _ and - allowed");
        }

        if (region.value.trim().length < 2) {
            return showError(region, "Region must be at least 2 characters");
        }

        const regionRegex = /^[A-Za-z ]+$/;
        if (!regionRegex.test(region.value.trim())) {
            return showError(region, "Region must contain only letters and spaces");
        }

        if (country.value.trim() === "") {
            return showError(country, "Country is required");
        }

        if (!/^[A-Za-z ]+$/.test(country.value.trim())) {
            return showError(country, "Country must contain only letters");
        }

        if (contactName.value.trim() === "") {
            return showError(contactName, "Contact name is required");
        }

        if (contactName.value.trim().length < 2) {
            return showError(contactName, "Contact name must be at least 2 characters");
        }

        if (!/^[A-Za-z ]+$/.test(contactName.value.trim())) {
            return showError(contactName, "Contact name must contain only letters");
        }

        if (contactNumber.value.trim() === "") {
            return showError(contactNumber, "Contact number is required");
        }

        const phoneRegex = /^[0-9]{10}$/;

        if (!phoneRegex.test(contactNumber.value.trim())) {
            return showError(contactNumber, "Enter a valid phone number (10 digits)");
        }

        if (location.value.trim() === "") {
            return showError(location, "Location is required");
        }

        if (location.value.trim().length < 3) {
            return showError(location, "Location must be at least 3 characters");
        }

        const locationRegex = /^[A-Za-z ]+$/;
        if (!locationRegex.test(location.value.trim())) {
            return showError(location, "Location must contain only letters and spaces");
        }

    });
});
</script>
</body>
</html>