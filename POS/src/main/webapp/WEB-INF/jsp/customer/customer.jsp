<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;

            overflow-x: hidden;
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
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            z-index: 2;
        }

        .form-card {
            width: 700px;
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

        <h4>Add New Customer</h4>
            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>

        <form:form method="post"
                   action="/customer/update"
                   modelAttribute="customerDto">

            <div class="container-fluid p-0">

                <!-- ROW 1 -->
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="name" class="form-label fw-semibold">Customer Name</label>
                        <form:input path="name" id="name"
                                    cssClass="form-control"
                                    placeholder="Enter Customer Name"/>
                    </div>

                    <div class="col-md-6">
                        <label for="phoneNo" class="form-label fw-semibold">Phone Number</label>
                        <form:input path="phoneNo" id="phoneNo"
                                    cssClass="form-control"
                                    placeholder="Enter Phone Number"
                                    readonly="true"/>
                    </div>
                </div>

                <!-- ROW 2 -->
                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <label for="partyType" class="form-label fw-semibold">Party Type</label>
                        <form:select path="partyType" id="partyType" cssClass="form-control">
                            <form:option value="">Select one</form:option>
                            <form:option value="Customer">Customer</form:option>
                            <form:option value="Dealer">Dealer</form:option>
                            <form:option value="Wholesaler">Wholesaler</form:option>
                        </form:select>
                    </div>

                    <div class="col-md-6">
                        <label for="balance" class="form-label fw-semibold">Balance</label>
                        <div class="input-group">
                            <form:input path="balance" id="balance"
                                        cssClass="form-control"
                                        placeholder="Ex: 500"/>
                            <form:select path="balanceType" cssClass="form-select" style="max-width: 120px;">
                                <form:option value="Due">Due</form:option>
                                <form:option value="Advance">Advance</form:option>
                            </form:select>
                        </div>
                    </div>
                </div>

                <!-- ROW 3 -->
                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <label for="email" class="form-label fw-semibold">Email</label>
                        <form:input path="email" id="email"
                                    cssClass="form-control"
                                    placeholder="Enter Email"/>
                    </div>

                    <div class="col-md-6">
                        <label for="creditLimit" class="form-label fw-semibold">Party Credit Limit</label>
                        <form:input path="creditLimit" id="creditLimit"
                                    cssClass="form-control"
                                    placeholder="Ex: 800"/>
                    </div>
                </div>

                <!-- BILLING ADDRESS -->
                <div class="mt-4">
                    <h6 class="fw-bold text-danger mb-3">– Billing Address</h6>

                    <!-- ROW 1 -->
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="billingAddressLine" class="form-label">Address Line 1</label>
                            <form:input path="billingAddress.addressLine" id="billingAddressLine"
                                        cssClass="form-control"
                                        placeholder="Enter Address"/>
                        </div>

                        <div class="col-md-6">
                            <label for="billingCity" class="form-label">City</label>
                            <form:input path="billingAddress.city" id="billingCity"
                                        cssClass="form-control"
                                        placeholder="Enter city"/>
                        </div>
                    </div>

                    <!-- ROW 2 -->
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label for="billingState" class="form-label">State</label>
                            <form:input path="billingAddress.state" id="billingState"
                                        cssClass="form-control"
                                        placeholder="Enter state"/>
                        </div>

                        <div class="col-md-6">
                            <label for="billingZip" class="form-label">Zip Code</label>
                            <form:input path="billingAddress.zip" id="billingZip"
                                        cssClass="form-control"
                                        placeholder="Enter zip code"/>
                        </div>
                    </div>

                    <!-- ROW 3 -->
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label for="billingCountry" class="form-label">Country</label>
                            <form:input path="billingAddress.country" id="billingCountry"
                                        cssClass="form-control"
                                        placeholder="Enter country"/>
                        </div>
                    </div>
                </div>

                <!-- SHIPPING ADDRESS -->
                <div class="mt-4">
                    <h6 class="fw-bold text-danger mb-3">– Shipping Address</h6>

                    <!-- ROW 1 -->
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="shippingAddressLine" class="form-label">Address Line 1</label>
                            <form:input path="shippingAddress.addressLine" id="shippingAddressLine"
                                        cssClass="form-control"
                                        placeholder="Enter Address"/>
                        </div>

                        <div class="col-md-6">
                            <label for="shippingCity" class="form-label">City</label>
                            <form:input path="shippingAddress.city" id="shippingCity"
                                        cssClass="form-control"
                                        placeholder="Enter city"/>
                        </div>
                    </div>

                    <!-- ROW 2 -->
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label for="shippingState" class="form-label">State</label>
                            <form:input path="shippingAddress.state" id="shippingState"
                                        cssClass="form-control"
                                        placeholder="Enter state"/>
                        </div>

                        <div class="col-md-6">
                            <label for="shippingZip" class="form-label">Zip Code</label>
                            <form:input path="shippingAddress.zip" id="shippingZip"
                                        cssClass="form-control"
                                        placeholder="Enter zip code"/>
                        </div>
                    </div>

                    <!-- ROW 3 -->
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label for="shippingZip" class="form-label">Country</label>
                            <form:input path="shippingAddress.country" id="shippingCountry"
                                        cssClass="form-control"
                                        placeholder="Enter country"/>
                        </div>
                    </div>
                </div>

                <!-- SUBMIT -->
                <div class="mt-4">
                    <button type="submit" class="btn-primary-custom">
                        Save
                    </button>
                </div>

            </div>

        </form:form>

        <div class="back-link">
            <a href="/customer/list">← Back to List</a>
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
    if (!form) return;

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        function showError(element, message) {
            const small = document.createElement("small");
            small.className = "validation-error text-danger";
            small.style.display = "block";
            small.style.marginTop = "5px";
            small.innerText = message;

            element.closest(".col-md-6, .col-md-12, .input-group").appendChild(small);
            element.focus();
            e.preventDefault();
            return false;
        }

        const name = document.getElementById("name");
        const phone = document.getElementById("phoneNo");
        const partyType = document.getElementById("partyType");
        const balance = document.getElementById("balance");
        const email = document.getElementById("email");
        const creditLimit = document.getElementById("creditLimit");

        const billingCity = document.getElementById("billingCity");
        const billingState = document.getElementById("billingState");
        const billingZip = document.getElementById("billingZip");
        const billingCountry = document.getElementById("billingCountry");

        const letterSpace = /^[A-Za-z ]+$/;
        const phoneRegex = /^[6-9][0-9]{9}$/;
        const numberRegex = /^[0-9]+$/;
        const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (name.value.trim() === "")
            return showError(name, "Customer name is required");

        if (name.value.trim().length < 2 || !letterSpace.test(name.value.trim()))
            return showError(name, "Name must contain only letters and spaces");

        if (!phoneRegex.test(phone.value.trim()))
            return showError(phone, "Enter a valid 10‑digit mobile number");

        if (partyType.value === "")
            return showError(partyType, "Please select a party type");

        if (balance.value.trim() !== "" && !numberRegex.test(balance.value.trim()))
            return showError(balance, "Balance must be a number");

        if (email.value.trim() !== "" && !emailRegex.test(email.value.trim()))
            return showError(email, "Enter a valid email address");

        if (creditLimit.value.trim() !== "" && !numberRegex.test(creditLimit.value.trim()))
            return showError(creditLimit, "Credit limit must be numeric");

        if (billingCity.value.trim() !== "" && !letterSpace.test(billingCity.value.trim()))
            return showError(billingCity, "City must contain only letters");

        if (billingState.value.trim() !== "" && !letterSpace.test(billingState.value.trim()))
            return showError(billingState, "State must contain only letters");

        if (billingCountry.value.trim() !== "" && !letterSpace.test(billingCountry.value.trim()))
            return showError(billingCountry, "Country must contain only letters");

        if (billingZip.value.trim() !== "" && !/^[0-9]{5,6}$/.test(billingZip.value.trim()))
            return showError(billingZip, "Zip code must be 5 or 6 digits");

    });
});
</script>
</body>
</html>