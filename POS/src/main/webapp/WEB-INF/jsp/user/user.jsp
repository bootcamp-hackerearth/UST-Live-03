<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update User</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

<style>
    html, body {
        margin: 0;
        padding: 0;
        height: 100vh;
        width: 100vw;
        overflow: hidden; /* Disables all scrolling and locking viewport */
    }

    body {
        font-family: "Segoe UI", Arial, sans-serif;
        background: linear-gradient(135deg, #ede9fe, #ddd6fe); /* light purple gradient */
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .container {
        width: 420px;
        background: #ffffff;
        padding: 25px 30px;
        border-radius: 12px;
        box-shadow: 0 20px 40px rgba(76, 29, 149, 0.18); /* soft purple shadow */
        box-sizing: border-box;
    }

    h2 {
        text-align: center;
        margin-top: 0;
        margin-bottom: 20px;
        color: #6d28d9;
        font-weight: 600;
        font-size: 24px;
    }

    label {
        margin-top: 14px;
        display: block;
        font-weight: 600;
        font-size: 13px;
        color: #4c1d95;
    }

    input, select {
        width: 100%;
        margin-top: 5px;
        padding: 9px;
        border: 1px solid #c4b5fd;
        border-radius: 6px;
        font-size: 13px;
        box-sizing: border-box;
    }

    input:focus, select:focus {
        outline: none;
        border-color: #a78bfa;
        box-shadow: 0 0 0 0.15rem rgba(167, 139, 250, 0.35);
    }

    select[multiple] {
        height: 100px;
    }

    button {
        margin-top: 22px;
        width: 100%;
        padding: 11px;
        background: #7c3aed;
        color: #ffffff;
        border: none;
        font-weight: 600;
        border-radius: 6px;
        cursor: pointer;
        font-size: 13px;
    }

    button:hover {
        background: #6d28d9;
    }

    a {
        display: block;
        text-align: center;
        margin-top: 16px;
        color: #6d28d9;
        font-weight: 600;
        text-decoration: none;
        font-size: 13px;
    }

    a:hover {
        text-decoration: underline;
        color: #5b21b6;
    }

    /* SERVER MESSAGE */
    .server-msg {
        margin-bottom: 12px;
        padding: 8px;
        border-radius: 6px;
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
        margin-top: 3px;
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

        <label>Name</label>
        <form:input path="name"/>

        <label>Email</label>
        <form:input path="username"/>
        <div id="emailErr" class="invalid-feedback"></div>

        <label>Phone Number</label>
        <form:input path="phoneNo" maxlength="10"/>
        <div id="phoneErr" class="invalid-feedback"></div>

        <label>Roles</label>
        <form:select path="roles" multiple="true">
            <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <button type="submit">Update</button>

    </form:form>

    <a href="${pageContext.request.contextPath}/user/list">
        ← Back to User List
    </a>

</div>

<div id="toast" class="toast-message"></div>

</body>
</html>