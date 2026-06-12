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
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
            background: #fff8f0;
        }

        .blob {
            display: none;
        }

        .main-container {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .form-card {
            width: 720px;
            padding: 40px;
            border-radius: 18px;
            background: #efe3d9;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        .form-card h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #4a2e2b;
            font-weight: 700;
        }

        .form-label,
        h6 {
            color: #4a2e2b;
            font-weight: 600;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
            border: 1px solid #d6c2b8;
            padding: 10px 12px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #6b4a46;
            box-shadow: 0 0 0 3px rgba(107, 74, 70, 0.2);
        }

        .form-control[readonly] {
            background-color: #f6ede7;
            color: #6b4a46;
        }

        h6.text-danger {
            color: #6b4a46 !important;
        }

        .btn-primary-custom {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;
            background-color: #6b4a46;
            color: #fff8f0;
            font-weight: 600;
            transition: 0.2s ease;
        }

        .btn-primary-custom:hover {
            background-color: #543835;
        }

        .back-link {
            text-align: center;
            margin-top: 18px;
        }

        .back-link a {
            color: #6b4a46;
            text-decoration: none;
            font-weight: 500;
        }

        .back-link a:hover {
            color: #543835;
            text-decoration: underline;
        }

        .custom-toast {
            position: fixed;
            bottom: 30px;
            left: 50%;
            transform: translateX(-50%) translateY(20px);
            min-width: 260px;
            padding: 14px 18px;
            border-radius: 14px;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
            color: #4a2e2b;
            background: rgba(255, 248, 240, 0.95);
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
            z-index: 9999;
            opacity: 0;
            animation: toastIn 0.4s ease forwards;
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
        <form:form method="post" action="/customer/update" modelAttribute="customerDto">
            <div class="container-fluid p-0">
                <form:hidden path="identifier"/>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Customer Name</label>
                        <form:input path="name"
                                    cssClass="form-control"
                                    placeholder="Enter Customer Name"
                        />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Phone Number</label>
                        <form:input path="phoneNo"
                                    cssClass="form-control"
                                    placeholder="Enter Phone Number"
                                    readonly="true"
                        />
                    </div>
                </div>
                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Party Type</label>
                        <form:select path="partyType" cssClass="form-control">
                            <form:option value="">Select one</form:option>
                            <form:option value="Customer">Customer</form:option>
                            <form:option value="Dealer">Dealer</form:option>
                            <form:option value="Wholesaler">Wholesaler</form:option>
                        </form:select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Balance</label>
                        <div class="input-group">
                            <form:input path="balance"
                                        cssClass="form-control"
                                        placeholder="Ex: 500"
                            />
                            <form:select path="balanceType" cssClass="form-select" style="max-width: 120px;">
                                <form:option value="Due">Due</form:option>
                                <form:option value="Advance">Advance</form:option>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Email</label>
                        <form:input path="email"
                                    cssClass="form-control"
                                    placeholder="Enter Email"
                        />
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Party Credit Limit</label>
                        <form:input path="creditLimit"
                                    cssClass="form-control"
                                    placeholder="Ex: 800"
                        />
                    </div>
                </div>
                <div class="mt-4">
                    <h6 class="fw-bold text-danger mb-3">– Billing Address</h6>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Address Line 1</label>
                            <form:input path="billingAddress.addressLine"
                                        cssClass="form-control"
                                        placeholder="Enter Address"
                            />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">City</label>
                            <form:input path="billingAddress.city"
                                        cssClass="form-control"
                                        placeholder="Enter city"
                            />
                        </div>
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label class="form-label">State</label>
                            <form:input path="billingAddress.state"
                                        cssClass="form-control"
                                        placeholder="Enter state"
                            />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Zip Code</label>
                            <form:input path="billingAddress.zip"
                                        cssClass="form-control"
                                        placeholder="Enter zip code"
                            />
                        </div>
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label class="form-label">Country</label>
                            <form:input path="billingAddress.country"
                                        cssClass="form-control"
                                        placeholder="Enter country"
                            />
                        </div>
                    </div>
                </div>
                <div class="mt-4">
                    <h6 class="fw-bold text-danger mb-3">– Shipping Address</h6>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Address Line 1</label>
                            <form:input path="shippingAddress.addressLine"
                                        cssClass="form-control"
                                        placeholder="Enter Address"
                            />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">City</label>
                            <form:input path="shippingAddress.city"
                                        cssClass="form-control"
                                        placeholder="Enter city"
                            />
                        </div>
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label class="form-label">State</label>
                            <form:input path="shippingAddress.state"
                                        cssClass="form-control"
                                        placeholder="Enter state"
                            />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Zip Code</label>
                            <form:input path="shippingAddress.zip"
                                        cssClass="form-control"
                                        placeholder="Enter zip code"
                            />
                        </div>
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-md-6">
                            <label class="form-label">Country</label>
                            <form:input path="shippingAddress.country"
                                        cssClass="form-control"
                                        placeholder="Enter country"
                            />
                        </div>
                    </div>
                </div>
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
</body>
</html>