<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>POS Retail Management | Customer Details</title>

<style>
    body {
        margin: 0;
        min-height: 100vh;
        background: #F4F5F7;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        display: flex;
        justify-content: center;
        padding: 30px 0;
    }

    .card {
        background: #FFFFFF;
        width: 100%;
        max-width: 1100px;
        padding: 40px;
        border-radius: 16px;
        box-shadow: 0 10px 26px rgba(0,0,0,0.08);
    }

    .header {
        background: #0B3C5D;
        margin: -40px -40px 30px -40px;
        padding: 26px;
        color: white;
        text-align: center;
        border-radius: 16px 16px 0 0;
        font-weight: 700;
    }

    .section {
        margin-bottom: 28px;
    }

    .section-title {
        font-size: 14px;
        font-weight: 700;
        margin-bottom: 14px;
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
        gap: 22px;
    }

    label {
        font-size: 12px;
        font-weight: 600;
        color: #4B5563;
        margin-bottom: 6px;
        display: block;
    }

    input, select {
        width: 100%;
        padding: 11px;
        border-radius: 8px;
        border: 1px solid #E5E7EB;
        font-size: 13px;
        box-sizing: border-box;
    }

    input:disabled, select:disabled {
        background: #F9FAFB;
        color: #111827;
        opacity: 1;
        cursor: default;
    }

    .address-card {
        background: #F9FAFB;
        border: 1px solid #E5E7EB;
        border-radius: 14px;
        padding: 20px;
    }

    .address-title {
        font-size: 13px;
        font-weight: 700;
        margin-bottom: 10px;
    }

    .actions {
        text-align: center;
        margin-top: 30px;
    }

    .btn {
        padding: 12px 30px;
        border-radius: 8px;
        font-weight: 700;
        border: none;
        cursor: pointer;
    }

    .edit-btn {
        background: #0B3C5D;
        color: white;
    }

    .save-btn {
        background: #16A34A;
        color: white;
        display: none;
    }

    .back-link {
        display: block;
        margin-top: 20px;
        text-align: center;
        font-size: 13px;
        font-weight: 600;
        color: #6B7280;
        text-decoration: none;
    }
</style>
</head>

<body>
    <div class="card">
        <div class="header">POS Retail Management</div>

        <form action="${pageContext.request.contextPath}/customer/update" method="post">

            <input type="hidden" name="id" value="${customer.id}">
            <input type="hidden" name="identifier" value="${customer.identifier}">
            <input type="hidden" name="username" value="${customer.username}">

            <h2>Customer Profile</h2>

            <div class="section">
            <div class="section-title">Customer Details</div>
                <div class="grid-3">
                    <div>
                        <label>Customer Name</label>
                        <input name="customerName" value="${customer.customerName}" disabled>
                    </div>

                    <div>
                        <label>Phone Number</label>
                        <input value="${customer.identifier}" readonly>
                    </div>

                    <div>
                        <label>Party Type</label>
                            <select name="partyType" disabled>
                                <option ${customer.partyType eq 'Customer' ? 'selected' : ''}>Customer</option>
                                <option ${customer.partyType eq 'Dealer' ? 'selected' : ''}>Dealer</option>
                                <option ${customer.partyType eq 'Retailer' ? 'selected' : ''}>Retailer</option>
                            </select>
                    </div>

                    <div>
                        <label>Balance</label>
                        <input name="balance" value="${customer.balance}" disabled>
                    </div>

                    <div>
                        <label>Balance Type</label>
                        <select name="balanceType" disabled>
                            <option ${customer.balanceType eq 'Advance' ? 'selected' : ''}>Advance</option>
                            <option ${customer.balanceType eq 'Due' ? 'selected' : ''}>Due</option>
                        </select>
                    </div>

                    <div>
                        <label>Credit Limit</label>
                        <input name="creditLimit" value="${customer.creditLimit}" disabled>
                    </div>
                </div>
            </div>

            <div class="section">
                <div class="section-title">Addresses</div>
                    <div class="grid-2">
                        <div class="address-card">
                            <div class="address-title">Billing Address</div>
                                <input name="billingAddress.addressLine" value="${customer.billingAddress.addressLine}" disabled>
                                <input name="billingAddress.city" value="${customer.billingAddress.city}" disabled>
                                <input name="billingAddress.state" value="${customer.billingAddress.state}" disabled>
                                <input name="billingAddress.zipcode" value="${customer.billingAddress.zipcode}" disabled>
                                <input name="billingAddress.country" value="${customer.billingAddress.country}" disabled>
                            </div>

                        <div class="address-card">
                        <div class="address-title">Shipping Address</div>
                        <input name="shippingAddress.addressLine" value="${customer.shippingAddress.addressLine}" disabled>
                        <input name="shippingAddress.city" value="${customer.shippingAddress.city}" disabled>
                        <input name="shippingAddress.state" value="${customer.shippingAddress.state}" disabled>
                        <input name="shippingAddress.zipcode" value="${customer.shippingAddress.zipcode}" disabled>
                        <input name="shippingAddress.country" value="${customer.shippingAddress.country}" disabled>
                    </div>
                </div>
            </div>

            <div class="actions">
            <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Customer</button>
            <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/customer/list" class="back-link">
            ← Back to Customer List
            </a>
        </form>
    </div>

    <script>
    function enableEdit() {
        document.querySelectorAll("input:disabled, select:disabled")
            .forEach(el => el.disabled = false);

        document.querySelector("input[readonly]").readOnly = true;

        document.querySelector(".edit-btn").style.display = "none";
        document.querySelector(".save-btn").style.display = "inline-block";
    }
    </script>
</body>
</html>
