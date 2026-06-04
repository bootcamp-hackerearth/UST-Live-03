<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Warehouse</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    padding: 40px 20px;
}

.container {
    width: 95%;
    max-width: 500px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 25px;
    font-weight: 600;
    color: #0f172a;
}

.form-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 25px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-size: 14px;
    font-weight: 500;
}

input, textarea {
    width: 100%;
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
}

input:focus, textarea:focus {
    outline: none;
    border-color: #6366f1;
}

textarea {
    resize: none;
}

input[readonly] {
    background: #f1f5f9;
    cursor: not-allowed;
}

.error-box {
    background: #fee2e2;
    color: #7f1d1d;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    font-size: 13px;
}

.field-error {
    color: #dc2626;
    font-size: 12px;
    margin-top: 4px;
}

.btn-container {
    text-align: center;
    margin-top: 20px;
}

.btn {
    display: inline-block;
    padding: 10px 18px;
    margin: 6px;
    border-radius: 6px;
    border: none;
    font-size: 14px;
    cursor: pointer;
    text-decoration: none;
    color: white;
}

.save-btn {
    background: #6366f1;
}

.save-btn:hover {
    background: #4f46e5;
}

.cancel-btn {
    background: #0ea5e9;
}

.cancel-btn:hover {
    background: #0284c7;
}

</style>

<script>
function validateForm() {

    let valid = true;
    document.querySelectorAll(".field-error").forEach(e => e.innerText = "");

    const country = document.querySelector('[name="country"]');
    const region = document.querySelector('[name="region"]');
    const address = document.querySelector('[name="address"]');
    const phone = document.querySelector('[name="phoneNumber"]');

    if (!country.value.trim()) {
        showError(country, "Country is required");
        valid = false;
    }

    if (!region.value.trim()) {
        showError(region, "Region is required");
        valid = false;
    }

    if (!address.value.trim()) {
        showError(address, "Address is required");
        valid = false;
    }

    if (!phone.value.trim()) {
        showError(phone, "Phone number is required");
        valid = false;
    } else if (!/^[0-9]{10}$/.test(phone.value)) {
        showError(phone, "Phone number must be exactly 10 digits");
        valid = false;
    }

    return valid;
}

function showError(input, message) {
    const errorDiv = input.nextElementSibling;
    errorDiv.innerText = message;
}

function allowOnlyNumbers(event) {
    event.target.value = event.target.value.replace(/[^0-9]/g, '');
}
</script>

</head>

<body>

<div class="container">

    <h2>Edit Warehouse</h2>

    <div class="form-container">

        <c:if test="${not empty message}">
            <div class="error-box">${message}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/warehouse/update"
              method="post"
              onsubmit="return validateForm()">

            <input type="hidden" name="identifier" value="${warehouse.identifier}" />

            <div class="form-group">
                <label>Warehouse Name</label>
                <input type="text" value="${warehouse.identifier}" readonly />
            </div>

            <div class="form-group">
                <label>Country</label>
                <input type="text" name="country" value="${warehouse.country}" />
                <div class="field-error"></div>
            </div>

            <div class="form-group">
                 <label>Region</label>
                 <input type="text" name="region" value="${warehouse.region}" />
                 <div class="field-error"></div>
            </div>

            <div class="form-group">
                <label>Address</label>
                <textarea name="address" rows="3">${warehouse.address}</textarea>
                <div class="field-error"></div>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <input type="text" name="phoneNumber" maxlength="10"
                       value="${warehouse.phoneNumber}"
                       oninput="allowOnlyNumbers(event)" />
                <div class="field-error"></div>
            </div>

            <div class="btn-container">
                <button type="submit" class="btn save-btn">Update Warehouse</button>

                <a href="${pageContext.request.contextPath}/warehouse/list" class="btn cancel-btn">
                    Cancel
                </a>
            </div>

        </form>

    </div>

</div>

</body>
</html>