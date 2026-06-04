<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Arial;
        }

        .page-header {
            background-color: #000;
            color: #fff;
            padding: 14px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #ddd;
        }

        .section-title {
            font-weight: 600;
            margin-top: 25px;
            margin-bottom: 15px;
            border-bottom: 1px solid #ddd;
            padding-bottom: 6px;
        }

        label {
            font-weight: 500;
            margin-bottom: 4px;
        }

        .form-control,
        .form-select {
            border-radius: 6px;
            padding: 8px;
        }

        .btn-custom {
            border-radius: 6px;
            padding: 8px 18px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h4 class="mb-0">Edit Customer</h4>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-light btn-sm me-2">Home</a>

            <a href="${pageContext.request.contextPath}/customer/list"
               class="btn btn-light btn-sm">List</a>
        </div>
    </div>

    <div class="card shadow p-4">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form modelAttribute="customerDto"
                   method="post"
                   action="${pageContext.request.contextPath}/customer/update">

            <form:hidden path="identifier"/>

            <div class="section-title">Customer Details</div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label>Customer Name</label>
                    <form:input path="customerName" class="form-control" readonly="true"/>
                </div>

                <div class="col-md-6">
                    <label>Phone</label>
                    <form:input path="phoneNo" class="form-control" readonly="true"/>
                </div>
            </div>

            <div class="section-title">Financial</div>

            <div class="row g-3">
                <div class="col-md-3">
                    <label>Party Type</label>
                    <form:select path="partyType" class="form-select">
                        <form:option value="">--Select--</form:option>
                        <form:option value="CUSTOMER">Customer</form:option>
                        <form:option value="WHOLESALER">Wholesaler</form:option>
                        <form:option value="DEALER">Dealer</form:option>
                    </form:select>
                </div>

                <div class="col-md-3">
                    <label>Credit Type</label>
                    <form:input path="creditType" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>Credit</label>
                    <form:input path="credit" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>Credit Limit</label>
                    <form:input path="creditLimit" class="form-control"/>
                </div>
            </div>

            <div class="section-title">Billing Address</div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label>Address</label>
                    <form:input path="billingAddress.addressLine" id="bill_address" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>City</label>
                    <form:input path="billingAddress.city" id="bill_city" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>State</label>
                    <form:input path="billingAddress.state" id="bill_state" class="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-2">
                <div class="col-md-3">
                    <label>Zip</label>
                    <form:input path="billingAddress.zipCode" id="bill_zip" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>Country</label>
                    <form:input path="billingAddress.country" id="bill_country" class="form-control"/>
                </div>
            </div>

            <div class="section-title d-flex justify-content-between align-items-center">
                Shipping Address
                <div>
                    <input type="checkbox" id="sameAddress"/>
                    <label for="sameAddress">Same as Billing</label>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label>Address</label>
                    <form:input path="shippingAddress.addressLine" id="ship_address" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>City</label>
                    <form:input path="shippingAddress.city" id="ship_city" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>State</label>
                    <form:input path="shippingAddress.state" id="ship_state" class="form-control"/>
                </div>
            </div>

            <div class="row g-3 mt-2">
                <div class="col-md-3">
                    <label>Zip</label>
                    <form:input path="shippingAddress.zipCode" id="ship_zip" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label>Country</label>
                    <form:input path="shippingAddress.country" id="ship_country" class="form-control"/>
                </div>
            </div>

            <div class="text-center mt-4">
                <a href="${pageContext.request.contextPath}/customer/list"
                   class="btn btn-secondary btn-custom">Back</a>

                <button type="submit" class="btn btn-success btn-custom ms-2">
                    Update
                </button>
            </div>

        </form:form>

    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function () {

    const checkbox = document.getElementById("sameAddress");

    const bill = {
        address: document.getElementById("bill_address"),
        city: document.getElementById("bill_city"),
        state: document.getElementById("bill_state"),
        zip: document.getElementById("bill_zip"),
        country: document.getElementById("bill_country")
    };

    const ship = {
        address: document.getElementById("ship_address"),
        city: document.getElementById("ship_city"),
        state: document.getElementById("ship_state"),
        zip: document.getElementById("ship_zip"),
        country: document.getElementById("ship_country")
    };

    function copyAddress() {
        ship.address.value = bill.address.value;
        ship.city.value = bill.city.value;
        ship.state.value = bill.state.value;
        ship.zip.value = bill.zip.value;
        ship.country.value = bill.country.value;
    }

    checkbox.addEventListener("change", function () {
        if (this.checked) {
            copyAddress();
        }
    });

});
</script>

</body>
</html>