<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <title>Add Customer</title>

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

    <div class="card shadow p-4"
         style="width: 700px;">

        <h3 class="text-center mb-4 fw-bold">
            Add Customer
        </h3>

        <c:if test="${not empty customerDto.message}">
            <div class="alert alert-danger text-center">
                ${customerDto.message}
            </div>
        </c:if>

        <form action="/customer/add"
              method="post">

            <h5 class="fw-bold mb-3">
                Customer Details
            </h5>
            <div class="mb-3">
                <label class="form-label">
                    Phone No
                </label>

                <input type="text"
                       name="phoneNo"
                       class="form-control"
                       maxlength="10"
                       pattern="[1-9][0-9]{9}"
                       inputmode="numeric"
                       title="Enter valid 10 digit mobile number"
                       oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                       required>

            </div>

            <div class="mb-3">

                <label class="form-label">
                    Name
                </label>
                <input type="text"
                       name="name"
                       class="form-control"
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
                       name="email"
                       class="form-control"
                       maxlength="100"
                       pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
                       title="Enter valid email"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">
                    Balance
                </label>

                <input type="number"
                       name="balance"
                       class="form-control"
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

                    <option value="CREDIT">
                        Credit
                    </option>

                    <option value="DUE">
                        Debit
                    </option>

                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">
                    Party Type
                </label>

                <input type="text"
                       name="partyType"
                       class="form-control"
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
                       name="creditLimit"
                       class="form-control"
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
                    <option value="true">
                        Active
                    </option>

                    <option value="false">
                        Inactive
                    </option>

                </select>

            </div>

            <h5 class="fw-bold mb-3">
                Billing Address
            </h5>

            <input type="text"
                   id="billAddressLine"
                   name="billingAddress.addressLine"
                   class="form-control mb-3"
                   placeholder="Address Line"
                   required>

            <input type="text"
                   id="billCity"
                   name="billingAddress.city"
                   class="form-control mb-3"
                   placeholder="City"
                   required>

            <input type="text"
                   id="billState"
                   name="billingAddress.state"
                   class="form-control mb-3"
                   placeholder="State"
                   required>

            <input type="text"
                   id="billZip"
                   name="billingAddress.zip"
                   class="form-control mb-3"
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
                   class="form-control mb-4"
                   placeholder="Country"
                   required>

            <div class="form-check mb-4">

                <input class="form-check-input"
                       type="checkbox"
                       id="sameAsBilling">
                <label class="form-check-label fw-bold" for="sameAsBilling">
                    Shipping same as Billing Address
                </label>

            </div>
            <h5 class="fw-bold mb-3">
                Shipping Address
            </h5>

            <input type="text"
                   id="shipAddressLine"
                   name="shippingAddress.addressLine"
                   class="form-control mb-3"
                   placeholder="Address Line"
                   required>

            <input type="text"
                   id="shipCity"
                   name="shippingAddress.city"
                   class="form-control mb-3"
                   placeholder="City"
                   required>

            <input type="text"
                   id="shipState"
                   name="shippingAddress.state"
                   class="form-control mb-3"
                   placeholder="State"
                   required>

            <input type="text"
                   id="shipZip"
                   name="shippingAddress.zip"
                   class="form-control mb-3"
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
                   placeholder="Country"
                   required>
            <div class="d-flex gap-2">

                <button type="submit" class="btn btn-primary w-100">
                    Save
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
        document.getElementById("shipAddressLine").value = document.getElementById("billAddressLine").value;
        document.getElementById("shipCity").value = document.getElementById("billCity").value;
        document.getElementById("shipState").value = document.getElementById("billState").value;
        document.getElementById("shipZip").value = document.getElementById("billZip").value;
        document.getElementById("shipCountry").value = document.getElementById("billCountry").value;

    } else {
        document.getElementById("shipAddressLine").value = "";
        document.getElementById("shipCity").value = "";
        document.getElementById("shipState").value = "";
        document.getElementById("shipZip").value = "";
        document.getElementById("shipCountry").value = "";
    }

});

</script>

</body>
</html>