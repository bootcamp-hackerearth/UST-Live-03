<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Customer</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            padding: 40px 0;
        }

        .customer-card {
            width: 1100px;
            margin: auto;
            background: #f3efe9;
            padding: 42px;
        }

        .back-btn {
            display: inline-block;
            margin-bottom: 22px;
            text-decoration: none;
            color: #2f2f2f;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .back-btn:hover {
            opacity: 0.7;
        }

        h2 {
            margin: 0 0 34px;
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
        }

        .section {
            margin-bottom: 36px;
        }

        .section-title {
            font-size: 15px;
            font-weight: 700;
            color: #2f2f2f;
            letter-spacing: 1px;
            margin-bottom: 24px;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 26px;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 26px;
        }

        .form-group {
            margin-bottom: 10px;
        }

        label {
            font-size: 12px;
            letter-spacing: 2px;
            color: #8a8a8a;
            display: block;
            margin-bottom: 10px;
        }

        input,
        select {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 0;
            border: none;
            border-bottom: 3px solid #cfcfcf;
            background: transparent;
            font-size: 15px;
            outline: none;
            color: #2f2f2f;
            font-family: "Inter", sans-serif;
            appearance: none;
        }

        input:focus,
        select:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        option {
            background: #f3efe9;
            color: #2f2f2f;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus {

            -webkit-box-shadow: 0 0 0px 1000px #f3efe9 inset !important;
            -webkit-text-fill-color: #2f2f2f !important;
            transition: background-color 5000s ease-in-out 0s;

        }

        .submit-btn {
            width: 100%;
            padding: 16px;
            margin-top: 10px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-size: 15px;
            font-weight: 700;
            letter-spacing: 2px;
            cursor: pointer;
            transition: 0.3s;
        }

        .submit-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        @media (max-width: 1150px) {

            .customer-card {
                width: 95%;
            }

        }

    </style>

</head>

<body>

<div class="customer-card">

    <a href="${pageContext.request.contextPath}/customer/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <h2>Add Customer</h2>

    <form:form method="post"
               action="${pageContext.request.contextPath}/customer/add"
               modelAttribute="customerDto">

        <div class="section">

            <div class="section-title">
                CUSTOMER DETAILS
            </div>

            <div class="grid">

                <div class="form-group">

                    <label>PHONE NUMBER</label>

                    <form:input path="identifier"
                                maxlength="13"
                                inputmode="tel"
                                pattern="[0-9+-]+"
                                oninput="this.value=this.value.replace(/[^0-9+-]/g,'')"/>

                </div>

                <div class="form-group">

                    <label>CUSTOMER NAME</label>

                    <form:input path="customerName"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>EMAIL</label>

                    <form:input path="email"
                                type="email"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>PARTY TYPE</label>

                    <form:select path="partyType"
                                 required="true">

                        <form:option value="">
                            -- Select --
                        </form:option>

                        <form:option value="Customer">
                            Customer
                        </form:option>

                        <form:option value="Dealer">
                            Dealer
                        </form:option>

                        <form:option value="Retailer">
                            Retailer
                        </form:option>

                        <form:option value="Distributor">
                            Distributor
                        </form:option>

                    </form:select>

                </div>

                <div class="form-group">

                    <label>CREDIT</label>

                    <form:input path="credit"
                                type="number"
                                min="0"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>CREDIT TYPE</label>

                    <form:select path="creditType"
                                 required="true">

                        <form:option value="">
                            -- Select --
                        </form:option>

                        <form:option value="NA">
                            NA
                        </form:option>

                        <form:option value="ADVANCE">
                            Advance
                        </form:option>

                        <form:option value="DUE">
                            Due
                        </form:option>

                    </form:select>

                </div>

                <div class="form-group">

                    <label>CREDIT LIMIT</label>

                    <form:input path="creditLimit"
                                type="number"
                                min="0"
                                required="true"/>

                </div>

            </div>

        </div>

        <div class="section">

            <div class="section-title">
                BILLING ADDRESS
            </div>

            <div class="grid-2">

                <div class="form-group">

                    <label>ADDRESS LINE</label>

                    <form:input path="billingAddress.addressLine"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>CITY</label>

                    <form:input path="billingAddress.city"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>STATE</label>

                    <form:input path="billingAddress.state"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>ZIP CODE</label>

                    <form:input path="billingAddress.zipCode"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>COUNTRY</label>

                    <form:input path="billingAddress.country"
                                required="true"/>

                </div>

            </div>

        </div>

        <div class="section">

            <div class="section-title">
                SHIPPING ADDRESS
            </div>

            <div class="grid-2">

                <div class="form-group">

                    <label>ADDRESS LINE</label>

                    <form:input path="shippingAddress.addressLine"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>CITY</label>

                    <form:input path="shippingAddress.city"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>STATE</label>

                    <form:input path="shippingAddress.state"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>ZIP CODE</label>

                    <form:input path="shippingAddress.zipCode"
                                required="true"/>

                </div>

                <div class="form-group">

                    <label>COUNTRY</label>

                    <form:input path="shippingAddress.country"
                                required="true"/>

                </div>

            </div>

        </div>

        <button type="submit"
                class="submit-btn">

            ADD CUSTOMER

        </button>

    </form:form>

</div>

</body>
</html>