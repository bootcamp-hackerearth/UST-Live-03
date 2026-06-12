<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Management</title>

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
        .form-control, .form-select {
            border-radius: 8px;
        }
        label {
            font-weight: 500;
        }
        h5 {
            margin-top: 25px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">

            <div class="card shadow-lg">
                <div class="card-body">

                    <h3 class="text-center mb-4">Update Customer</h3>

                    <!-- SUCCESS MESSAGE -->
                    <c:if test="${not empty customer}">
                        <div class="alert alert-success text-center">
                            ${customer}
                        </div>
                    </c:if>

                    <form:form action="/customer/update" method="post" modelAttribute="customerDto">

                        <!-- BASIC INFO -->
                        <div class="mb-3">
                            <label>Name</label>
                            <form:input path="name" cssClass="form-control" readonly="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Phone Number</label>
                            <form:input path="phoneNo" cssClass="form-control" type="tel" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Email</label>
                            <form:input path="identifier" cssClass="form-control" readonly="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Party Type</label>
                            <form:select path="partyType" cssClass="form-select" required="true" multiple="true">
                                <form:option value="customer">Customer</form:option>
                                <form:option value="dealer">Dealer</form:option>
                                <form:option value="wholesaler">Wholesaler</form:option>
                            </form:select>
                        </div>

                        <div class="mb-3">
                            <label>Balance</label>
                            <form:input path="balance" cssClass="form-control" type="number" step="0.01" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Credit Limit</label>
                            <form:input path="creditLimit" cssClass="form-control" type="number" step="0.01" required="true"/>
                        </div>

                        <!-- SHIPPING ADDRESS -->
                        <h5>Shipping Address</h5>


                        <div class="mb-3">
                            <label>Address Line</label>
                            <form:input path="shippingAddress.addressLine" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>City</label>
                            <form:input path="shippingAddress.city" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>State</label>
                            <form:input path="shippingAddress.state" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Zip Code</label>
                            <form:input path="shippingAddress.zipcode" cssClass="form-control" type="number" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Country</label>
                            <form:input path="shippingAddress.country" cssClass="form-control" required="true" />
                        </div>

                        <div class="mb-3">
                            <label>Address Type</label>
                            <form:select path="shippingAddress.addressType" cssClass="form-select">
                                <form:option value="shippingAddress">Shipping Address</form:option>
                            </form:select>
                        </div>

                        <!-- BILLING ADDRESS -->
                        <h5>Billing Address</h5>


                        <div class="mb-3">
                            <label>Address Line</label>
                            <form:input path="billingAddress.addressLine" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>City</label>
                            <form:input path="billingAddress.city" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>State</label>
                            <form:input path="billingAddress.state" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Zip Code</label>
                            <form:input path="billingAddress.zipcode" cssClass="form-control" type="number" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Country</label>
                            <form:input path="billingAddress.country" cssClass="form-control" required="true"/>
                        </div>

                        <div class="mb-3">
                            <label>Address Type</label>
                            <form:select path="billingAddress.addressType" cssClass="form-select">
                                <form:option value="billingAddress">Billing Address</form:option>
                            </form:select>
                        </div>

                        <!-- SUBMIT -->
                        <div class="d-grid mt-4">
                            <button type="submit" class="btn btn-success">
                                Update Customer
                            </button>
                        </div>

                        <div class="card-footer text-center">
                              <div class="d-flex justify-content-center gap-3">
                                    <a href="${pageContext.request.contextPath}/customer/list" class="btn btn-primary">
                                         Go Back
                                    </a>
                              </div>
                        </div>

                    </form:form>

                    <!-- ERROR MESSAGE -->
                    <c:if test="${not empty message}">
                        <div class="alert alert-danger text-center mt-3">
                            ${message}
                        </div>
                    </c:if>

                </div>

                <!-- FOOTER -->
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