<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Warehouse</title>

<style>
* {
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body {
    height:100vh;
    display:flex;
    justify-content:center;
    align-items:center;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
}

.card-container {
    width:420px;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    text-align:center;
    margin-bottom:20px;
    color:#fff;
}

.back-icon {
    position:absolute;
    top:20px;
    left:20px;
    color:#00ffff;
    text-decoration:none;
    font-size:20px;
}

.form-group {
    margin-bottom:15px;
}

label {
    color:#ccc;
    font-size:14px;
}

.form-control {
    width:100%;
    padding:10px;
    margin-top:6px;
    border-radius:8px;
    border:1px solid transparent;
    outline:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
    transition:border 0.3s, box-shadow 0.3s;
}

.form-control:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

.form-control.invalid {
    border:1px solid #ff4d4d;
    box-shadow:0 0 6px #ff4d4d;
}

.field-error {
    color:#ff8080;
    font-size:11px;
    margin-top:3px;
    display:none;
}

.btn-submit {
    width:100%;
    padding:12px;
    border-radius:8px;
    border:none;
    background:#00ffff;
    color:#000;
    font-weight:bold;
    cursor:pointer;
    margin-top:5px;
}

.btn-submit:hover {
    box-shadow:0 0 15px #00ffff,
               0 0 30px #00ffff;
}

.alert {
    text-align:center;
    padding:10px;
    margin-bottom:10px;
    border-radius:6px;
    color:#00ffcc;
}

.error-message {
    text-align:center;
    padding:10px;
    margin-bottom:10px;
    border-radius:6px;
    color:#ff8080;
}
</style>
</head>

<body>

<div class="card-container">

<a href="/warehouse/list" class="back-icon">←</a>

<h2>Add New Warehouse</h2>

<c:if test="${not empty warehouse}">
    <div class="alert">${warehouse}</div>
</c:if>

<c:if test="${not empty message}">
    <div class="error-message">${message}</div>
</c:if>

<form:form method="post"
           action="/warehouse/add"
           modelAttribute="warehouseDto"
           onsubmit="return validateForm()">

    <div class="form-group">
        <label>Warehouse Name</label>
        <form:input path="identifier"
                    cssClass="form-control"
                    placeholder="Enter warehouse name"
                    required="true"
                    minlength="2"
                    maxlength="50"
                    pattern="[A-Za-z0-9 ]+"
                    title="Enter valid warehouse name"/>
    </div>

    <div class="form-group">
        <label>Country</label>
        <form:input path="country"
                    cssClass="form-control"
                    placeholder="Enter country"
                    required="true"
                    minlength="2"
                    maxlength="50"
                    pattern="[A-Za-z ]+"
                    title="Enter valid country name"/>
    </div>

    <div class="form-group">
        <label>Pincode</label>
        <form:input path="pincode"
                    cssClass="form-control"
                    id="pincode"
                    placeholder="Enter 5-digit pincode"
                    required="true"
                    minlength="5"
                    maxlength="5"
                    pattern="[0-9]{5}"
                    title="Pincode must be exactly 5 numeric digits"/>
        <span class="field-error" id="pincodeError">Pincode must be exactly 5 numeric digits.</span>
    </div>

    <div class="form-group">
        <label>Address</label>
        <form:input path="address"
                    cssClass="form-control"
                    placeholder="Enter address"
                    required="true"
                    minlength="5"
                    maxlength="200"/>
    </div>

    <button type="submit" class="btn-submit">
        Add Warehouse
    </button>

</form:form>

</div>

<script>
    const pincodeInput = document.getElementById('pincode');
    const pincodeError = document.getElementById('pincodeError');

    pincodeInput.addEventListener('input', function () {
        this.value = this.value.replace(/[^0-9]/g, '');

        if (this.value.length === 5) {
            this.classList.remove('invalid');
            pincodeError.style.display = 'none';
        } else {
            this.classList.add('invalid');
            pincodeError.style.display = 'block';
        }
    });

    function validateForm() {
        if (pincodeInput.value.length !== 5) {
            pincodeInput.classList.add('invalid');
            pincodeError.style.display = 'block';
            pincodeInput.focus();
            return false;
        }
        return true;
    }
</script>

</body>
</html>