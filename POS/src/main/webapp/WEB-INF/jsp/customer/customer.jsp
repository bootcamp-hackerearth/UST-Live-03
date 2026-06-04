<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Edit Customer Details</title>

    <!-- Bootstrap 5 & Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        /* ========= Modern Variables ========= */
        :root {
            --primary-blue: #0d6efd;
            --bg-light: #f4f7f6;
            --text-dark: #2c3e50;
            --border-color: #e2e8f0;
            --shadow-soft: 0 4px 14px rgba(0,0,0,0.06);
            --shadow-deep: 0 12px 30px rgba(0,0,0,0.08);
        }

        body {
            background-color: var(--bg-light);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text-dark);
            padding: 40px 0;
        }

        .container {
            max-width: 1000px;
        }

        /* ========= Back Button Styling ========= */
        .back-btn {
            background: #ffffff;
            color: #64748b !important;
            border: 1px solid var(--border-color);
            padding: 10px 20px;
            border-radius: 10px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            box-shadow: var(--shadow-soft);
            margin-bottom: 25px;
        }

        .back-btn:hover {
            background: #f8fafc;
            color: var(--primary-blue) !important;
            transform: translateY(-2px);
        }

        /* ========= Main Card Styling ========= */
        .card-edit {
            background: #ffffff;
            border: none;
            border-radius: 16px;
            box-shadow: var(--shadow-deep);
            overflow: hidden;
        }

        .card-header-custom {
            background-color: var(--primary-blue);
            color: #ffffff;
            padding: 24px;
            text-align: center;
        }

        .card-header-custom h2 {
            margin: 0;
            font-size: 1.5rem;
            font-weight: 700;
            letter-spacing: 0.5px;
        }

        .card-body {
            padding: 40px;
        }

        /* ========= Section Headers ========= */
        .section-title {
            font-size: 0.9rem;
            font-weight: 800;
            color: var(--primary-blue);
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-title::after {
            content: "";
            flex-grow: 1;
            height: 1px;
            background: var(--border-color);
        }

        /* ========= Form Elements ========= */
        .form-label {
            font-size: 0.85rem;
            color: #64748b;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .input-group-text {
            background-color: #f8fafc;
            border-color: var(--border-color);
            color: #94a3b8;
        }

        .form-control, .form-select {
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 10px 14px;
            font-weight: 500;
        }

        .form-control:focus, .form-select:focus {
            border-color: var(--primary-blue);
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.1);
        }

        .readonly-field {
            background-color: #f1f5f9 !important;
            font-weight: 600;
            color: #475569;
        }

        .address-box {
            background-color: #fbfcfd;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 30px;
        }

        .btn-submit {
            background-color: var(--primary-blue);
            border: none;
            color: white;
            padding: 16px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            width: 100%;
            margin-top: 20px;
        }

        .btn-submit:hover {
            background-color: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 8px 15px rgba(13, 110, 253, 0.2);
        }
    </style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/customer/list" class="back-btn">
        <i class="bi bi-arrow-left"></i> Back to Customer List
    </a>

    <div class="card card-edit">
        <div class="card-header-custom">
            <h2><i class="bi bi-person-gear me-2"></i>Edit Customer Profile</h2>
        </div>

        <div class="card-body">

            <form method="post" action="${pageContext.request.contextPath}/customer/update">

                <input type="hidden" name="id" value="${customer.id}">

                <div class="section-title">Basic Information</div>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <label class="form-label">Full Customer Name</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person"></i></span>
                            <input name="customerName" class="form-control" value="${customer.customerName}" required>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Phone / Identifier (Read-Only)</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-telephone"></i></span>
                            <input name="identifier" class="form-control readonly-field" value="${customer.identifier}" readonly>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Party Type</label>
                        <select name="partyType" class="form-select">
                            <option value="Customer" ${customer.partyType == 'Customer' ? 'selected' : ''}>Customer</option>
                            <option value="Dealer" ${customer.partyType == 'Dealer' ? 'selected' : ''}>Dealer</option>
                            <option value="Wholesaler" ${customer.partyType == 'Wholesaler' ? 'selected' : ''}>Wholesaler</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Email Address</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input name="username" class="form-control" value="${customer.username}">
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Current Balance ($)</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-wallet2"></i></span>
                            <input name="balance" class="form-control" value="${customer.balance}">
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Credit Limit ($)</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-graph-up-arrow"></i></span>
                            <input name="creditLimit" class="form-control" value="${customer.creditLimit}">
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-6">
                        <div class="section-title">Billing Address</div>
                        <div class="address-box">
                            <div class="mb-3">
                                <input name="billingAddress.addressLine" class="form-control" placeholder="Street Address"
                                       value="${customer.billingAddress != null ? customer.billingAddress.addressLine : ''}">
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-6"><input name="billingAddress.city" class="form-control" placeholder="City" value="${customer.billingAddress != null ? customer.billingAddress.city : ''}"></div>
                                <div class="col-6"><input name="billingAddress.state" class="form-control" placeholder="State" value="${customer.billingAddress != null ? customer.billingAddress.state : ''}"></div>
                            </div>
                            <div class="row g-2">
                                <div class="col-5"><input name="billingAddress.zipcode" class="form-control" placeholder="Zip" value="${customer.billingAddress != null ? customer.billingAddress.zipcode : ''}"></div>
                                <div class="col-7"><input name="billingAddress.country" class="form-control" placeholder="Country" value="${customer.billingAddress != null ? customer.billingAddress.country : ''}"></div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-6">
                        <div class="section-title">Shipping Address</div>
                        <div class="address-box">
                            <div class="mb-3">
                                <input name="shippingAddress.addressLine" class="form-control" placeholder="Street Address"
                                       value="${customer.shippingAddress != null ? customer.shippingAddress.addressLine : ''}">
                            </div>
                            <div class="row g-2 mb-3">
                                <div class="col-6"><input name="shippingAddress.city" class="form-control" placeholder="City" value="${customer.shippingAddress != null ? customer.shippingAddress.city : ''}"></div>
                                <div class="col-6"><input name="shippingAddress.state" class="form-control" placeholder="State" value="${customer.shippingAddress != null ? customer.shippingAddress.state : ''}"></div>
                            </div>
                            <div class="row g-2">
                                <div class="col-5"><input name="shippingAddress.zipcode" class="form-control" placeholder="Zip" value="${customer.shippingAddress != null ? customer.shippingAddress.zipcode : ''}"></div>
                                <div class="col-7"><input name="shippingAddress.country" class="form-control" placeholder="Country" value="${customer.shippingAddress != null ? customer.shippingAddress.country : ''}"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-submit">
                    <i class="bi bi-check-circle"></i> Update Customer Records
                </button>

            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>