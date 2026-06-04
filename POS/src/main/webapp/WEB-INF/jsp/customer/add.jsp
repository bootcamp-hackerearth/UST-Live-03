<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>POS Management | Add Customer</title>
    <style>
        body {
            margin: 0;
            padding: 20px 0;
            background: #F4F5F7;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
        }
        .card {
            background: #FFFFFF;
            width: 100%;
            max-width: 900px;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }
        .header {
            background: #0B3C5D;
            margin: -40px -40px 30px -40px;
            padding: 25px;
            color: white;
            text-align: center;
            border-radius: 16px 16px 0 0;
        }
        h2 { margin-bottom: 20px; }
        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 18px;
            margin-bottom: 25px;
        }
        label {
            font-size: 13px;
            font-weight: 600;
            color: #374151;
            display: block;
            margin-bottom: 6px;
        }
        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            font-size: 14px;
        }
        .inline { display: flex; gap: 10px; }
        .toggle { cursor: pointer; color: #0B3C5D; font-weight: 600; margin-bottom: 10px; }
        .address-box {
            background: #F9FAFB;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 25px;
            display: none;
        }
        .btn {
            width: 100%;
            padding: 13px;
            background: #0B3C5D;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
        }
    </style>
    <script>
        function toggleSection(id) {
            const el = document.getElementById(id);
            el.style.display = el.style.display === 'none' ? 'block' : 'none';
        }
    </script>
</head>
<body>
<div class="card">
    <div class="header">
        <h1>POS Management</h1>
    </div>

    <h2>Add Customer</h2>

    <form action="${pageContext.request.contextPath}/customer/add" method="post">

        <!-- CUSTOMER DETAILS -->
        <div class="grid">
            <div>
                <label>Customer Name</label>
                <input name="customerName" required />
            </div>

            <div>
                <label>Phone Number</label>
                <input name="phoneNum" required />
            </div>

            <div>
                <label>Party Type</label>
                <select name="partyType">
                    <option value="">Select one</option>
                    <option value="Customer">Customer</option>
                    <option value="Dealer">Dealer</option>
                    <option value="Wholesaler">Wholesaler</option>
                </select>
            </div>

            <div>
                <label>Balance</label>
                <div class="inline">
                    <input name="balance" />
                    <select name="balanceType">
                        <option value="Due">Due</option>
                        <option value="Advance">Advance</option>
                    </select>
                </div>
            </div>

            <div>
                <label>Email</label>
                <input type="email" name="username" />
            </div>

            <div>
                <label>Credit Limit</label>
                <input name="creditLimit" />
            </div>

            <div style="grid-column: span 2;">
                <label>Address</label>
                <input name="address" />
            </div>
        </div>

        <!-- BILLING ADDRESS -->
        <div class="toggle" onclick="toggleSection('billing')">+ Billing Address</div>
        <div id="billing" class="address-box">
            <div class="grid">
                <input name="billingAddress.addressType" placeholder="Address Type" />
                <input name="billingAddress.addressLine" placeholder="Address Line" />
                <input name="billingAddress.city" placeholder="City" />
                <input name="billingAddress.state" placeholder="State" />
                <input name="billingAddress.country" placeholder="Country" />
                <input name="billingAddress.zipcode" placeholder="Zip Code" />

            </div>
        </div>

        <!-- SHIPPING ADDRESS -->
        <div class="toggle" style="color:#DC2626" onclick="toggleSection('shipping')">− Shipping Address</div>
        <div id="shipping" class="address-box" style="display:block;">
            <div class="grid">
                <input name="shippingAddress.addressType" placeholder="Address Type" />
                <input name="shippingAddress.addressLine" placeholder="Address Line" />
                <input name="shippingAddress.city" placeholder="City" />
                <input name="shippingAddress.state" placeholder="State" />
                <input name="shippingAddress.country" placeholder="Country" />
                <input name="shippingAddress.zipcode" placeholder="Zip Code" />
            </div>
        </div>

        <button class="btn">Save Customer</button>
    </form>
</div>
</body>
</html>