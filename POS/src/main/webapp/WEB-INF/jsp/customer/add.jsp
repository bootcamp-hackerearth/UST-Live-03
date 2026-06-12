<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Customer Registration</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap"
      rel="stylesheet">

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

.glass-card {
    width:900px;
    padding:30px;
    border-radius:15px;

    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);

    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h3, h5 {
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

hr {
    border-top:1px solid rgba(0,255,255,0.3);
}

.btn-success {
    background:#00ffff;
    color:#000;
    border:none;
    padding:10px 25px;
    border-radius:8px;
    font-weight:bold;
    cursor:pointer;
}

.btn-success:hover {
    box-shadow:0 0 15px #00ffff;
}

.alert-success {
    color:#00ff99;
    text-align:center;
}

.alert-danger {
    color:#ff4d4d;
    text-align:center;
}

.footer {
    display:flex;
    justify-content:center;
    gap:15px;
    margin-top:20px;
}

.footer a {
    padding:10px 18px;
    border-radius:8px;
    text-decoration:none;
    font-weight:bold;
}

.home {
    background:#666;
    color:#fff;
}

.view {
    background:#00ffff;
    color:#000;
}
</style>
</head>

<body>

<div class="glass-card">

<h3>Add New Customer</h3>

<c:if test="${not empty customer}">
    <div class="alert-success">${customer}</div>
</c:if>

<c:if test="${not empty message}">
    <div class="alert-danger">${message}</div>
</c:if>

<form:form action="${pageContext.request.contextPath}/customer/add"
           method="post"
           modelAttribute="customerDto">

<label>Name</label>
<form:input path="name" class="form-control"/>

<label>Phone Number</label>
<form:input path="phoneNo" class="form-control"/>

<label>Email</label>
<form:input path="identifier" class="form-control"/>

<label>Party Type</label>
<form:select path="partyType" class="form-select" multiple="true">
    <form:option value="customer">Customer</form:option>
    <form:option value="dealer">Dealer</form:option>
    <form:option value="wholesaler">Wholesaler</form:option>
</form:select>

<label>Balance</label>
<form:input path="balance" class="form-control"/>

<label>Credit Limit</label>
<form:input path="creditLimit" class="form-control"/>

<hr>

<h5>Shipping Address</h5>

<label>Address Line</label>
<form:input path="shippingAddress.addressLine" class="form-control"/>

<label>City</label>
<form:input path="shippingAddress.city" class="form-control"/>

<label>State</label>
<form:input path="shippingAddress.state" class="form-control"/>

<label>Zip Code</label>
<form:input path="shippingAddress.zipcode" class="form-control"/>

<label>Country</label>
<form:input path="shippingAddress.country" class="form-control"/>

<hr>

<h5>Billing Address</h5>

<label>Address Line</label>
<form:input path="billingAddress.addressLine" class="form-control"/>

<label>City</label>
<form:input path="billingAddress.city" class="form-control"/>

<label>State</label>
<form:input path="billingAddress.state" class="form-control"/>

<label>Zip Code</label>
<form:input path="billingAddress.zipcode" class="form-control"/>

<label>Country</label>
<form:input path="billingAddress.country" class="form-control"/>

<div style="text-align:center; margin-top:15px;">
<button class="btn-success">Register Customer</button>
</div>

</form:form>

<div class="footer">
<a href="${pageContext.request.contextPath}/" class="home">Home</a>
<a href="${pageContext.request.contextPath}/customer/list" class="view">View Customers</a>
</div>

</div>

</body>
</html>