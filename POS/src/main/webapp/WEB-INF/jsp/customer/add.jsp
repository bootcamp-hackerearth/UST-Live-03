<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Registration</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 15px;
        }
        label {
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-body">

            <h3 class="text-center mb-4">Add New Customer</h3>

            <!-- SUCCESS MESSAGE -->
            <c:if test="${not empty customer}">
                <div class="alert alert-success text-center">
                    ${customer}
                </div>
            </c:if>

            <!-- ERROR MESSAGE -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <!-- CUSTOMER FORM -->
            <form:form action="${pageContext.request.contextPath}/customer/add"
                       method="post"
                       modelAttribute="customerDto">

                <div class="row g-3">

                    <!-- BASIC DETAILS -->
                    <div class="col-md-6">
                        <label>Name</label>
                        <form:input path="name" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Phone Number</label>
                        <form:input path="phoneNo" type="number"
                                    class="form-control"
                                    title="10 digits" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Email</label>
                        <form:input path="identifier" type="email"
                                    class="form-control"
                                    title="@example.com" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Party Types</label>
                        <form:select path="partyType" class="form-select" required="true" multiple="true">
                            <form:option value="customer">Customer</form:option>
                            <form:option value="dealer">Dealer</form:option>
                            <form:option value="wholesaler">Wholesaler</form:option>
                        </form:select>
                    </div>

                    <div class="col-md-6">
                        <label>Balance</label>
                        <form:input path="balance" type="number" step="0.01"
                                    class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Credit Limit</label>
                        <form:input path="creditLimit"
                                    class="form-control"
                                    type="number"
                                    step="0.01"
                                    required="true"/>
                    </div>

                    <!-- SHIPPING ADDRESS -->
                    <hr class="mt-4">
                    <h5 class="text-center mt-3">Shipping Address</h5>

                    <div class="col-md-12">
                        <label>Address Line</label>
                        <form:input path="shippingAddress.addressLine" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="shippingAddress.city" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="shippingAddress.state" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Zip Code</label>
                        <form:input path="shippingAddress.zipcode" class="form-control" type="number" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="shippingAddress.country" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Address Type</label>
                        <form:select path="shippingAddress.addressType" class="form-select">
                            <form:option value="shippingAddress">Shipping Address</form:option>
                        </form:select>
                    </div>

                    <!-- BILLING ADDRESS -->
                    <hr class="mt-4">
                    <h5 class="text-center mt-3">Billing Address</h5>

                    <div class="col-md-12">
                        <label>Address Line</label>
                        <form:input path="billingAddress.addressLine" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="billingAddress.city" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="billingAddress.state" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Zip Code</label>
                        <form:input path="billingAddress.zipcode" class="form-control" type="number" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="billingAddress.country" class="form-control" required="true"/>
                    </div>

                    <div class="col-md-6">
                        <label>Address Type</label>
                        <form:select path="billingAddress.addressType" class="form-select">
                            <form:option value="billingAddress">Billing Address</form:option>
                        </form:select>
                    </div>

                    <!-- SUBMIT -->
                    <div class="col-12 text-center mt-4">
                        <button type="submit" class="btn btn-success px-5">
                            Register Customer
                        </button>
                    </div>

                </div>
            </form:form>

        </div>

        <!-- FOOTER -->
        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                    Home
                </a>
                <a href="${pageContext.request.contextPath}/customer/list" class="btn btn-primary">
                    View Customers
                </a>
            </div>

            <div class="text-muted small mt-2">
                Customer Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>