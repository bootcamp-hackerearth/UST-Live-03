<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Customer Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background-color: #f8fafc;
            background-image: radial-gradient(#cbd5e1 0.5px, transparent 0.5px);
            background-size: 24px 24px;
            font-family: system-ui, -apple-system, sans-serif;
        }

        .card {
            border-radius: 16px;
            border: none;
        }

        .card-header {
            background: linear-gradient(135deg, #0f172a, #1e293b);
            color: #fff;
            text-align: center;
            padding: 20px;
        }

        .card-header h3 {
            margin: 0;
            font-weight: 600;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            color: #475569;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
            padding: 10px 12px;
        }

        h5 {
            margin-top: 30px;
            margin-bottom: 15px;
            font-size: 15px;
            font-weight: 700;
            color: #0f172a;
            border-bottom: 1px dashed #cbd5e1;
            padding-bottom: 6px;
        }

        .btn-success {
            border-radius: 12px;
            font-weight: 600;
            padding: 10px;
        }

        .card-footer {
            background: #f8fafc;
            border-top: 1px solid #e2e8f0;
        }
    </style>
</head>

<body>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">

            <div class="card shadow-lg">

                <!-- HEADER -->
                <div class="card-header">
                    <h3>Update Customer</h3>
                    <small class="text-light">Modify Customer Details</small>
                </div>

                <div class="card-body">

                    <c:if test="${not empty customer}">
                        <div class="alert alert-success text-center">
                            ${customer}
                        </div>
                    </c:if>

                    <form:form action="/customer/update" method="post" modelAttribute="customerDto">

                        <h5>Basic Information</h5>

                        <div class="mb-3">
                            <label>Name</label>
                            <form:input path="name" cssClass="form-control" readonly="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Phone Number</label>
                                    <form:input path="phoneNo"
                                                pattern="[0-9]{10}"
                                                maxlength="10"
                                                inputmode="numeric"
                                                title="Enter a valid 10-digit phone number"
                                                oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                                                required="true"
                                                cssClass="form-control"/>
                                    <form:errors path="phoneNo" cssClass="form-control" type="tel"/>
                        </div>

                        <div class="mb-3">
                            <label>Email</label>
                            <form:input path="identifier" cssClass="form-control" readonly="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Party Type</label>
                            <form:select path="partyType" cssClass="form-select" multiple="true">
                                <form:option value="customer">Customer</form:option>
                                <form:option value="dealer">Dealer</form:option>
                                <form:option value="wholesaler">Wholesaler</form:option>
                            </form:select>
                        </div>

                        <div class="mb-3">
                            <label>Balance</label>
                            <form:input path="balance" cssClass="form-control" type="number" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Credit Limit</label>
                            <form:input path="creditLimit" cssClass="form-control" type="number" required="true"/>
                        </div>

                        <h5>Shipping Address</h5>

                        <div class="mb-3">
                            <label>Address Line</label>
                            <form:input path="shippingAddress.addressLine" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>City</label>
                            <form:input path="shippingAddress.city" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>State</label>
                            <form:input path="shippingAddress.state" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Zip Code</label>
                            <form:input path="shippingAddress.zipcode" type="number" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Country</label>
                            <form:input path="shippingAddress.country" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Address Type</label>
                            <form:select path="shippingAddress.addressType" cssClass="form-select">
                                <form:option value="shippingAddress">Shipping Address</form:option>
                            </form:select>
                        </div>

                        <h5>Billing Address</h5>

                        <div class="mb-3">
                            <label>Address Line</label>
                            <form:input path="billingAddress.addressLine" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>City</label>
                            <form:input path="billingAddress.city" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>State</label>
                            <form:input path="billingAddress.state" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Zip Code</label>
                            <form:input path="billingAddress.zipcode" type="number" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Country</label>
                            <form:input path="billingAddress.country" cssClass="form-control"/>
                        </div>

                        <div class="mb-3">
                            <label>Address Type</label>
                            <form:select path="billingAddress.addressType" cssClass="form-select">
                                <form:option value="billingAddress">Billing Address</form:option>
                            </form:select>
                        </div>

                        <div class="d-grid mt-4">
                            <button type="submit" class="btn btn-success">
                                Update Customer
                            </button>
                        </div>

                        <div class="card-footer text-center mt-4">
                            <a href="${pageContext.request.contextPath}/customer/list"
                               class="btn btn-primary">
                                Go Back
                            </a>
                        </div>

                    </form:form>

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger text-center mt-3">
                            ${message}
                        </div>
                    </c:if>

                </div>

                <div class="card-footer text-center">
                    <div class="text-muted small">
                        Customer Management System
                    </div>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>