<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Registration</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: radial-gradient(#cbd5e1 0.5px, transparent 0.5px),
                        linear-gradient(135deg, #0f172a, #1e293b);
            background-size: 24px 24px, cover;
            font-family: system-ui, -apple-system, sans-serif;
        }

        .card {
            border-radius: 16px;
            border: none;
            overflow: hidden;
        }

        .card-header {
            background: #0f172a;
            color: white;
            padding: 20px;
            text-align: center;
        }

        label {
            font-weight: 600;
            font-size: 13px;
            color: #475569;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
            padding: 10px 12px;
        }

        hr {
            border-top: 1px dashed #cbd5e1;
        }

        .section-title {
            font-size: 15px;
            font-weight: 700;
            color: #0f172a;
            text-align: center;
            margin: 15px 0;
        }

        .btn-success {
            padding: 10px 30px;
            border-radius: 12px;
            font-weight: 600;
        }

        .card-footer {
            background: #f8fafc;
        }
    </style>
</head>

<body>

<div class="container my-5">
    <div class="card shadow-lg">

        <div class="card-header">
            <h3>Add New Customer</h3>
            <small class="text-light">Customer Registration</small>
        </div>

        <div class="card-body">

            <c:if test="${not empty customer}">
                <div class="alert alert-success text-center">
                    ${customer}
                </div>
            </c:if>

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form:form action="${pageContext.request.contextPath}/customer/add"
                       method="post"
                       modelAttribute="customerDto">

                <div class="row g-3">

                    <div class="section-title">Basic Details</div>

                    <div class="col-md-6">
                        <label>Name</label>
                        <form:input path="name" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Phone Number</label>
                        <form:input path="phoneNo"
                                    type="tel"
                                    class="form-control"
                                    maxlength="10"
                                    required="true"
                                    inputmode="numeric"
                                    pattern="[0-9]{10}"
                                    title="Enter a valid 10-digit mobile number"
                                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>
                    </div>

                    <div class="col-md-6">
                        <label>Email</label>
                        <form:input path="identifier"
                                    type="email"
                                    class="form-control"
                                    required="true"
                                    pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                                    title="Enter a valid Gmail address (example@gmail.com)"/>
                    </div>

                    <div class="col-md-6">
                        <label>Party Types</label>
                        <form:select path="partyType" class="form-select" multiple="true">
                            <form:option value="customer">Customer</form:option>
                            <form:option value="dealer">Dealer</form:option>
                            <form:option value="wholesaler">Wholesaler</form:option>
                        </form:select>
                    </div>

                    <div class="col-md-6">
                        <label>Balance</label>
                        <form:input path="balance" type="number"
                                    class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Credit Limit</label>
                        <form:input path="creditLimit" type="number"
                                    class="form-control" required="true"/>
                    </div>

                    <hr class="mt-4">

                    <div class="section-title">Shipping Address</div>

                    <div class="col-md-12">
                        <label>Address Line</label>
                        <form:input path="shippingAddress.addressLine" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="shippingAddress.city" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="shippingAddress.state" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>Zip Code</label>
                        <form:input path="shippingAddress.zipcode" type="number" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="shippingAddress.country" class="form-control"/>
                    </div>

                    <hr class="mt-4">

                    <div class="section-title">Billing Address</div>

                    <div class="col-md-12">
                        <label>Address Line</label>
                        <form:input path="billingAddress.addressLine" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="billingAddress.city" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="billingAddress.state" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>Zip Code</label>
                        <form:input path="billingAddress.zipcode" type="number" class="form-control"/>
                    </div>

                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="billingAddress.country" class="form-control"/>
                    </div>

                    <div class="col-12 text-center mt-4">
                        <button type="submit" class="btn btn-success">
                            Register Customer
                        </button>
                    </div>

                </div>
            </form:form>
        </div>

        <div class="card-footer text-center">
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary me-2">
                Home
            </a>
            <a href="${pageContext.request.contextPath}/customer/list" class="btn btn-outline-primary">
                View Customers
            </a>
        </div>

    </div>
</div>

</body>
</html>