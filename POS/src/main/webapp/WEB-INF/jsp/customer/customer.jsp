<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        .card-custom {
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
        .btn-custom {
            font-size: 18px;
            padding: 10px 30px;
            border-radius: 30px;
        }
        .section-title {
            font-weight: 600;
            margin-top: 20px;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<div class="container mt-4">
    <div class="card card-custom p-4">

        <h2 class="text-center mb-4">Edit Customer</h2>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form:form method="post"
                   modelAttribute="customerDto"
                   action="${pageContext.request.contextPath}/customer/update">

            <form:hidden path="identifier"/>

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
                    <form:input path="partyType" class="form-control"/>
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

            <div class="row mb-4">
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
                   class="btn btn-secondary btn-custom">
                    <i class="bi bi-arrow-left-circle"></i>
                    Back
                </a>

                <button type="submit"
                        class="btn btn-primary btn-custom ms-3">
                    <i class="bi bi-pencil-square"></i>
                    Update Customer
                </button>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>
