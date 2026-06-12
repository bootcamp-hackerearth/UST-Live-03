<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>POS Management | Add Customer</title>

<style>
    body {
        margin: 0;
        min-height: 100vh;
        background: #F4F5F7;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 30px 0;
    }

    .card {
        background: #FFFFFF;
        width: 100%;
        max-width: 1100px;
        padding: 40px;
        border-radius: 16px;
        box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    }

    .header {
        background: #0B3C5D;
        margin: -40px -40px 30px -40px;
        padding: 26px;
        color: white;
        text-align: center;
        border-radius: 16px 16px 0 0;
    }

    h2 {
        margin: 10px 0 25px;
        color: #1F2937;
    }

    .section {
        margin-bottom: 28px;
    }

    .section-title {
        font-size: 14px;
        font-weight: 700;
        color: #374151;
        margin-bottom: 12px;
        border-bottom: 1px solid #E5E7EB;
        padding-bottom: 6px;
    }

    .grid-3 {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;
    }

    .grid-2 {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
    }

    label {
        font-size: 12px;
        font-weight: 600;
        color: #4B5563;
        display: block;
        margin-bottom: 5px;
    }

    input, select {
        width: 100%;
        padding: 11px;
        border-radius: 8px;
        border: 1px solid #E5E7EB;
        font-size: 13px;
    }

    .inline {
        display: flex;
        gap: 8px;
    }

    .btn {
        margin-top: 30px;
        width: 100%;
        padding: 13px;
        background: #0B3C5D;
        color: white;
        border: none;
        border-radius: 8px;
        font-size: 15px;
        font-weight: 700;
        cursor: pointer;
    }

    /* Field wrapper to avoid cramped look */
    .field {
        margin-bottom: 14px;
    }

    /* Address card look */
    .address-card {
        background: #F9FAFB;
        border: 1px solid #E5E7EB;
        border-radius: 14px;
        padding: 20px;
    }

    /* Stronger visual title */
    .address-title {
        font-size: 13px;
        font-weight: 700;
        margin-bottom: 14px;
        color: #1F2937;
    }

    /* Improve overall readability */
    input, select {
        box-sizing: border-box;
    }


</style>
</head>

<body>
    <div class="card">
    <div class="header">
        <h1>POS Management</h1>
    </div>

    <h2>Add Customer</h2>

    <form action="${pageContext.request.contextPath}/customer/add" method="post">

        <!-- CUSTOMER DETAILS -->
        <div class="section">
            <div class="section-title">Customer Details</div>
            <div class="grid-3">

                <div>
                    <label>Customer Name</label>
                    <input name="customerName" required>
                </div>

                <div>
                    <label>Phone Number (Identifier)</label>
                    <input name="identifier" required>
                </div>

                <div>
                    <label>Email</label>
                    <input name="username" type="email">
                </div>

                <div>
                    <label>Party Type</label>
                    <select name="partyType">
                        <option>Customer</option>
                        <option>Dealer</option>
                        <option>Wholesaler</option>
                    </select>
                </div>

                <div>
                    <label>Balance</label>
                    <div class="inline">
                        <input name="balance" placeholder="Ex: 500">
                        <select name="balanceType">
                            <option>Due</option>
                            <option>Advance</option>
                        </select>
                    </div>
                </div>

                <div>
                    <label>Credit Limit</label>
                    <input name="creditLimit" placeholder="Ex: 800">
                </div>

            </div>
        </div>

        <!-- ADDRESS -->
        <div class="section">
            <div class="section-title">Addresses</div>

            <div class="grid-2">

                <!-- BILLING -->
                <div class="address-card">
                    <div class="address-title">Billing Address</div>

                    <div class="field">
                        <input name="billingAddress.addressLine" placeholder="Address Line">
                    </div>
                    <div class="field">
                        <input name="billingAddress.city" placeholder="City">
                    </div>
                    <div class="field">
                        <input name="billingAddress.state" placeholder="State">
                    </div>
                    <div class="field">
                        <input name="billingAddress.zipcode" placeholder="Zip Code">
                    </div>
                    <div class="field">
                        <input name="billingAddress.country" placeholder="Country">
                    </div>
                </div>

                <!-- SHIPPING -->
                <div class="address-card">
                    <div class="address-title">Shipping Address</div>

                    <div class="field">
                        <input name="shippingAddress.addressLine" placeholder="Address Line">
                    </div>
                    <div class="field">
                        <input name="shippingAddress.city" placeholder="City">
                    </div>
                    <div class="field">
                        <input name="shippingAddress.state" placeholder="State">
                    </div>
                    <div class="field">
                        <input name="shippingAddress.zipcode" placeholder="Zip Code">
                    </div>
                    <div class="field">
                        <input name="shippingAddress.country" placeholder="Country">
                    </div>
                </div>

            </div>
        </div>
        <button type="submit" class="btn">Save Customer</button>
        <div style="margin-top:20px;">
            <a href="${pageContext.request.contextPath}/customer/list"
               style="font-size:13px; color:#6B7280; text-decoration:none; font-weight:600;">
                ← Back to Customers
            </a>
        </div>
    </form>
</div>
</body>
</html>