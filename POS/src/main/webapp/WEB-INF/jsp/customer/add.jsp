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
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            width: 440px;
            padding: 35px 40px;
            border-radius: 16px;
            background: #fff;
            box-shadow: 0 25px 50px rgba(0,0,0,0.25);
        }

        h2 {
            text-align: center;
            color: #4b6cb7;
            margin-bottom: 25px;
        }

        .form-group { margin-bottom: 16px; }

        label {
            font-size: 13px;
            font-weight: 500;
            display: block;
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
            margin-bottom: 8px;
            box-sizing: border-box;
        }

        details {
            margin-top: 15px;
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 12px;
            background: #fafafa;
        }

        summary {
            font-weight: 600;
            color: #4b6cb7;
            cursor: pointer;
            margin-bottom: 12px;
        }

        .btn-submit {
            margin-top: 20px;
            width: 100%;
            padding: 13px;
            border-radius: 10px;
            border: none;
            background: linear-gradient(135deg,#4b6cb7,#182848);
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-cancel {
            margin-top: 10px;
            display: block;
            text-align: center;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border-radius: 10px;
            text-decoration: none;
        }

        .error-message {
            margin-bottom: 16px;
            padding: 10px;
            background: rgba(220,53,69,.15);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="card-container">
    <h2>Add Customer</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <!-- ✅ ONE form → customerDto -->
    <form:form action="/customer/add" method="post" modelAttribute="customerDto">

        <!-- Customer -->
        <div class="form-group">
            <label>Customer Name</label>
            <form:input path="identifier"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneno"/>
        </div>
        <div class="form-group">
            <label>Email</label>
            <form:input path="email"/>
        </div>
        <div class="form-group">
            <label>Address</label>
            <form:input path="address"/>
        </div>
        <div class="form-group">
            <label>Party Type</label>
            <form:select path="partytype">
                <form:option value="" label="-- Select Party Type --"/>
                <form:option value="Customer" label="Customer"/>
                <form:option value="Dealer" label="Dealer"/>
                <form:option value="Supplier" label="Supplier"/>
            </form:select>
        </div>

        <!-- ✅ Billing Address -->
        <details>
            <summary>Billing Address</summary>
            <input type="text"   name="billing.addressLine" placeholder="Address Line"/>
            <input type="text"   name="billing.city"        placeholder="City"/>
            <input type="text"   name="billing.state"       placeholder="State"/>
            <input type="number" name="billing.pincode"     placeholder="Pincode"/>
            <input type="text"   name="billing.country"     placeholder="Country"/>
        </details>

        <!-- ✅ Shipping Address -->
        <details>
            <summary>Shipping Address</summary>
            <input type="text"   name="shipping.addressLine" placeholder="Address Line"/>
            <input type="text"   name="shipping.city"        placeholder="City"/>
            <input type="text"   name="shipping.state"       placeholder="State"/>
            <input type="number" name="shipping.pincode"     placeholder="Pincode"/>
            <input type="text"   name="shipping.country"     placeholder="Country"/>
        </details>

        <input type="submit" value="Add Customer" class="btn-submit"/>
        <a href="/customer/list" class="btn-cancel">Cancel</a>

    </form:form>
</div>

</body>
</html>