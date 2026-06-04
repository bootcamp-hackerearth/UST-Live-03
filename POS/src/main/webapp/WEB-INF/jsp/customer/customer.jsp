<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

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

        .form-container {
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

        .section-title {
            margin: 25px 0 15px;
            font-size: 15px;
            font-weight: 600;
            color: #4b6cb7;
            border-bottom: 1px solid #e4e7f2;
            padding-bottom: 6px;
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

        input:focus,
        select:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.15);
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

        .actions {
            margin-top: 15px;
        }

        .btn {
            width: 100%;
            border: none;
            cursor: pointer;
            text-decoration: none;
            box-sizing: border-box;
        }

        .btn-save {
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
    </style>
</head>

<body>

<div class="form-container">

    <a href="/customer/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Edit Customer</h2>

    <form:form action="/customer/update" method="post" modelAttribute="customerDto">

        <form:hidden path="id"/>

        <div class="section-title">Customer Information</div>

        <div class="form-group">
            <label>Customer Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>

            <form:input
                    path="phoneNo"
                    type="text"
                    maxlength="10"
                    pattern="[0-9]{10}"
                    required="required"
                    oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                    class="form-control"/>

            <form:errors path="phoneNo" cssClass="text-danger"/>
        </div>

        <div class="form-group">
            <label>Email Address</label>
            <form:input
                    type="email"
                    required="required"
                    path="email"/>
        </div>

        <div class="form-group">
            <label>Primary Address (Short)</label>
            <form:input path="address"/>
        </div>

        <div class="form-group">
            <label>Customer Type</label>
            <form:select path="partyType">
                <form:option value="" label="-- Select Type --"/>
                <form:option value="Customer" label="Customer"/>
                <form:option value="Dealer" label="Dealer"/>
                <form:option value="Supplier" label="Supplier"/>
            </form:select>
        </div>

        <details>
            <summary>Billing Address</summary>

            <input type="hidden" name="billing.id" value="${customerDto.billing.id}" />

            <div class="address-grid">
                <input type="text" name="billing.addressLine"
                       value="${customerDto.billing.addressLine}"
                       placeholder="Address Line"/>

                <input type="text" name="billing.city"
                       value="${customerDto.billing.city}"
                       placeholder="City"/>

                <input type="text" name="billing.state"
                       value="${customerDto.billing.state}"
                       placeholder="State"/>

                <input type="text"
                       name="billing.pincode"
                       value="${customerDto.billing.pincode}"
                       placeholder="Pincode"
                       maxlength="6"
                       pattern="[0-9]{6}"
                       inputmode="numeric"
                       oninput="this.value = this.value.replace(/[^0-9]/g,'')"/>

                <input type="text" name="billing.country"
                       value="${customerDto.billing.country}"
                       placeholder="Country"/>
            </div>
        </details>

        <details>
            <summary>Shipping Address</summary>

            <input type="hidden" name="shipping.id" value="${customerDto.shipping.id}" />

            <div class="address-grid">
                <input type="text" name="shipping.addressLine"
                       value="${customerDto.shipping.addressLine}"
                       placeholder="Address Line"/>

                <input type="text" name="shipping.city"
                       value="${customerDto.shipping.city}"
                       placeholder="City"/>

                <input type="text" name="shipping.state"
                       value="${customerDto.shipping.state}"
                       placeholder="State"/>

                <input type="number" pattern="[0-9]{6}" maxlength="6" name="shipping.pincode"
                       value="${customerDto.shipping.pincode}"
                       placeholder="Pincode"/>

                <input type="text" name="shipping.country"
                       value="${customerDto.shipping.country}"
                       placeholder="Country"/>
            </div>
        </details>

        <div class="actions">
            <button type="submit" class="btn-save">Update Customer</button>
            <a href="/customer/list" class="btn-cancel">Cancel</a>
        </div>

    </form:form>

</div>

</body>
</html>