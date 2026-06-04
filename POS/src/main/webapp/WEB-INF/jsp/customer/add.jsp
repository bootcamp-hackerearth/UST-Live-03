<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>POS Management | Add Customer</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

<style>
    :root {
        --primary: #4F46E5;       /* Modern Indigo */
        --primary-dark: #3730A3;
        --secondary: #64748B;
        --bg-body: #F8FAFC;
        --bg-card: #FFFFFF;
        --text-main: #1E293B;
        --text-muted: #64748B;
        --border-color: #E2E8F0;
        --input-focus: #C7D2FE;
    }

    body {
        margin: 0;
        padding: 40px 20px;
        background-color: var(--bg-body);
        font-family: 'Inter', sans-serif;
        color: var(--text-main);
        display: flex;
        justify-content: center;
    }

    .card {
        background: var(--bg-card);
        width: 100%;
        max-width: 850px;
        border-radius: 20px;
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
        overflow: hidden;
        border: 1px solid var(--border-color);
    }

    .header-banner {
        background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
        padding: 40px 20px;
        color: white;
        text-align: center;
    }

    .header-banner h1 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 700;
        letter-spacing: -0.025em;
        text-transform: uppercase;
        opacity: 0.9;
    }

    .form-container { padding: 40px; }

    .form-title {
        font-size: 1.25rem;
        font-weight: 700;
        margin-bottom: 30px;
        color: var(--text-main);
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 24px;
        margin-bottom: 30px;
    }

    .full-width { grid-column: span 2; }

    label {
        font-size: 0.875rem;
        font-weight: 600;
        color: var(--text-main);
        display: block;
        margin-bottom: 8px;
    }

    input, select {
        width: 100%;
        padding: 12px 16px;
        border-radius: 10px;
        border: 1px solid var(--border-color);
        font-size: 0.95rem;
        transition: all 0.2s ease;
        background-color: #FFFFFF;
        box-sizing: border-box;
    }

    input:focus, select:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 4px var(--input-focus);
    }

    .inline-group {
        display: flex;
        gap: 12px;
    }

    /* Section Toggles */
    .section-toggle {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 15px 20px;
        background: #F1F5F9;
        border-radius: 12px;
        cursor: pointer;
        font-weight: 600;
        font-size: 0.9rem;
        margin-bottom: 15px;
        transition: background 0.2s;
        border: 1px solid transparent;
    }

    .section-toggle:hover { background: #E2E8F0; }

    .toggle-billing { color: var(--primary); }
    .toggle-shipping { color: #DC2626; }

    .address-box {
        background: #FFFFFF;
        border: 1px dashed var(--border-color);
        border-radius: 12px;
        padding: 25px;
        margin-bottom: 20px;
        animation: slideDown 0.3s ease-out;
    }

    @keyframes slideDown {
        from { opacity: 0; transform: translateY(-10px); }
        to { opacity: 1; transform: translateY(0); }
    }

    .btn-submit {
        width: 100%;
        padding: 16px;
        background: var(--primary);
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 1rem;
        font-weight: 700;
        cursor: pointer;
        transition: all 0.3s ease;
        margin-top: 20px;
        box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.3);
    }

    .btn-submit:hover {
        background: var(--primary-dark);
        transform: translateY(-2px);
        box-shadow: 0 10px 15px -3px rgba(79, 70, 229, 0.4);
    }

    .btn-submit:active { transform: translateY(0); }
</style>

<script>
    function toggleSection(id) {
        const el = document.getElementById(id);
        const isHidden = el.style.display === 'none';
        el.style.display = isHidden ? 'block' : 'none';

        // Update the icon/symbol if you want (Optional)
        const toggleBtn = event.currentTarget;
        const icon = toggleBtn.querySelector('.toggle-icon');
        icon.className = isHidden ? 'bi bi-dash-circle-fill toggle-icon' : 'bi bi-plus-circle-fill toggle-icon';
    }
</script>
</head>

<body>

<div class="card">
    <div class="header-banner">
        <h1><i class="bi bi-speedometer2"></i> POS Management System</h1>
    </div>

    <div class="form-container">
        <div class="form-title">
            <i class="bi bi-person-plus-fill"></i> Add New Customer
        </div>

        <form action="${pageContext.request.contextPath}/customer/add" method="post">

            <div class="grid">
                <div>
                    <label>Customer Name</label>
                    <input name="customerName" placeholder="John Doe" required>
                </div>

                <div>
                    <label>Phone Number</label>
                    <input name="identifier" placeholder="+1 (555) 000-0000" required>
                </div>

                <div>
                    <label>Party Type</label>
                    <select name="partyType">
                        <option value="" disabled selected>Select Type</option>
                        <option>Customer</option>
                        <option>Dealer</option>
                        <option>Wholesaler</option>
                    </select>
                </div>

                <div>
                    <label>Balance</label>
                    <div class="inline-group">
                        <input name="balance" placeholder="0.00" style="flex: 2;">
                        <select name="balanceType" style="flex: 1;">
                            <option>Due</option>
                            <option>Advance</option>
                        </select>
                    </div>
                </div>

                <div>
                    <label>Email Address</label>
                    <input name="username" type="email" placeholder="email@example.com">
                </div>

                <div>
                    <label>Party Credit Limit</label>
                    <input name="creditLimit" placeholder="5000">
                </div>

                <div class="full-width">
                    <label>Primary Address</label>
                    <input name="address" placeholder="123 Main St, Suite 400">
                </div>
            </div>

            <div class="section-toggle toggle-billing" onclick="toggleSection('billing')">
                <span><i class="bi bi-receipt me-2"></i> Billing Address</span>
                <i class="bi bi-plus-circle-fill toggle-icon"></i>
            </div>
            <div id="billing" class="address-box" style="display:none;">
                <div class="grid">
                    <input name="billingAddress.addressLine" placeholder="Address Line">
                    <input name="billingAddress.city" placeholder="City">
                    <input name="billingAddress.state" placeholder="State">
                    <input name="billingAddress.zipcode" placeholder="Zip Code">
                    <div class="full-width">
                        <input name="billingAddress.country" placeholder="Country">
                    </div>
                </div>
            </div>

            <div class="section-toggle toggle-shipping" onclick="toggleSection('shipping')">
                <span><i class="bi bi-truck me-2"></i> Shipping Address</span>
                <i class="bi bi-dash-circle-fill toggle-icon"></i>
            </div>
            <div id="shipping" class="address-box">
                <div class="grid">
                    <input name="shippingAddress.addressLine" placeholder="Address Line">
                    <input name="shippingAddress.city" placeholder="City">
                    <input name="shippingAddress.state" placeholder="State">
                    <input name="shippingAddress.zipcode" placeholder="Zip Code">
                    <div class="full-width">
                        <input name="shippingAddress.country" placeholder="Country">
                    </div>
                </div>
            </div>

            <button type="submit" class="btn-submit">
                <i class="bi bi-check2-all me-2"></i> Save Customer Profile
            </button>
        </form>
    </div>
</div>

</body>
</html>