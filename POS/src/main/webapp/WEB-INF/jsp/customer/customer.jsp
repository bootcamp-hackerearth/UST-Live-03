<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Update Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>

        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }

        h5 {
            margin-top: 10px;
        }

    </style>

</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">

    <div class="container-fluid">

        <span class="navbar-brand fw-bold">
            Customer Management
        </span>

        <a href="/customer/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>

    </div>

</nav>

<div class="container d-flex justify-content-center align-items-center"
     style="min-height: 100vh;">

    <div class="card shadow p-4" style="width: 750px;">

        <h3 class="text-center mb-4 fw-bold">
            Update Customer
        </h3>

        <c:if test="${not empty customerDto.message}">
            <div class="alert alert-danger text-center">
                ${customerDto.message}
            </div>
        </c:if>

        <form action="/customer/update" method="post">
            <input type="hidden"
                   name="identifier"
                   value="${customerDto.identifier}" />

            <h5 class="fw-bold">Customer Details</h5>
            <hr>
            <div class="mb-3">
                <label class="form-label">
                    Phone No
                </label>
                <input type="text"
                       class="form-control"
                       name="phoneNo"
                       value="${customerDto.phoneNo}"
                       maxlength="10"
                       pattern="[1-9][0-9]{9}"
                       inputmode="numeric"
                       title="Enter valid 10 digit mobile number"
                       oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                       readonly>
            </div>
            <div class="mb-3">
                <label class="form-label">
                    Name
                </label>
                <input type="text"
                       class="form-control"
                       name="name"
                       value="${customerDto.name}"
                       maxlength="100"
                       pattern="^[A-Za-z ]+$"
                       title="Only alphabets allowed"
                       oninput="this.value=this.value.replace(/[^A-Za-z ]/g,'')"
                       required>
            </div>
            <div class="mb-3">
                <label class="form-label">
                    Email
                </label>
                <input type="email"
                       class="form-control"
                       name="email"
                       value="${customerDto.email}"
                       maxlength="100"
                       required>
            </div>
            <div class="mb-3">
                <label class="form-label">
                    Balance
                </label>
                <input type="number"
                       class="form-control"
                       name="balance"
                       value="${customerDto.balance}"
                       min="0"
                       step="0.01"
                       oninput="if(this.value < 0) this.value = 0"
                       required>
            </div>
            <div class="mb-3">
                <label class="form-label">
                    Balance Type
                </label>
                <select name="balanceType"
                        class="form-select">
                    <option value="CR"
                        <c:if test="${customerDto.balanceType == 'CR'}">
                            selected
                        </c:if>>
                        Credit
                    </option>
                    <option value="DR"
                        <c:if test="${customerDto.balanceType == 'DR'}">
                            selected
                        </c:if>>
                        Debit
                    </option>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">
                    Party Type
                </label>
                <input type="text"
                       class="form-control"
                       name="partyType"
                       value="${customerDto.partyType}"
                       maxlength="50"
                       pattern="^[A-Za-z ]+$"
                       title="Only alphabets allowed"
                       oninput="this.value=this.value.replace(/[^A-Za-z ]/g,'')"
                       required>
            </div>

            <div class="mb-4">
                <label class="form-label">
                    Credit Limit
                </label>
                <input type="number"
                       class="form-control"
                       name="creditLimit"
                       value="${customerDto.creditLimit}"
                       min="0"
                       step="0.01"
                       oninput="if(this.value < 0) this.value = 0"
                       required>
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">
                    Status
                </label>
                <select name="status" class="form-select">
                    <option value="true"
                        <c:if test="${customerDto.status}">
                            selected
                        </c:if>>
                        Active
                    </option>
                    <option value="false"
                        <c:if test="${!customerDto.status}">
                            selected
                        </c:if>>
                        Inactive
                    </option>
                </select>
            </div>

            <h5 class="fw-bold">
                Billing Address
            </h5>

            <hr>

            <input type="text"
                   id="billAddress"
                   name="billingAddress.addressLine"
                   class="form-control mb-2"
                   value="${customerDto.billingAddress.addressLine}"
                   placeholder="Address Line"
                   required>

            <input type="text"
                   id="billCity"
                   name="billingAddress.city"
                   class="form-control mb-2"
                   value="${customerDto.billingAddress.city}"
                   placeholder="City"
                   required>

            <input type="text"
                   id="billState"
                   name="billingAddress.state"
                   class="form-control mb-2"
                   value="${customerDto.billingAddress.state}"
                   placeholder="State"
                   required>

            <input type="text"
                   id="billZip"
                   name="billingAddress.zip"
                   class="form-control mb-2"
                   value="${customerDto.billingAddress.zip}"
                   placeholder="PIN Code"
                   maxlength="6"
                   pattern="[1-9][0-9]{5}"
                   inputmode="numeric"
                   title="Enter valid 6 digit PIN code"
                   oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                   required>

            <input type="text"
                   id="billCountry"
                   name="billingAddress.country"
                   class="form-control mb-3"
                   value="${customerDto.billingAddress.country}"
                   placeholder="Country"
                   required>

            <div class="form-check mb-4">

                <input class="form-check-input"
                       type="checkbox"
                       id="sameAsBilling">

                <label class="form-check-label fw-bold">
                    Shipping same as Billing Address
                </label>

            </div>

            <h5 class="fw-bold">
                Shipping Address
            </h5>

            <hr>

            <input type="text"
                   id="shipAddress"
                   name="shippingAddress.addressLine"
                   class="form-control mb-2"
                   value="${customerDto.shippingAddress.addressLine}"
                   placeholder="Address Line"
                   required>

            <input type="text"
                   id="shipCity"
                   name="shippingAddress.city"
                   class="form-control mb-2"
                   value="${customerDto.shippingAddress.city}"
                   placeholder="City"
                   required>

            <input type="text"
                   id="shipState"
                   name="shippingAddress.state"
                   class="form-control mb-2"
                   value="${customerDto.shippingAddress.state}"
                   placeholder="State"
                   required>

            <input type="text"
                   id="shipZip"
                   name="shippingAddress.zip"
                   class="form-control mb-2"
                   value="${customerDto.shippingAddress.zip}"
                   placeholder="PIN Code"
                   maxlength="6"
                   pattern="[1-9][0-9]{5}"
                   inputmode="numeric"
                   title="Enter valid 6 digit PIN code"
                   oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                   required>

            <input type="text"
                   id="shipCountry"
                   name="shippingAddress.country"
                   class="form-control mb-4"
                   value="${customerDto.shippingAddress.country}"
                   placeholder="Country"
                   required>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary w-100">
                    Update Customer
                </button>

                <a href="/customer/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>
        </form>
    </div>

</div>

<script>

document.getElementById("sameAsBilling")
    .addEventListener("change", function () {

    if (this.checked) {
        document.getElementById("shipAddress").value = document.getElementById("billAddress").value;
        document.getElementById("shipCity").value = document.getElementById("billCity").value;
        document.getElementById("shipState").value = document.getElementById("billState").value;
        document.getElementById("shipZip").value = document.getElementById("billZip").value;
        document.getElementById("shipCountry").value = document.getElementById("billCountry").value;
    } else {
        document.getElementById("shipAddress").value = "";
        document.getElementById("shipCity").value = "";
        document.getElementById("shipState").value = "";
        document.getElementById("shipZip").value = "";
        document.getElementById("shipCountry").value = "";
    }
});

</script>

</body>
</html>