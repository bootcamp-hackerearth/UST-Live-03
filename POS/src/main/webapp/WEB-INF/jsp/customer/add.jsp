<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

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
            padding-bottom: 6px;
            border-bottom: 1px solid #ddd;
        }

        label {
            font-weight: 500;
            margin-bottom: 4px;
        }

        .form-control, .form-select {
            border-radius: 6px;
            padding: 8px;
        }

        .btn-custom {
            border-radius: 6px;
            padding: 8px 18px;
        }

        .checkbox-inline {
            display: flex;
            align-items: center;
            gap: 6px;
        }
    </style>
</head>

<body>

<div class="container mt-4">

    <div class="page-header d-flex justify-content-between align-items-center">
        <h4 class="mb-0">Add Customer</h4>

        <div>
            <a href="${pageContext.request.contextPath}/"
               class="btn btn-light btn-sm me-2">
                <i class="bi bi-house-door"></i> Home
            </a>

            <a href="${pageContext.request.contextPath}/customer/list"
               class="btn btn-light btn-sm">
                <i class="bi bi-list"></i> List
            </a>
        </div>
    </div>

    <div class="card shadow p-4">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">${message}</div>
        </c:if>

        <form:form method="post"
                   modelAttribute="customerDto"
                   action="${pageContext.request.contextPath}/customer/add">

            <div class="section-title">Customer Details</div>

            <div class="row g-3">
                <div class="col-md-6">
                    <label>Customer Name *</label>
                    <form:input path="customerName" class="form-control" required="true"/>
                </div>

                <div class="col-md-6">
                    <label>Phone *</label>
                    <form:input path="phoneNo" class="form-control" required="true"/>
                </div>
            </div>

            <div class="section-title">Financial Details</div>

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

                <div class="checkbox-inline">
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
                   class="btn btn-secondary btn-custom">
                    Back
                </a>

                <button class="btn btn-success btn-custom ms-2">
                    <i class="bi bi-save"></i> Save
                </button>
            </div>

        </form:form>

    </div>
</div>

<script>
document.getElementById("sameAddress").addEventListener("change", function () {
    if (this.checked) {
        document.getElementById("ship_address").value =
            document.getElementById("bill_address").value;

        document.getElementById("ship_city").value =
            document.getElementById("bill_city").value;

        document.getElementById("ship_state").value =
            document.getElementById("bill_state").value;

        document.getElementById("ship_zip").value =
            document.getElementById("bill_zip").value;

        document.getElementById("ship_country").value =
            document.getElementById("bill_country").value;
    }
});
</script>

</body>
</html>
