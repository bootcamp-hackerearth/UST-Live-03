<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Form</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
        }
        .container {
            max-width: 750px;
            margin: auto;
        }
        .card {
            border-radius: 14px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
            padding: 20px;
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            border-radius: 14px 14px 0 0;
            text-align: center;
            margin: -20px -20px 20px -20px;
            padding: 15px;
        }
        label {
            font-weight: 600;
            margin-bottom: 5px;
        }
        input, select {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 8px;
            border: 1px solid #d1d5db;
        }
        input:focus, select:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 2px rgba(59,130,246,0.2);
        }
        .error {
            color: #dc2626;
            font-size: 13px;
        }
        .section-title {
            font-weight: 600;
            margin: 20px 0 10px;
        }
        .toggle-header {
            cursor: pointer;
            font-weight: 600;
            margin-top: 15px;
        }
        .toggle-content {
            display: none;
            margin-top: 10px;
        }
        .btn-submit {
            width: 100%;
            padding: 12px;
            background: #3b82f6;
            border: none;
            color: #fff;
            border-radius: 8px;
            font-size: 16px;
        }
    </style>
    <script>
        function toggleSection(id) {
            const section = document.getElementById(id);
            section.style.display =
                (section.style.display === "none" || section.style.display === "")
                ? "block" : "none";
        }
    </script>
</head>
<body>
<div class="container mt-5">
    <div class="card">
        <div class="card-header">
            <h4>Customer Form</h4>
        </div>

        <form:form action="/customer/add" method="post" modelAttribute="customerDto">
            <div class="section-title">Basic Details</div>
            <div class="row">
                <div class="col-md-6">
                    <label>Name</label>
                    <form:input path="name"
                    required="true"/>
                </div>
                <div class="col-md-6">
                    <label>Phone</label>
                    <form:input path="phoneNo" type="tel"
                     required="true"/>
                </div>
                <div class="col-md-6">
                    <label>Email</label>
                    <form:input path="identifier" type="email"
                     required="true"/>
                </div>
                <div class="col-md-6">
                    <label>Party Type</label>
                    <form:select path="userType">
                        <form:option value="">Select</form:option>
                        <form:option value="customer">Customer</form:option>
                        <form:option value="dealer">Dealer</form:option>
                        <form:option value="wholesaler">Wholesaler</form:option>
                    </form:select>
                </div>
            </div>
            <div class="section-title">Account Summary</div>
            <div class="row">
                <div class="col-md-6">
                    <label>Balance</label>
                    <form:input path="balance" type="number"
                     required="true"/>
                </div>
                <div class="col-md-6">
                    <label>Credit Limit</label>
                    <form:input path="creditLimit" type="number"
                     required="true"/>
                </div>
            </div>
            <div class="toggle-header" onclick="toggleSection('ship')">
                ▶ Shipping Address
            </div>
            <div id="ship" class="toggle-content">
                <div class="row">
                    <div class="col-md-12">
                        <label>Address</label>
                        <form:input path="shippingAddress.addressLine"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="shippingAddress.city"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="shippingAddress.state"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>Zip</label>
                        <form:input path="shippingAddress.zipcode"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="shippingAddress.country"
                         required="true"/>
                    </div>
            <div class="col-md-6">
                <label>Address Type</label>
                <form:select path="shippingAddress.addressType" class="form-select">
                    <form:option value="shippingAddress">Shipping Address</form:option>
                </form:select>
            </div>
                </div>
            </div>
            <div class="toggle-header" onclick="toggleSection('bill')">
                ▶ Billing Address
            </div>
            <div id="bill" class="toggle-content">
                <div class="row">
                    <div class="col-md-12">
                        <label>Address</label>
                        <form:input path="billingAddress.addressLine"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>City</label>
                        <form:input path="billingAddress.city"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>State</label>
                        <form:input path="billingAddress.state"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>Zip</label>
                        <form:input path="billingAddress.zipcode"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>Country</label>
                        <form:input path="billingAddress.country"
                         required="true"/>
                    </div>
                    <div class="col-md-6">
                        <label>Address Type</label>
                        <form:select path="billingAddress.addressType" class="form-select">
                        <form:option value="billingAddress">Billing Address</form:option>
                        </form:select>
                    </div>
                </div>
            </div>
            <br/>
            <input type="submit" class="btn-submit"/>
        </form:form>
        <div class="back">
                <a href="${pageContext.request.contextPath}/node/list">← Back to List</a>
            </div>
    </div>
</div>
</body>
</html>