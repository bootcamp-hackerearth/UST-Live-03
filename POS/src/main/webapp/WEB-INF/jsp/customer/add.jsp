<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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
            position: relative;
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75, 108, 183, 0.08);
            transition: all 0.25s ease;
        }

        .home-link:hover {
            background: #4b6cb7;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
            transform: translateY(-2px);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
        }

        input,
        select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
            box-sizing: border-box;
        }

        select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            background-color: white;
            cursor: pointer;
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

        .address-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
        }

        .btn-submit {
            margin-top: 12px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
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
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
            text-align: center;
            font-size: 13px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/customer/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Add Customer</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form action="/customer/add" method="post" modelAttribute="customerDto">

        <div class="form-group">
            <label>Customer Name</label>
            <form:input path="identifier" required="required"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        required="required"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        title="Enter exactly 10 digit phone number"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="email" required="required" type="email"/>
        </div>

        <div class="form-group">
            <label>Address</label>
            <form:input path="address" required="required"/>
        </div>

        <div class="form-group">
            <label>Party Type</label>
            <form:select path="partyType">
                <form:option value="" label="-- Select Party Type --"/>
                <form:option value="Customer" label="Customer"/>
                <form:option value="Dealer" label="Dealer"/>
                <form:option value="Supplier" label="Supplier"/>
            </form:select>
        </div>

        <details>
            <summary>Billing Address</summary>

            <div class="address-grid">
                <input type="text" name="billing.addressLine"
                       placeholder="Address Line"/>

                <input type="text" name="billing.city"
                       placeholder="City"/>

                <input type="text" name="billing.state"
                       placeholder="State"/>

                <input type="text"
                       name="billing.pincode"
                       placeholder="Pincode"
                       maxlength="6"
                       pattern="[0-9]{6}"
                       required
                       inputmode="numeric"
                       oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

                <input type="text" name="billing.country"
                       placeholder="Country"/>
            </div>
        </details>

        <details>
            <summary>Shipping Address</summary>

            <div class="address-grid">
                <input type="text" name="shipping.addressLine"
                       placeholder="Address Line"/>

                <input type="text" name="shipping.city"
                       placeholder="City"/>

                <input type="text" name="shipping.state"
                       placeholder="State"/>

                <input type="text"
                       name="shipping.pincode"
                       placeholder="Pincode"
                       maxlength="6"
                       pattern="[0-9]{6}"
                       required
                       inputmode="numeric"
                       oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

                <input type="text" name="shipping.country"
                       placeholder="Country"/>
            </div>
        </details>

        <input type="submit" value="Add Customer" class="btn-submit"/>

        <a href="/customer/list" class="btn-cancel">Cancel</a>

    </form:form>

</div>

</body>
</html>