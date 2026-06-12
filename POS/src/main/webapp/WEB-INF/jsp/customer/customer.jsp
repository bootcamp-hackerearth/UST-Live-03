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
            background-color: #f6f7f9;
        }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .top-title {
            color: #e5e7eb;
            font-weight: 600;
        }

        .home-btn {
            padding: 7px 16px;
            background-color: #1e293b;
            color: #e5e7eb;
            text-decoration: none;
            border-radius: 6px;
            font-weight: 600;
            border: 1px solid #334155;
        }

        .logout-btn {
            background: #dc2626;
            color: white;
            border: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
        }

        .card {
            max-width: 1200px;
            background: #ffffff;
            margin: 40px auto;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 20px;
            left: 20px;
            padding: 6px 14px;
            background: #eef0f3;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #374151;
            text-decoration: none;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #020617;
        }

        .section {
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            padding: 22px;
            margin-bottom: 26px;
        }

        .section-title {
            font-size: 18px;
            font-weight: 700;
            margin-bottom: 14px;
            color: #020617;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 18px;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 18px;
        }

        label {
            display: block;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 9px 11px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f1f5f9;
        }

        .submit-btn {
            margin-top: 10px;
            width: 100%;
            padding: 12px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 20px;
            font-weight: 700;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <div class="top-title">POS Application</div>
        <a class="home-btn" href="${pageContext.request.contextPath}/">Home</a>
    </div>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <a href="${pageContext.request.contextPath}/customer/list" class="back-btn">Back</a>

    <h2>Edit Customer</h2>
<c:if test="${not empty message}">
    <div style="
        background:#fee2e2;
        color:#991b1b;
        padding:12px 18px;
        margin-bottom:20px;
        border-radius:8px;
        font-weight:600;
        text-align:center;
    ">
        ${message}
    </div>
</c:if>
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
                                pattern="[0-9+-]+"
                                oninput="this.value=this.value.replace(/[^0-9+-]/g,'')"/>
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

        <!-- BILLING ADDRESS -->
        <div class="section">
            <div class="section-title">Billing Address</div>
            <div class="grid-2">

                <div>
                    <label>Address Line</label>
                    <form:input path="billingAddress.addressLine" required="true"/>
                </div>

                <div>
                    <label>City</label>
                    <form:input path="billingAddress.city" required="true"/>
                </div>

                <div>
                    <label>State</label>
                    <form:input path="billingAddress.state" required="true"/>
                </div>

                <div>
                    <label>Zip Code</label>
                    <form:input path="billingAddress.zipCode" required="true"/>
                </div>

                <div>
                    <label>Country</label>
                    <form:input path="billingAddress.country" required="true"/>
                </div>

            </div>
        </div>

        <!-- SHIPPING ADDRESS -->
        <div class="section">
            <div class="section-title">Shipping Address</div>
            <div class="grid-2">

                <div>
                    <label>Address Line</label>
                    <form:input path="shippingAddress.addressLine" required="true"/>
                </div>

                <div>
                    <label>City</label>
                    <form:input path="shippingAddress.city" required="true"/>
                </div>

                <div>
                    <label>State</label>
                    <form:input path="shippingAddress.state" required="true"/>
                </div>

                <div>
                    <label>Zip Code</label>
                    <form:input path="shippingAddress.zipCode" required="true"/>
                </div>

                <div>
                    <label>Country</label>
                    <form:input path="shippingAddress.country" required="true"/>
                </div>

            </div>
        </div>

        <button type="submit" class="submit-btn">Update Customer</button>

    </form:form>

</div>

</body>
</html>