<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Warehouse</title>

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

.card {
    width:450px;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h4 {
    text-align:center;
    margin-bottom:20px;
    color:#fff;
}

.message {
    text-align:center;
    margin-bottom:10px;
    color:#ff8080;
}

.alert {
    text-align:center;
    padding:10px;
    border-radius:6px;
    margin-bottom:15px;
    color:#ff8080;
}

.form-group {
    margin-bottom:15px;
}

.form-label {
    color:#ccc;
    font-size:14px;
    display:block;
    margin-bottom:5px;
}

.form-control {
    width:100%;
    padding:10px;
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

input[readonly] {
    background:rgba(255,255,255,0.05);
    color:#888;
}

.field-error {
    color:#ff8080;
    font-size:11px;
    margin-top:3px;
    display:none;
}

.btn-group {
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-top:10px;
}

.btn-primary {
    background:#00ffff;
    color:#000;
    border:none;
    border-radius:8px;
    padding:10px 16px;
    font-weight:bold;
    cursor:pointer;
}

.btn-primary:hover {
    box-shadow:0 0 15px #00ffff;
}

.btn-outline-secondary {
    border-radius:8px;
    padding:10px 16px;
    text-decoration:none;
    color:#ccc;
    border:1px solid #ccc;
}

.btn-outline-secondary:hover {
    color:#fff;
    border-color:#fff;
}
</style>
</head>

<body>

<div class="card">

<h4>Edit Warehouse</h4>
<div class="message">${message}</div>

<c:if test="${empty warehouse}">
    <div class="alert">Warehouse not found</div>
</c:if>

<c:if test="${not empty warehouse}">

<form:form action="/warehouse/update"
           method="post"
           modelAttribute="warehouse"
           onsubmit="return validateForm()">

    <form:hidden path="id" />

    <div class="form-group">
        <label class="form-label">Warehouse Name</label>
        <form:input path="identifier"
                    cssClass="form-control"
                    readonly="true" />
    </div>

    <div class="form-group">
        <label class="form-label">Country</label>
        <form:input path="country"
                    cssClass="form-control"
                    required="true"
                    minlength="2"
                    maxlength="50"
                    pattern="[A-Za-z ]+"
                    title="Enter valid country name" />
    </div>

    <div class="form-group">
        <label class="form-label">Pincode</label>
        <form:input path="pincode"
                    cssClass="form-control"
                    id="pincode"
                    required="true"
                    minlength="5"
                    maxlength="5"
                    pattern="[0-9]{5}"
                    placeholder="Enter 5-digit pincode"
                    title="Pincode must be exactly 5 numeric digits" />
        <span class="field-error" id="pincodeError">Pincode must be exactly 5 numeric digits.</span>
    </div>

    <div class="form-group">
        <label class="form-label">Address</label>
        <form:input path="address"
                    cssClass="form-control"
                    required="true"
                    minlength="5"
                    maxlength="200" />
    </div>

    <div class="btn-group">
        <a href="/warehouse/list" class="btn-outline-secondary">
            Cancel
        </a>
        <button type="submit" class="btn-primary">
            Update
        </button>
    </div>

</form:form>

</c:if>

</div>

<script>
    const pincodeInput = document.getElementById('pincode');
    const pincodeError = document.getElementById('pincodeError');

    if (pincodeInput) {
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
    }

    function validateForm() {
        if (pincodeInput && pincodeInput.value.length !== 5) {
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