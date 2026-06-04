<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Update Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body { background-color: #f8fafc; }
        .card { border-radius: 12px; }
        .card-header {
            background: #1e293b;
            color: #fff;
            text-align: center;
            padding: 15px;
        }
    </style>
</head>

<body>

<div class="container mt-5">
<div class="row justify-content-center">
<div class="col-lg-8">

<div class="card shadow">

<div class="card-header">
    <h3>Update Customer</h3>
</div>

<div class="card-body">

<c:if test="${not empty message}">
    <div class="alert alert-danger text-center">
        ${message}
    </div>
</c:if>

<form:form action="/customer/update" method="post" modelAttribute="customerDto">

    <form:hidden path="shippingAddress.addressType" value="shipping"/>
    <form:hidden path="billingAddress.addressType" value="billing"/>

    <h5>Basic Information</h5>

    <div class="mb-3">
        <label>Name</label>
        <form:input path="name" cssClass="form-control" readonly="true"/>
    </div>

    <div class="mb-3">
        <label>Phone</label>
        <form:input path="phoneNo" cssClass="form-control" required="true"/>
    </div>

    <div class="mb-3">
        <label>Email</label>
        <form:input path="identifier" cssClass="form-control" readonly="true"/>
    </div>

    <div class="mb-3">
        <label>Party Type</label>
        <form:select path="partyType" cssClass="form-select">
            <form:option value="customer">Customer</form:option>
            <form:option value="dealer">Dealer</form:option>
            <form:option value="wholesaler">Wholesaler</form:option>
        </form:select>
    </div>

    <div class="mb-3">
        <label>Balance</label>
        <form:input path="balance" type="number" cssClass="form-control"/>
    </div>

    <div class="mb-3">
        <label>Credit Limit</label>
        <form:input path="creditLimit" type="number" cssClass="form-control"/>
    </div>

    <h5>Shipping Address</h5>

    <form:input path="shippingAddress.addressLine" cssClass="form-control mb-2" placeholder="Address"/>
    <form:input path="shippingAddress.city" cssClass="form-control mb-2" placeholder="City"/>
    <form:input path="shippingAddress.state" cssClass="form-control mb-2" placeholder="State"/>
    <form:input path="shippingAddress.zipcode" cssClass="form-control mb-2" placeholder="Zip"/>
    <form:input path="shippingAddress.country" cssClass="form-control mb-2" placeholder="Country"/>

    <h5>Billing Address</h5>

    <form:input path="billingAddress.addressLine" cssClass="form-control mb-2" placeholder="Address"/>
    <form:input path="billingAddress.city" cssClass="form-control mb-2" placeholder="City"/>
    <form:input path="billingAddress.state" cssClass="form-control mb-2" placeholder="State"/>
    <form:input path="billingAddress.zipcode" cssClass="form-control mb-2" placeholder="Zip"/>
    <form:input path="billingAddress.country" cssClass="form-control mb-2" placeholder="Country"/>

    <div class="d-grid mt-4">
        <button type="submit" class="btn btn-success">Update</button>
    </div>

</form:form>

</div>
</div>
</div>
</div>
</div>

</body>
</html>