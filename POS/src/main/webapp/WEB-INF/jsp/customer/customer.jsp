<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<style>
body {
    font-family: Arial, sans-serif;
    background: linear-gradient(to bottom, #ffffff, #e9ecef, #cccccc);
    margin: 0;
    padding: 40px 0;
}

.container {
    max-width: 700px;
    margin: auto;
}

.card {
    background: #ffffff;
    padding: 25px;
    border-radius: 12px;
    border: none;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
    text-align: center;
    border-bottom: 1px solid #eee;
    margin-bottom: 20px;
    padding-bottom: 10px;
}

h4 {
    margin: 10px 0;
    font-weight: 600;
    color: #333;
}

h5 {
    margin-top: 20px;
    margin-bottom: 10px;
    font-weight: 600;
    color: #444;
    border-left: 4px solid #0d6efd;
    padding-left: 8px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-weight: 600;
    color: #333;
}

input, select {
    width: 100%;
    padding: 10px;
    box-sizing: border-box;
    margin-bottom: 15px;
    border-radius: 6px;
    border: 1px solid #ccc;
    transition: 0.2s ease;
}

input:focus, select:focus {
    border-color: #0d6efd;
    outline: none;
    box-shadow: 0 0 4px rgba(13, 110, 253, 0.3);
}

select[multiple] {
    height: 100px;
}

.btn-submit {
    width: 100%;
    padding: 12px;
    background: #0d6efd;
    border: none;
    color: #fff;
    cursor: pointer;
    border-radius: 6px;
    font-weight: 600;
    transition: 0.3s ease;
}

.btn-submit:hover {
    background: #0b5ed7;
}

.alert {
    padding: 12px;
    margin-bottom: 15px;
    border-radius: 6px;
    text-align: center;
    font-size: 14px;
}

.alert-success {
    background: #e6f4ea;
    color: #1e7e34;
    border: 1px solid #c3e6cb;
}
</style>
<head>
    <title>Customer Updation</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
<style>
   body {
              background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
             min-height: 100vh;
         }
         .card {
             border-radius: 12px;
         }
         .form-control {
             border-radius: 8px;
         }
         .card-header{
             background: #ffffff;
         }
</style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center text-black">
                <h4 class="mb-0">Update Customer</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty customer}">
                    <div class="alert alert-success text-center">
                        ${customer}
                    </div>
                </c:if>


    <form:form action="/customer/update" method="post" modelAttribute="customerDto">


        <div class="mb-3">
            <label>Name</label>
            <form:input path="name" readonly="true"/>
        </div>

        <div class="mb-3">
                    <label>Phone Number</label>

                    <form:input path="phoneNo"
                    type="tel" title="9999999999" maxlength="10" required="true"/>
                </div>


        <div class="mb-3">
            <label>Email</label>

            <form:input path="identifier" type="email"
            title="@example.com" required="true"
            readonly="true"/>
        </div>


<div class="mb-3">
    <label>Party Types</label>
    <form:select path="userType" multiple="true">
        <form:option value="customer">Customer</form:option>
        <form:option value="dealer">Dealer</form:option>
        <form:option value="wholesaler">Wholesaler</form:option>
    </form:select>
</div>


        <div class="mb-3">
            <label>Balance</label>
            <form:input
            path="balance"
            required="true"
            type="number"

            pattern="[0-9]"
            />
        </div>


<div class="mb-3">
    <label>Credit Limit</label>
    <form:input
        path="creditLimit"
        required="true"
        title="Enter Credit Limit"
         />
</div>

<h5>Shipping Address</h5>

<div class="mb-3">
    <label>Address Line</label>
    <form:input path="shippingAddress.addressLine" required="true" />
</div>

<div class="mb-3">
    <label>City</label>
    <form:input path="shippingAddress.city" required="true" />
</div>

<div class="mb-3">
    <label>State</label>
    <form:input path="shippingAddress.state" required="true"/>
</div>

<div class="mb-3">
    <label>Zip Code</label>
    <form:input path="shippingAddress.zipcode" required="true" />
</div>

<div class="mb-3">
    <label>Country</label>
    <form:input path="shippingAddress.country" required="true"/>
</div>
<div class="mb-3">
    <label>Address Types</label>
    <form:select path="shippingAddress.addressType">
        <form:option value="shippingAddress">Shipping Address</form:option>
    </form:select>
</div>

<h5>Billing Address</h5>

<div class="mb-3">
    <label>Address Line</label>
    <form:input path="billingAddress.addressLine" required="true"/>
</div>

<div class="mb-3">
    <label>City</label>
    <form:input path="billingAddress.city" required="true" />
</div>

<div class="mb-3">
    <label>State</label>
    <form:input path="billingAddress.state" required="true"/>
</div>

<div class="mb-3">
    <label>Zip Code</label>
    <form:input path="billingAddress.zipcode" required="true" />
</div>

<div class="mb-3">
    <label>Country</label>
    <form:input path="billingAddress.country" required="true" />
</div>
<div class="mb-3">
    <label>Address Types</label>
    <form:select path="billingAddress.addressType">
        <form:option value="billingAddress">Billing Address</form:option>
    </form:select>
</div>
        <input type="submit" class="btn-submit"/>

    </form:form>

     <c:if test="${not empty message}">
            <div style="
                background:#f8d7da;
                color:#721c24;
                padding:10px;
                margin-bottom:15px;
                border-radius:4px;
                text-align:center;">
                ${message}
            </div>
        </c:if>
</div>
</div>
</body>
</html>