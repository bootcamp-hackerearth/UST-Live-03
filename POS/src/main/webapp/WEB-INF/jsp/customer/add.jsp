<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Registration</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 40px 0;
        }
        .container {
            max-width: 600px;
            margin: auto;
        }
        .card {
            background: #ffffff;
            padding: 20px;
            border-radius: 6px;
            border: 1px solid #ddd;
        }
        .card-header {
            text-align: center;
            border-bottom: 1px solid #ddd;
            margin-bottom: 20px;
        }
        h4, h5 {
            margin: 10px 0;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
        }
        input, select {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
            margin-bottom: 15px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }
        .btn-submit {
            width: 100%;
            padding: 10px;
            background: #333;
            border: none;
            color: #fff;
            cursor: pointer;
            border-radius: 4px;
        }
        .btn-submit:hover {
            background: #555;
        }
        .alert {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            text-align: center;
        }
        .alert-success {
            background: #e6f4ea;
            color: #1e7e34;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <div class="card-header">
            <h4>Add New Customer</h4>
        </div>

        <c:if test="${not empty customer}">
            <div class="alert alert-success">
                ${customer}
            </div>
        </c:if>

        <form:form action="/customer/add" method="post" modelAttribute="customerDto">

            <label>Name</label>
            <form:input path="name" type="text" title="Enter Name" required="true"/>

            <label>Phone Number</label>
            <form:input
                path="phoneNo"
                type="text"
                maxlength="10"
                pattern="[0-9]{10}"
                title="Enter a 10-digit phone number"
                required="true"
            />

            <label>Email</label>
            <form:input path="identifier" type="email" title="@example.com" required="true"/>

            <label>Party Types</label>
            <form:select path="userType" multiple="true">
                <form:option value="customer">Customer</form:option>
                <form:option value="dealer">Dealer</form:option>
                <form:option value="wholesaler">Wholesaler</form:option>
            </form:select>

            <label>Balance</label>
            <form:input path="balance" type="number" maxlength="10" pattern="[0-9]" required="true"/>

            <label>Credit Limit</label>
            <form:input path="creditLimit" title="Enter Credit Limit" type="number" step="0.01" required="true"/>

            <h5>Shipping Address</h5>

            <label>Address Line</label>
            <form:input path="shippingAddress.addressLine" type="text" rows="2" required="true"/>

            <label>City</label>
            <form:input path="shippingAddress.city" type="text" required="true"/>

            <label>State</label>
            <form:input path="shippingAddress.state" type="text" required="true"/>

            <label>Zip Code</label>
            <form:input path="shippingAddress.zipcode" type="number" minlength="10" required="true"/>

            <label>Country</label>
            <form:input path="shippingAddress.country" type="text" required="true"/>

            <label>Address Types</label>
            <form:select path="shippingAddress.addressType" required="true">
                <form:option value="shippingAddress">Shipping Address</form:option>
            </form:select>

            <h5>Billing Address</h5>

            <label>Address Line</label>
            <form:input path="billingAddress.addressLine" type="text" rows="2" required="true"/>

            <label>City</label>
            <form:input path="billingAddress.city" type="text" required="true"/>

            <label>State</label>
            <form:input path="billingAddress.state" required="true"/>

            <label>Zip Code</label>
            <form:input path="billingAddress.zipcode" type="number" required="true"/>

            <label>Country</label>
            <form:input path="billingAddress.country" type="text" pattern="^[A-Za-z ]{3,50}$"
            title="Country should contain only letters and spaces" required="true"/>

            <label>Address Types</label>
            <form:select path="billingAddress.addressType" required="true">
                <form:option value="billingAddress">Billing Address</form:option>
            </form:select>

            <input type="submit" class="btn-submit"/>

        </form:form>

        <c:if test="${not empty message}">
            <div class="alert" style="background:#f8d7da;color:#721c24;">
                ${message}
            </div>
        </c:if>
    </div>
</div>

</body>
</html>