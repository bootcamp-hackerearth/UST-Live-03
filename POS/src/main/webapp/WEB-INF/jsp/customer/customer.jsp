<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #fff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .section-title {
            font-weight: 600;
            margin-top: 25px;
            margin-bottom: 12px;
            border-bottom: 1px solid #e5e7eb;
            padding-bottom: 6px;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header d-flex justify-content-between align-items-center">
    <h4 class="mb-0">
        <i class="bi bi-pencil-square me-2"></i> Edit Customer
    </h4>

    <a href="${pageContext.request.contextPath}/customer/list"
       class="btn btn-light btn-sm">
        <i class="bi bi-arrow-left-circle me-1"></i> Back to List
    </a>
</div>

<div class="card shadow-sm">
    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   modelAttribute="customerDto"
                   action="${pageContext.request.contextPath}/customer/update">

            <form:hidden path="identifier" />

            <div class="section-title">Customer Details</div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label">Customer Name</label>
                    <form:input path="customerName"
                                class="form-control"
                                readonly="true"/>
                </div>

                <div class="col-md-6">
                    <label class="form-label">Phone Number</label>
                    <form:input path="phoneNo"
                                class="form-control"
                                readonly="true"/>
                </div>
            </div>

            <div class="section-title">Financial Details</div>

            <div class="row mb-3">
                <div class="col-md-3">
                    <label class="form-label">Party Type</label>
                    <form:select path="partyType" class="form-select">
                        <form:option value="Customer">Customer</form:option>
                        <form:option value="Dealer">Dealer</form:option>
                        <form:option value="Wholesaler">Wholesaler</form:option>
                    </form:select>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Credit Type</label>
                    <form:input path="creditType" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Credit</label>
                    <form:input path="credit" class="form-control"/>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Credit Limit</label>
                    <form:input path="creditLimit" class="form-control"/>
                </div>
            </div>

            <div class="section-title">Billing Address</div>
            <form:hidden path="billingAddress.addressType" value="Billing"/>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label">Address Line</label>
                    <form:input path="billingAddress.addressLine" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">City</label>
                    <form:input path="billingAddress.city" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">State</label>
                    <form:input path="billingAddress.state" class="form-control"/>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-3">
                    <label class="form-label">Zip Code</label>
                    <form:input path="billingAddress.zipCode" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Country</label>
                    <form:input path="billingAddress.country" class="form-control"/>
                </div>
            </div>

            <div class="section-title">Shipping Address</div>
            <form:hidden path="shippingAddress.addressType" value="Shipping"/>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label">Address Line</label>
                    <form:input path="shippingAddress.addressLine" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">City</label>
                    <form:input path="shippingAddress.city" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">State</label>
                    <form:input path="shippingAddress.state" class="form-control"/>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-3">
                    <label class="form-label">Zip Code</label>
                    <form:input path="shippingAddress.zipCode" class="form-control"/>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Country</label>
                    <form:input path="shippingAddress.country" class="form-control"/>
                </div>
            </div>

            <div class="text-center">
                <a href="${pageContext.request.contextPath}/customer/list"
                   class="btn btn-secondary">
                    <i class="bi bi-arrow-left-circle"></i> Back
                </a>

                <button type="submit" class="btn btn-primary ms-3">
                    <i class="bi bi-save"></i> Update Customer
                </button>
            </div>

        </form:form>
    </div>
</div>

</body>
</html>