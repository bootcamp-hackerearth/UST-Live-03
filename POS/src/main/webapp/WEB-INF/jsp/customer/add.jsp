<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #fff;
        }

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
        }

        .content {
            padding: 40px;
        }

        .page-card {
            max-width: 700px;
            margin: auto;
            background: #ffffff;
            border-radius: 18px;
            padding: 28px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        label {
            font-weight: 600;
            margin-bottom: 5px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-bottom: 12px;
            border-radius: 8px;
            border: 1px solid #d1d5db;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            background: #4f46e5;
            border: none;
            color: #fff;
            border-radius: 10px;
        }

        .btn-submit:hover {
            background: #4338ca;
        }

        .alert {
            margin-bottom: 15px;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="topbar">
    <h5>Add Customer</h5>

    <div>
        <button class="dashboard-btn" onclick="window.location.href='/'">
            Dashboard
        </button>

        <form action="/logout" method="post" style="display:inline;">
            <button class="logout-btn">Logout</button>
        </form>
    </div>
</div>

<div class="content">
    <div class="page-card">

        <h4 class="mb-3 text-center">Add New Customer</h4>

        <c:if test="${not empty message}">
            <div class="alert" style="background:#f8d7da;color:#721c24;">
                ${message}
            </div>
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