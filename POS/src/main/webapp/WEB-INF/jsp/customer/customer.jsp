<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Management | Edit Customer</title>

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
        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 18px;
            margin-bottom: 25px;
        }
        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
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
        .address-box {
            background: #F9FAFB;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 25px;
        }
    </style>
</head>

<body>
<div class="card">

    <div class="header">
        <h1>Edit Customer</h1>
    </div>

    <form action="${pageContext.request.contextPath}/customer/update"
          method="post">

        <!--  HIDDEN FIELDS -->
        <input type="hidden" name="id" value="${customerDto.id}"/>
        <input type="hidden" name="identifier" value="${customerDto.identifier}"/>

        <!--  CUSTOMER DETAILS -->
        <div class="grid">

            <div>
                <label>Customer Name</label>
                <input name="customerName"
                       value="${customerDto.customerName}" />
            </div>

            <div>
                <label>Phone Number</label>
                <input name="phoneNum"
                       value="${customerDto.phoneNum}"
                       readonly />
            </div>

            <div>
                <label>Party Type</label>
                <select name="partyType">
                    <option ${customerDto.partyType == 'Customer' ? 'selected' : ''}>Customer</option>
                    <option ${customerDto.partyType == 'Dealer' ? 'selected' : ''}>Dealer</option>
                    <option ${customerDto.partyType == 'Wholesaler' ? 'selected' : ''}>Wholesaler</option>
                </select>
            </div>

            <div>
                <label>Balance</label>
                <input name="balance"
                       value="${customerDto.balance}" />
            </div>

            <div>
                <label>Email</label>
                <input name="username"
                       value="${customerDto.username}" />
            </div>

            <div>
                <label>Credit Limit</label>
                <input name="creditLimit"
                       value="${customerDto.creditLimit}" />
            </div>

        </div>

        <!--  BILLING ADDRESS -->
        <h3>Billing Address</h3>
        <div class="address-box">

            <input type="hidden"
                   name="billingAddress.identifier"
                   value="${customerDto.billingAddress.identifier}" />

            <input type="hidden"
                   name="billingAddress.addressType"
                   value="Billing Address" />

            <div class="grid">
                <input name="billingAddress.addressLine"
                       value="${customerDto.billingAddress.addressLine}" />

                <input name="billingAddress.city"
                       value="${customerDto.billingAddress.city}" />

                <input name="billingAddress.state"
                       value="${customerDto.billingAddress.state}" />

                <input name="billingAddress.country"
                       value="${customerDto.billingAddress.country}" />

                <input name="billingAddress.zipcode"
                       value="${customerDto.billingAddress.zipcode}" />
            </div>
        </div>

        <!--  SHIPPING ADDRESS -->
        <h3>Shipping Address</h3>
        <div class="address-box">

            <input type="hidden"
                   name="shippingAddress.identifier"
                   value="${customerDto.shippingAddress.identifier}" />

            <input type="hidden"
                   name="shippingAddress.addressType"
                   value="Shipping Address" />

            <div class="grid">
                <input name="shippingAddress.addressLine"
                       value="${customerDto.shippingAddress.addressLine}" />

                <input name="shippingAddress.city"
                       value="${customerDto.shippingAddress.city}" />

                <input name="shippingAddress.state"
                       value="${customerDto.shippingAddress.state}" />

                <input name="shippingAddress.country"
                       value="${customerDto.shippingAddress.country}" />

                <input name="shippingAddress.zipcode"
                       value="${customerDto.shippingAddress.zipcode}" />
            </div>
        </div>

        <button class="btn">Update Customer</button>

    </form>

</div>
</body>
</html>