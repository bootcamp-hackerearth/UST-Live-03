<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<title>Update User</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

<style>
body {
    margin: 0;
    font-family: 'Inter', sans-serif;
    background: #d1d5db;
}

/* CONTAINER */
.container {
    width: 520px;
    margin: 80px auto;
    background: #f1f5f9;
    padding: 35px;
    border-radius: 16px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

/* TITLE */
h2 {
    text-align: center;
    margin-bottom: 25px;
    font-size: 22px;
    color: #0891b2;
    font-weight: 600;
}

/* LABEL */
label {
    margin-top: 16px;
    display: block;
    font-weight: 600;
    font-size: 13px;
    color: #334155;
}

/* INPUT */
input, select {
    width: 100%;
    margin-top: 6px;
    padding: 10px;
    border: 1px solid #cbd5f5;
    border-radius: 8px;
    font-size: 13px;
    outline: none;
    transition: 0.2s;
    box-sizing: border-box; /* Preserves explicit layout with nested elements */
}

input:focus, select:focus {
    border-color: #0891b2;
    box-shadow: 0 0 0 2px rgba(8,145,178,0.2);
}

select[multiple] {
    height: 120px;
}

/* UPDATE BUTTON */
.btn-update {
    padding: 10px 20px;
    background: linear-gradient(135deg, #0891b2, #0e7490);
    color: white;
    border: none;
    border-radius: 20px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.25s;
}

.btn-update:hover {
    background: linear-gradient(135deg, #0e7490, #075985);
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(8,145,178,0.4);
}

/* BUTTON GROUP */
.btn-group {
    display: flex;
    justify-content: space-between;
    margin-top: 25px;
}

/* BACK BUTTON */
.back-btn {
    background: #64748b;
    color: white;
    padding: 10px 20px;
    border-radius: 20px;
    text-decoration: none;
    font-weight: 600;
    transition: 0.25s;
}

.back-btn:hover {
    background: #475569;
}

/* SERVER MESSAGE */
.server-msg {
    margin-bottom: 15px;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
    font-size: 13px;
}

.server-msg.success {
    background: #dcfce7;
    color: #166534;
}

.server-msg.error {
    background: #fee2e2;
    color: #b91c1c;
}

/* FIELD ERROR */
.invalid-feedback {
    font-size: 12px;
    color: #b91c1c;
    margin-top: 4px;
}

.error-border {
    border: 1px solid #b91c1c !important;
}

/* TOAST */
.toast-message {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background: #1f2937;
    color: white;
    padding: 12px 16px;
    border-radius: 10px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    opacity: 0;
    transform: translateY(20px);
    transition: 0.3s;
    font-size: 13px;
}

.toast-message.show {
    opacity: 1;
    transform: translateY(0);
}
</style>

<script>
function validateForm() {

    document.getElementById("emailErr").innerText = "";
    document.getElementById("phoneErr").innerText = "";

    let emailInput = document.getElementsByName("username")[0];
    let phoneInput = document.getElementsByName("phoneNo")[0];

    emailInput.classList.remove("error-border");
    phoneInput.classList.remove("error-border");

    let name = document.getElementsByName("name")[0].value.trim();
    let email = emailInput.value.trim();
    let phone = phoneInput.value.trim();

    if (!name || !email || !phone) {
        showToast("All fields are required");
        return false;
    }

    let valid = true;

    let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;
    if (!emailPattern.test(email)) {
        document.getElementById("emailErr").innerText = "Enter a valid email address";
        emailInput.classList.add("error-border");
        valid = false;
    }

    let phonePattern = /^[0-9]{10}$/;
    if (!phonePattern.test(phone)) {
        document.getElementById("phoneErr").innerText = "Phone must be exactly 10 digits";
        phoneInput.classList.add("error-border");
        valid = false;
    }

    return valid;
}

function showToast(msg) {
    const toast = document.getElementById("toast");
    toast.innerText = msg;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 2500);
}
</script>

</head>

<body>

<div class="container">

<h2>Update User</h2>

<c:if test="${not empty message}">
    <div class="server-msg ${messageType == 'success' ? 'success' : 'error'}">
        ${message}
    </div>
</c:if>

<form:form action="${pageContext.request.contextPath}/user/update"
           method="post"
           modelAttribute="user"
           onsubmit="return validateForm()">

    <form:hidden path="id"/>

    <label for="name">
        Name
        <form:input path="name" id="name"/>
    </label>

    <label for="username">
        Email
        <form:input path="username" id="username"/>
    </label>
    <div id="emailErr" class="invalid-feedback"></div>

    <label for="phoneNo">
        Phone Number
        <form:input path="phoneNo" id="phoneNo" maxlength="10"/>
    </label>
    <div id="phoneErr" class="invalid-feedback"></div>

    <label for="roles">
        Roles
        <form:select path="roles" id="roles" multiple="true">
            <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>
    </label>

    <div class="btn-group">

        <a href="${pageContext.request.contextPath}/user/list"
           class="back-btn">
            Back to Users
        </a>

        <button type="submit" class="btn-update">
            Update User
        </button>

    </div>

</form:form>

</div>

<div id="toast" class="toast-message"></div>

</body>
</html>