<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", sans-serif;
            padding: 40px;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .container {
            max-width: 750px;
            margin: auto;
        }

        .card {
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            color: #fff;
            margin-bottom: 20px;
        }

        h5 {
            color: #ffb3b3;
            margin-top: 15px;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 5px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            background: rgba(255,255,255,0.1);
            color: #fff;
            margin-bottom: 12px;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 14px;
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .alert {
            margin-bottom: 15px;
            text-align: center;
            color: #ffb3b3;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card">

        <h4>Add New Customer</h4>

        <c:if test="${not empty message}">
            <div class="alert">${message}</div>
        </c:if>

        <form:form action="/customer/add" method="post" modelAttribute="customerDto">

            <label>Name</label>
            <form:input path="name"/>

            <label>Phone Number</label>
            <form:input path="phoneNo"/>

            <label>Email</label>
            <form:input path="identifier"/>

            <label>Party Types</label>
            <form:select path="userType" multiple="true">
                <form:option value="customer">Customer</form:option>
                <form:option value="dealer">Dealer</form:option>
                <form:option value="wholesaler">Wholesaler</form:option>
            </form:select>

            <label>Balance</label>
            <form:input path="balance"/>

            <label>Credit Limit</label>
            <form:input path="creditLimit"/>

            <h5>Shipping Address</h5>

            <form:input path="shippingAddress.addressline" placeholder="Address"/>
            <form:input path="shippingAddress.city" placeholder="City"/>
            <form:input path="shippingAddress.state" placeholder="State"/>
            <form:input path="shippingAddress.zipcode" placeholder="Zip"/>
            <form:input path="shippingAddress.country" placeholder="Country"/>

            <label>Address Type</label>
            <form:select path="shippingAddress.addressType">
                <form:option value="shippingAddress">Shipping Address</form:option>
            </form:select>

            <h5>Billing Address</h5>

            <form:input path="billingAddress.addressline" placeholder="Address"/>
            <form:input path="billingAddress.city" placeholder="City"/>
            <form:input path="billingAddress.state" placeholder="State"/>
            <form:input path="billingAddress.zipcode" placeholder="Zip"/>
            <form:input path="billingAddress.country" placeholder="Country"/>

            <label>Address Type</label>
            <form:select path="billingAddress.addressType">
                <form:option value="billingAddress">Billing Address</form:option>
            </form:select>

            <button class="btn-submit">Add Customer</button>

        </form:form>

    </div>
</div>

</body>
</html>