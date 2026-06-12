<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CARD ===== */
        .card {
            max-width: 1000px;
            background: #ffffff;
            margin: 40px auto;
            padding: 22px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            position: relative;
        }


        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            background: #ffffff;
            border: 1px solid teal;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
            color: teal;
            text-decoration: none;
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
            font-size: 20px;
        }

        .section {
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            padding: 16px;
            margin-bottom: 18px;
        }

        .section-title {
            font-size: 15px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 14px;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 14px;
        }

        label {
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 4px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 8px 10px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        input[readonly] {
            background: #f1f5f9;
        }

        .submit-btn {
            margin-top: 10px;
            width: 100%;
            padding: 10px;
            background: teal;
            color: white;
            border: none;
            border-radius: 18px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card">


    <div class="app-title">POS Application</div>


    <a href="${pageContext.request.contextPath}/customer/list" class="back-btn">Back</a>

    <h2>Edit Customer</h2>


    <form:form method="post"
               action="${pageContext.request.contextPath}/customer/update"
               modelAttribute="customer">

        <form:hidden path="id"/>

        <!-- CUSTOMER DETAILS -->
        <div class="section">
            <div class="section-title">Customer Details</div>

            <div class="grid">

                <div>
                    <label>Phone Number</label>
                    <form:input path="identifier"
                                readonly="true"
                                maxlength="13"
                                inputmode="tel"
                                pattern="[0-9+-]+"/>
                </div>

                <div>
                    <label>Customer Name</label>
                    <form:input path="customerName" required="true"/>
                </div>

                <div>
                    <label>Email</label>
                    <form:input path="email" type="email" required="true"/>
                </div>

                <div>
                    <label>Party Type</label>
                    <form:select path="partyType" required="true">
                        <form:option value="">-- Select --</form:option>
                        <form:option value="Customer">Customer</form:option>
                        <form:option value="Dealer">Dealer</form:option>
                        <form:option value="Retailer">Retailer</form:option>
                        <form:option value="Distributor">Distributor</form:option>
                    </form:select>
                </div>

                <div>
                    <label>Credit</label>
                    <form:input path="credit" type="number" min="0" required="true"/>
                </div>

                <div>
                    <label>Credit Type</label>
                    <form:select path="creditType" required="true">
                        <form:option value="">-- Select --</form:option>
                        <form:option value="NA">NA</form:option>
                        <form:option value="ADVANCE">Advance</form:option>
                        <form:option value="DUE">Due</form:option>
                    </form:select>
                </div>

                <div>
                    <label>Credit Limit</label>
                    <form:input path="creditLimit" type="number" min="0" required="true"/>
                </div>

            </div>
        </div>

        <!-- BILLING -->
        <div class="section">
            <div class="section-title">Billing Address</div>
            <div class="grid-2">

                <form:input path="billingAddress.addressLine"/>
                <form:input path="billingAddress.city"/>
                <form:input path="billingAddress.state"/>
                <form:input path="billingAddress.zipCode"/>
                <form:input path="billingAddress.country"/>

            </div>
        </div>

        <!-- SHIPPING -->
        <div class="section">
            <div class="section-title">Shipping Address</div>
            <div class="grid-2">

                <form:input path="shippingAddress.addressLine"/>
                <form:input path="shippingAddress.city"/>
                <form:input path="shippingAddress.state"/>
                <form:input path="shippingAddress.zipCode"/>
                <form:input path="shippingAddress.country"/>

            </div>
        </div>

        <button type="submit" class="submit-btn">Update Customer</button>

    </form:form>
</div>

</body>
</html>