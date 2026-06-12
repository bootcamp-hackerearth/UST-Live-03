<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update Customer</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

<style>
* {
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body {
    min-height:100vh;
    display:flex;
    justify-content:center;
    align-items:center;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
}

.card-container {
    width:900px;
    padding:30px;
    border-radius:15px;

    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);

    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h2, h5 {
    text-align:center;
    color:#00ffff;
    margin-bottom:15px;
}

label {
    color:#ccc;
    font-size:13px;
}

.form-control, .form-select {
    width:100%;
    padding:10px;
    margin-top:5px;
    margin-bottom:12px;

    border-radius:8px;
    border:none;

    background:rgba(255,255,255,0.1);
    color:#fff;
}

.form-control:focus, .form-select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

.btn-update {
    width:100%;
    padding:12px;
    border:none;
    border-radius:8px;

    background:#00ffff;
    color:#000;
    font-weight:bold;
}

.btn-update:hover {
    box-shadow:0 0 15px #00ffff;
}

.btn-back {
    display:inline-block;
    margin-top:15px;
    padding:10px 20px;
    border-radius:8px;
    text-decoration:none;

    background:#666;
    color:#fff;
}

.alert-success {
    color:#00ff99;
    text-align:center;
    margin-bottom:10px;
}

.alert-danger {
    color:#ff4d4d;
    text-align:center;
    margin-top:10px;
}
</style>
</head>

<body>

<div class="card-container">

<h2>Update Customer</h2>

<c:if test="${not empty customer}">
    <div class="alert-success">${customer}</div>
</c:if>

<form:form action="/customer/update" method="post" modelAttribute="customerDto">

<label>Name</label>
<form:input path="name" cssClass="form-control" readonly="true"/>

<label>Phone Number</label>
<form:input path="phoneNo" cssClass="form-control"/>

<label>Email</label>
<form:input path="identifier" cssClass="form-control" readonly="true"/>

<label>Party Type</label>
<form:select path="partyType" cssClass="form-select" multiple="true">
    <form:option value="customer">Customer</form:option>
    <form:option value="dealer">Dealer</form:option>
    <form:option value="wholesaler">Wholesaler</form:option>
</form:select>

<label>Balance</label>
<form:input path="balance" cssClass="form-control"/>

<label>Credit Limit</label>
<form:input path="creditLimit" cssClass="form-control"/>

<h5>Shipping Address</h5>

<form:input path="shippingAddress.addressLine" cssClass="form-control" placeholder="Address Line"/>
<form:input path="shippingAddress.city" cssClass="form-control" placeholder="City"/>
<form:input path="shippingAddress.state" cssClass="form-control" placeholder="State"/>
<form:input path="shippingAddress.zipcode" cssClass="form-control" placeholder="Zip Code"/>
<form:input path="shippingAddress.country" cssClass="form-control" placeholder="Country"/>

<h5>Billing Address</h5>

<form:input path="billingAddress.addressLine" cssClass="form-control" placeholder="Address Line"/>
<form:input path="billingAddress.city" cssClass="form-control" placeholder="City"/>
<form:input path="billingAddress.state" cssClass="form-control" placeholder="State"/>
<form:input path="billingAddress.zipcode" cssClass="form-control" placeholder="Zip Code"/>
<form:input path="billingAddress.country" cssClass="form-control" placeholder="Country"/>

<button type="submit" class="btn-update">
Update Customer
</button>

</form:form>

<c:if test="${not empty message}">
    <div class="alert-danger">${message}</div>
</c:if>

<div style="text-align:center;">
<a href="${pageContext.request.contextPath}/customer/list" class="btn-back">
Back
</a>
</div>

</div>

</body>
</html>