<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;

            --border: #e2e8f0;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            padding: 40px 20px;
            color: var(--text);
        }

        /* BACK BUTTON (SAME ACROSS APP) */
        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);

            border: 1px solid var(--border);
            color: var(--text);

            text-decoration: none;
            font-size: 18px;

            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
        }

        /* MAIN CARD */
        .container-box {
            max-width: 1200px;
            margin: 60px auto 0;
        }

        .card {
            background: var(--glass);
            backdrop-filter: blur(16px);

            border-radius: var(--radius);
            border: 1px solid var(--border);

            box-shadow: var(--shadow);
            padding: 32px;
        }

        h2 {
            text-align: center;
            margin-bottom: 32px;
            font-size: 22px;
            font-weight: 600;
        }

        /* SECTIONS */
        .section {
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 22px;
            margin-bottom: 26px;
            background: rgba(248,250,252,0.6);
        }

        .section-title {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 14px;
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
            color: var(--muted);
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid var(--border);
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 2px rgba(37,99,235,0.15);
        }

        .submit-btn {
            margin-top: 26px;
            width: 100%;
            padding: 14px;
            background: var(--primary);
            color: white;
            border: none;
            border-radius: 999px;
            font-weight: 600;
            font-size: 15px;
            cursor: pointer;
            transition: 0.2s;
        }

        .submit-btn:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }
    </style>
</head>

<body>

<!-- BACK BUTTON -->
<a href="${pageContext.request.contextPath}/customer/list" class="back-arrow">←</a>

<div class="container-box">
    <div class="card">

        <h2>Add Customer</h2>

        <!-- FORM LOGIC UNCHANGED -->
        <form:form method="post"
                   action="${pageContext.request.contextPath}/customer/add"
                   modelAttribute="customerDto">

            <!-- CUSTOMER DETAILS -->
            <div class="section">
                <div class="section-title">Customer Details</div>
                <div class="grid">

                    <div>
                        <label>Phone Number</label>
                        <form:input path="identifier"/>
                    </div>

                    <div>
                        <label>Customer Name</label>
                        <form:input path="customerName"/>
                    </div>

                    <div>
                        <label>Email</label>
                        <form:input path="email"/>
                    </div>

                    <div>
                        <label>Party Type</label>
                        <form:select path="partyType">
                            <form:option value="">-- Select --</form:option>
                            <form:option value="Customer">Customer</form:option>
                            <form:option value="Dealer">Dealer</form:option>
                            <form:option value="Retailer">Retailer</form:option>
                            <form:option value="Distributor">Distributor</form:option>
                        </form:select>
                    </div>

                    <div>
                        <label>Credit</label>
                        <form:input path="credit"/>
                    </div>

                    <div>
                        <label>Credit Type</label>
                        <form:select path="creditType">
                            <form:option value="">-- Select --</form:option>
                            <form:option value="ADVANCE">Advance</form:option>
                            <form:option value="DUE">Due</form:option>
                        </form:select>
                    </div>

                    <div>
                        <label>Credit Limit</label>
                        <form:input path="creditLimit"/>
                    </div>

                </div>
            </div>

            <!-- BILLING ADDRESS -->
            <div class="section">
                <div class="section-title">Billing Address</div>
                <div class="grid-2">
                    <form:input path="billingAddress.addressLine" placeholder="Address Line"/>
                    <form:input path="billingAddress.phoneNo" placeholder="Phone No"/>
                    <form:input path="billingAddress.city" placeholder="City"/>
                    <form:input path="billingAddress.state" placeholder="State"/>
                    <form:input path="billingAddress.zipCode" placeholder="Zip Code"/>
                    <form:input path="billingAddress.country" placeholder="Country"/>
                </div>
            </div>

            <!-- SHIPPING ADDRESS -->
            <div class="section">
                <div class="section-title">Shipping Address</div>
                <div class="grid-2">
                    <form:input path="shippingAddress.addressLine" placeholder="Address Line"/>
                    <form:input path="shippingAddress.phoneNo" placeholder="Phone No"/>
                    <form:input path="shippingAddress.city" placeholder="City"/>
                    <form:input path="shippingAddress.state" placeholder="State"/>
                    <form:input path="shippingAddress.zipCode" placeholder="Zip Code"/>
                    <form:input path="shippingAddress.country" placeholder="Country"/>
                </div>
            </div>

            <button type="submit" class="submit-btn">Add Customer</button>

        </form:form>

    </div>
</div>

</body>
</html>