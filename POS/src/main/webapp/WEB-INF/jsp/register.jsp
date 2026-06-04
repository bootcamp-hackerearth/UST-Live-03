<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>User Registration</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap"
      rel="stylesheet">

<style>
    html, body {
        margin: 0;
        padding: 0;
        height: 100vh;
        width: 100vw;
        overflow: hidden; /* Disables all scrolling and locks viewport */
    }

    body {
        font-family: 'Inter', sans-serif;
        background: linear-gradient(135deg, #ede9fe, #ddd6fe);
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .back-arrow {
        position: fixed;
        top: 20px;
        left: 20px;
        width: 42px;
        height: 42px;
        background: #ffffff;
        border: 1px solid #c4b5fd;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        text-decoration: none;
        color: #6d28d9;
        font-size: 18px;
        font-weight: 600;
        box-shadow: 0 6px 15px rgba(76, 29, 149, 0.18);
        transition: 0.2s;
    }

    .back-arrow:hover {
        background: #7c3aed;
        color: #ffffff;
    }

    .register-card {
        width: 420px;
        background: #ffffff;
        padding: 25px 30px;
        border-radius: 12px;
        box-shadow: 0 20px 40px rgba(76, 29, 149, 0.18);
        box-sizing: border-box;
    }

    h2 {
        text-align: center;
        margin-top: 0;
        margin-bottom: 20px;
        font-size: 20px;
        color: #6d28d9;
        font-weight: 600;
    }

    .error-message {
        margin-bottom: 14px;
        padding: 10px 12px;
        background-color: #efe9ff;
        color: #5b21b6;
        border: 1px solid #c4b5fd;
        border-radius: 8px;
        font-size: 13px;
        text-align: center;
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
        border-color: #7c3aed;
        box-shadow: 0 0 0 2px rgba(124,58,237,0.25);
    }

    select[multiple] {
        height: 100px;
    }

    .btn-submit {
        margin-top: 22px;
        width: 100%;
        padding: 11px;
        background: linear-gradient(135deg, #7c3aed, #6d28d9);
        color: white;
        border: none;
        border-radius: 6px;
        font-weight: 600;
        cursor: pointer;
        font-size: 13px;
    }

    .btn-submit:hover {
        background: linear-gradient(135deg, #6d28d9, #5b21b6);
    }
</style>

<script>
function validateForm() {

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("username").value.trim();
    const roles = document.getElementById("roles").selectedOptions.length;
    const phone = document.getElementById("phoneNo").value.trim();
    const password = document.getElementById("password").value.trim();

    const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;

    if (!name || !email || roles === 0 || !phone || !password) {
        alert("Please fill all the details before registering");
        return false;
    }

    if (!gmailRegex.test(email)) {
        alert("Email must be in the format abc@gmail.com");
        return false;
    }

    return true;
}
</script>

</head>

<body>
<a href="${pageContext.request.contextPath}/user/list" class="back-arrow">←</a>

<div class="register-card">

<h2>User Registration</h2>

<c:if test="${not empty message}">
    <div class="error-message">${message}</div>
</c:if>

<form:form action="register"
           method="post"
           modelAttribute="userDto"
           onsubmit="return validateForm();">

    <label>Name</label>
    <form:input path="name" id="name" required="true"/>

    <label>Email</label>
    <form:input path="username"
                id="username"
                required="true"
                pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                title="Email must be in the format name@gmail.com"/>

    <label>Roles</label>
    <form:select path="roles" id="roles" multiple="true" required="true">
        <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
    </form:select>

    <label>Mobile number</label>
    <form:input path="phoneNo"
                id="phoneNo"
                type="tel"
                pattern="[0-9]{10}"
                maxlength="10"
                required="true"/>

    <label>Password</label>
    <form:password path="password" id="password" required="true"/>

    <input type="submit" value="Register" class="btn-submit"/>

</form:form>

</div>

</body>
</html>