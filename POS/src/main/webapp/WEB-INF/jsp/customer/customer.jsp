<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Customer</title>

    <style>
        body {
            margin: 0;
            padding: 32px;
            font-family: 'Poppins', sans-serif;
            background: #f4f6fb;
        }

        h2 {
            text-align: center;
            color: #374a9e;
            margin-bottom: 28px;
        }

        .form-container {
            max-width: 760px;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 14px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.08);
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
            margin-bottom: 14px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #444;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 7px;
            border: 1px solid #ccd3ea;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75,108,183,0.15);
        }

        details {
            margin-top: 16px;
            padding: 14px;
            border-radius: 10px;
            background: #f7f9ff;
            border: 1px solid #e0e5f5;
        }

        details summary {
            font-weight: 600;
            font-size: 14px;
            color: #4b6cb7;
            cursor: pointer;
            margin-bottom: 10px;
        }

        .address-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
        }

        .actions {
            margin-top: 32px;
            display: flex;
            justify-content: center;
            gap: 18px;
        }

        .btn {
            padding: 12px 22px;
            border-radius: 10px;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            border: none;
            cursor: pointer;
        }

        .btn-save {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
        }

        .btn-cancel {
            background: #6c757d;
            color: #fff;
        }

        .btn-save:hover {
            opacity: 0.95;
        }

        .btn-cancel:hover {
            background: #5a6268;
        }
    </style>
</head>

<body>

<h2>Edit Customer</h2>

<div class="form-container">

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
        path="phoneno"
        type="text"
        maxlength="10"
        pattern="[0-9]{10}"
        oninput="this.value = this.value.replace(/[^0-9]/g, '')"
        class="form-control" />

    <form:errors path="phoneno" cssClass="text-danger"/>
</div>


        <div class="form-group">
            <label>Email Address</label>
            <form:input
            type = "email"
            path="email"/>
        </div>

        <div class="form-group">
            <label>Primary Address (Short)</label>
            <form:input path="address"/>
        </div>

        <div class="form-group">
            <label>Customer Type</label>
            <form:select path="partytype">
                <form:option value="" label="-- Select Type --"/>
                <form:option value="Customer" label="Customer"/>
                <form:option value="Dealer" label="Dealer"/>
                <form:option value="Supplier" label="Supplier"/>
            </form:select>
        </div>

        <!-- ✅ Billing Address -->
        <details>
            <summary>Billing Address</summary>
            <input type="hidden" name="billing.id" value="${customerDto.billing.id}" />
            <div class="address-grid">
                <input type="text" name="billing.addressLine" value="${customerDto.billing.addressLine}" placeholder="Address Line"/>
                <input type="text" name="billing.city"        value="${customerDto.billing.city}"        placeholder="City"/>
                <input type="text" name="billing.state"       value="${customerDto.billing.state}"       placeholder="State"/>
                <input type="text" name="billing.pincode" value="${customerDto.billing.pincode}" placeholder="Pincode" maxlength="6" pattern="[0-9]{6}" inputmode="numeric" oninput="this.value = this.value.replace(/[^0-9]/g,'')"/>
                <input type="text" name="billing.country"     value="${customerDto.billing.country}"     placeholder="Country"/>
            </div>
        </details>
        <details>
            <summary>Shipping Address</summary>
            <input type="hidden" name="shipping.id" value="${customerDto.shipping.id}" />
            <div class="address-grid">
                <input type="text" name="shipping.addressLine" value="${customerDto.shipping.addressLine}" placeholder="Address Line"/>
                <input type="text" name="shipping.city"        value="${customerDto.shipping.city}"        placeholder="City"/>
                <input type="text" name="shipping.state"       value="${customerDto.shipping.state}"       placeholder="State"/>
                <input type="number" name="shipping.pincode"   value="${customerDto.shipping.pincode}"     placeholder="Pincode"/>
                <input type="text" name="shipping.country"     value="${customerDto.shipping.country}"     placeholder="Country"/>
            </div>
        </details>

        <div class="actions">
            <button type="submit" class="btn btn-save">Update Customer</button>
            <a href="/customer/list" class="btn btn-cancel">Cancel</a>
        </div>

    </form:form>

</div>

</body>
</html>