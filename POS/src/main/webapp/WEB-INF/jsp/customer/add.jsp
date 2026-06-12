<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .container {
            max-width: 900px;
            margin: auto;
            background: #fff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        label {
            font-weight: 600;
            margin-top: 12px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
        }

        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .section {
            margin-top: 25px;
            border-top: 1px solid #E5E7EB;
            padding-top: 20px;
        }

        .section h3 {
            margin-bottom: 15px;
        }

        .btn-group {
            margin-top: 25px;
            display: flex;
            gap: 10px;
        }

        .save-btn {
            background: #2B2B2B;
            color: white;
            padding: 12px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            flex: 1;
        }

        .back-btn {
            background: #E5E7EB;
            padding: 12px;
            border-radius: 6px;
            font-weight: 600;
            text-align: center;
            flex: 1;
            text-decoration: none;
            color: #111827;
        }
    </style>
</head>

<body>

<h2>Customer</h2>

<div class="container">


    <form:form action="/customer/add" method="post" modelAttribute="customerDto">



        <label>Email (Identifier)</label>
        <form:input path="identifier" type="email" required="true"/>

        <label>Customer Name</label>
        <form:input path="customerName"/>

        <label>Party Type</label>
        <form:select path="partyType">
            <form:option value="INDIVIDUAL">INDIVIDUAL</form:option>
            <form:option value="BUSINESS">BUSINESS</form:option>
        </form:select>

        <label>Phone Number</label>
        <form:input path="phoneNo"/>

        <div class="grid">
            <div>
                <label>Balance</label>
                <form:input path="balance"/>
            </div>
            <div>
                <label>Credit Limit</label>
                <form:input path="creditLimit"/>
            </div>
        </div>


        <div class="section">
            <h3>Billing Address</h3>

            <form:hidden path="billingAddress.addressType" value="BILLING"/>

            <label>Address Line</label>
            <form:input path="billingAddress.addressLine"/>

            <div class="grid">
                <div>
                    <label>City</label>
                    <form:input path="billingAddress.city"/>
                </div>
                <div>
                    <label>State</label>
                    <form:input path="billingAddress.state"/>
                </div>
            </div>

            <div class="grid">
                <div>
                    <label>Zip Code</label>
                    <form:input path="billingAddress.zipcode"/>
                </div>
                <div>
                    <label>Country</label>
                    <form:input path="billingAddress.country"/>
                </div>
            </div>
        </div>


        <div class="section">
            <h3>Shipping Address</h3>

            <form:hidden path="shippingAddress.addressType" value="SHIPPING"/>

            <label>Address Line</label>
            <form:input path="shippingAddress.addressLine"/>

            <div class="grid">
                <div>
                    <label>City</label>
                    <form:input path="shippingAddress.city"/>
                </div>
                <div>
                    <label>State</label>
                    <form:input path="shippingAddress.state"/>
                </div>
            </div>

            <div class="grid">
                <div>
                    <label>Zip Code</label>
                    <form:input path="shippingAddress.zipcode"/>
                </div>
                <div>
                    <label>Country</label>
                    <form:input path="shippingAddress.country"/>
                </div>
            </div>
        </div>

        <div class="btn-group">
            <button type="submit" class="save-btn">Save</button>
            <a href="/customer/list" class="back-btn">Back</a>
        </div>

    </form:form>

</div>

</body>
</html>
