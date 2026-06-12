<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f7f6;
        }

        .card {
            width: 500px;
            margin: 60px auto;
            background: #fff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        h4 {
            margin-top: 25px;
            color: #444;
        }

        label {
            display: block;
            margin-top: 12px;
            font-size: 14px;
            font-weight: 600;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #007bff;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background: #28a745;
            border: none;
            color: #fff;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
        }

        .btn-submit:hover {
            background: #218838;
        }

        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 15px;
        }

        .success {
            background: #e6f4ea;
            color: #1e7e34;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 15px;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .back-btn:hover {
            background: #5a6268;
        }

    </style>
</head>

<body>

<a href="/customer/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Edit Customer</h2>

    <!-- SUCCESS -->
    <c:if test="${not empty customer}">
        <div class="success">${customer}</div>
    </c:if>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="/customer/update" method="post" modelAttribute="customerDto">

        <!-- BASIC DETAILS -->
        <label>Name</label>
        <form:input path="customerName" required="true"/>

        <label>Phone Number</label>
        <form:input path="phoneNumber" type="tel" readonly="true"/>

        <label>Email</label>
        <form:input path="identifier" type="email" readonly="true"/>

        <label>Party Type</label>
        <form:select path="partyType">
            <form:option value="CUSTOMER">Customer</form:option>
            <form:option value="DEALER">Dealer</form:option>
            <form:option value="WHOLESALER">Wholesaler</form:option>
        </form:select>

        <label>Balance</label>
        <form:input path="balance" type="number" step="0.01"/>

        <label>Credit Limit</label>
        <form:input path="creditLimit" type="number" step="0.01"/>

        <!-- SHIPPING -->
        <h4>Shipping Address</h4>

        <label>Address Line</label>
        <form:input path="shippingAddress.addressLine"/>

        <label>City</label>
        <form:input path="shippingAddress.city"/>

        <label>State</label>
        <form:input path="shippingAddress.state"/>

        <label>Zip Code</label>
        <form:input path="shippingAddress.zipcode"/>

        <label>Country</label>
        <form:input path="shippingAddress.country"/>

        <!-- hidden type -->
        <form:hidden path="shippingAddress.addressType" value="SHIPPING"/>

        <!-- BILLING -->
        <h4>Billing Address</h4>

        <label>Address Line</label>
        <form:input path="billingAddress.addressLine"/>

        <label>City</label>
        <form:input path="billingAddress.city"/>

        <label>State</label>
        <form:input path="billingAddress.state"/>

        <label>Zip Code</label>
        <form:input path="billingAddress.zipcode"/>

        <label>Country</label>
        <form:input path="billingAddress.country"/>

        <!-- hidden type -->
        <form:hidden path="billingAddress.addressType" value="BILLING"/>

        <input type="submit" value="Save Customer" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>