<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Customer</title>

<style>

* {
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    margin: 0;
    padding: 30px 0;
}

.container {
    background: #ffffff;
    padding: 30px;
    width: 900px;
    max-height: 95vh;
    overflow-y: auto;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

.container::-webkit-scrollbar {
    width: 8px;
}

.container::-webkit-scrollbar-thumb {
    background: #c7d2fe;
    border-radius: 10px;
}

h2 {
    text-align: center;
    margin-bottom: 22px;
    font-weight: 600;
    color: #0f172a;
}

.section-title {
    margin-top: 20px;
    margin-bottom: 15px;
    color: #4f46e5;
    font-size: 18px;
    font-weight: 600;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 18px;
}

.full-width {
    grid-column: span 2;
}

label {
    font-size: 13px;
    margin-bottom: 5px;
    color: #475569;
    display: block;
}

input,
select,
textarea {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    background-color: #f8fafc;
    color: #1e293b;
    font-size: 14px;
}

input[readonly] {
    background-color: #e2e8f0;
    cursor: not-allowed;
}

input:focus,
select:focus,
textarea:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 6px rgba(99,102,241,0.25);
    background-color: #ffffff;
}

.role-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 6px;
    max-height: 110px;
    overflow-y: auto;
    background: #f9fafb;
}

.role-list::-webkit-scrollbar {
    width: 6px;
}

.role-list::-webkit-scrollbar-thumb {
    background: #c7d2fe;
    border-radius: 10px;
}

.role-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 6px;
    font-size: 14px;
    border-radius: 6px;
}

.role-item:hover {
    background-color: #eef2ff;
}

.role-item input[type="checkbox"] {
    width: 16px;
    height: 16px;
    margin: 0;
    cursor: pointer;
}

.checkbox-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
}

.checkbox-wrapper input[type="checkbox"] {
    width: 16px;
    height: 16px;
    margin: 0;
}

button {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 8px;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    font-weight: 600;
    cursor: pointer;
    margin-top: 10px;
}

button:hover {
    box-shadow: 0 5px 15px rgba(99,102,241,0.3);
}

.error-msg {
    color: red;
    font-size: 13px;
    text-align: center;
    margin-bottom: 10px;
}

.success-msg {
    color: green;
    font-size: 13px;
    text-align: center;
    margin-bottom: 10px;
}

.link-btn {
    display: block;
    text-align: center;
    margin-top: 15px;
    color: #4f46e5;
    text-decoration: none;
    font-size: 13px;
}

.link-btn:hover {
    text-decoration: underline;
}

</style>

<script>

function validatePartyType() {

    const checkboxes = document.querySelectorAll('input[name="partyType"]');
    const errorDiv = document.getElementById("partyTypeError");

    let checked = false;

    checkboxes.forEach(cb => {
        if (cb.checked) {
            checked = true;
        }
    });

    if (!checked) {
        errorDiv.innerText = "Select atleast one party type";
        return false;
    }

    errorDiv.innerText = "";
    return true;
}

function copyShippingAddress() {

    const checkbox = document.getElementById("sameAddress");

    if (checkbox.checked) {

        document.getElementById("billingAddressLine").value =
            document.getElementById("shippingAddressLine").value;

        document.getElementById("billingCity").value =
            document.getElementById("shippingCity").value;

        document.getElementById("billingState").value =
            document.getElementById("shippingState").value;

        document.getElementById("billingZipcode").value =
            document.getElementById("shippingZipcode").value;

        document.getElementById("billingCountry").value =
            document.getElementById("shippingCountry").value;
    }
}

</script>

</head>

<body>

<div class="container">

<h2>Update Customer</h2>

<c:if test="${not empty customer}">
    <div class="success-msg">${customer}</div>
</c:if>

<c:if test="${not empty message}">
    <div class="error-msg">${message}</div>
</c:if>

<form:form action="${pageContext.request.contextPath}/customer/update"
           method="post"
           modelAttribute="customerDto"
           onsubmit="return validatePartyType()">

<div class="form-grid">

    <div>
        <label>Name</label>
        <form:input path="name"
                    class="form-control"
                    readonly="true"/>
    </div>

    <div>
        <label>Phone Number</label>
        <form:input path="phoneNo"
                    class="form-control"
                    readonly="true"/>
    </div>

    <div>
        <label>Email</label>
        <form:input path="identifier"
                    type="email"
                    class="form-control"
                    readonly="true"/>
    </div>

    <div>
        <label>Party Types</label>

        <div class="role-list">

            <div class="role-item">
                <span>Customer</span>

                <input type="checkbox"
                       name="partyType"
                       value="customer"
                       <c:if test="${not empty customerDto.partyType and customerDto.partyType.contains('customer')}">checked</c:if> />
            </div>

            <div class="role-item">
                <span>Dealer</span>

                <input type="checkbox"
                       name="partyType"
                       value="dealer"
                       <c:if test="${not empty customerDto.partyType and customerDto.partyType.contains('dealer')}">checked</c:if> />
            </div>

            <div class="role-item">
                <span>Wholesaler</span>

                <input type="checkbox"
                       name="partyType"
                       value="wholesaler"
                       <c:if test="${not empty customerDto.partyType and customerDto.partyType.contains('wholesaler')}">checked</c:if> />
            </div>

        </div>

        <div id="partyTypeError" class="error-msg"></div>
    </div>

    <div>
        <label>Balance</label>
        <form:input path="balance"
                    type="number"
                    step="0.01"
                    min="0"
                    class="form-control"
                    required="true"/>
    </div>

    <div>
        <label>Credit Limit</label>
        <form:input path="creditLimit"
                    type="number"
                    step="0.01"
                    min="0"
                    class="form-control"
                    required="true"/>
    </div>

    <div class="full-width section-title">
        Shipping Address
    </div>

    <div class="full-width">
        <label>Address Line</label>
        <form:input path="shippingAddress.addressLine"
                    id="shippingAddressLine"
                    class="form-control"
                    required="true"
                    minlength="5"
                    title="Address should contain minimum 5 characters"/>
    </div>

    <div>
        <label>City</label>
        <form:input path="shippingAddress.city"
                    id="shippingCity"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"
                    title="Enter a valid city"/>
    </div>

    <div>
        <label>State</label>
        <form:input path="shippingAddress.state"
                    id="shippingState"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"
                    title="Enter a valid state"/>
    </div>

    <div>
        <label>Zip Code</label>
        <form:input path="shippingAddress.zipcode"
                    id="shippingZipcode"
                    type="text"
                    class="form-control"
                    required="true"
                    pattern="[0-9]{6}"
                    maxlength="6"
                    title="Enter valid 6 digit zipcode"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>
    </div>

    <div>
        <label>Country</label>
        <form:input path="shippingAddress.country"
                    id="shippingCountry"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"
                    title="Enter a valid country"/>
    </div>

    <div class="full-width checkbox-wrapper">
        <input type="checkbox"
               id="sameAddress"
               onclick="copyShippingAddress()">

        <label for="sameAddress">
            Billing Address same as Shipping Address
        </label>
    </div>

    <div class="full-width section-title">
        Billing Address
    </div>

    <div class="full-width">
        <label>Address Line</label>
        <form:input path="billingAddress.addressLine"
                    id="billingAddressLine"
                    class="form-control"
                    required="true"
                    minlength="5"/>
    </div>

    <div>
        <label>City</label>
        <form:input path="billingAddress.city"
                    id="billingCity"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"/>
    </div>

    <div>
        <label>State</label>
        <form:input path="billingAddress.state"
                    id="billingState"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"/>
    </div>

    <div>
        <label>Zip Code</label>
        <form:input path="billingAddress.zipcode"
                    id="billingZipcode"
                    type="text"
                    class="form-control"
                    required="true"
                    pattern="[0-9]{6}"
                    maxlength="6"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>
    </div>

    <div>
        <label>Country</label>
        <form:input path="billingAddress.country"
                    id="billingCountry"
                    class="form-control"
                    required="true"
                    pattern="[A-Za-z ]{2,}"/>
    </div>

</div>

<button type="submit">
    Update Customer
</button>

<a href="${pageContext.request.contextPath}/customer/list"
   class="link-btn">
    Go Back
</a>

</form:form>

</div>

</body>
</html>