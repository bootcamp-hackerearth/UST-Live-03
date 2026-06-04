<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Login</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

<style>
body {
    margin: 0;
    font-family: 'Inter', sans-serif;
    background: #d1d5db;
}

/* SAME AS NODE */
.container {
    width: 420px;
    margin: 100px auto;
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
input {
    width: 100%;
    margin-top: 6px;
    padding: 10px;
    border: 1px solid #cbd5f5;
    border-radius: 8px;
    font-size: 13px;
    outline: none;
    transition: 0.2s;
}

input:focus {
    border-color: #0891b2;
    box-shadow: 0 0 0 2px rgba(8,145,178,0.2);
}

/* BUTTON */
.btn-login {
    margin-top: 28px;
    width: 100%;
    padding: 12px;
    background: linear-gradient(135deg, #0891b2, #0e7490);
    color: white;
    border: none;
    border-radius: 20px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.25s;
}

.btn-login:hover {
    background: linear-gradient(135deg, #0e7490, #075985);
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(8,145,178,0.4);
}

/* ERROR MESSAGE */
.error-message {
    margin-bottom: 15px;
    padding: 10px;
    background: #fee2e2;
    color: #b91c1c;
    border-radius: 8px;
    text-align: center;
    font-size: 13px;
}

/* REGISTER LINK */
.register-text {
    text-align: center;
    margin-top: 20px;
    font-size: 13px;
    color: #334155;
}

.register-text a {
    display: inline-block;
    margin-top: 6px;
    color: #0891b2;
    font-weight: 600;
    text-decoration: none;
}

.register-text a:hover {
    color: #0e7490;
    text-decoration: underline;
}
</style>

<script>
function validateLoginForm() {
    let username = document.getElementsByName("username")[0].value.trim();
    let password = document.getElementsByName("password")[0].value.trim();

    if (username === "" || password === "") {
        alert("Please fill in all fields.");
        return false;
    }

    let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;
    if (!username.match(emailPattern)) {
        alert("Enter a valid email address.");
        return false;
    }
    return true;
}
</script>

</head>

<body>

<div class="container">

<h2>Login</h2>

<c:if test="${not empty errorMsg}">
    <div class="error-message">
        ${errorMsg}
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/login"
      method="post"
      onsubmit="return validateLoginForm()">

    <label for="usernameField">Email</label>
    <input type="email" id="usernameField" name="username" required />

    <label for="passwordField">Password</label>
    <input type="password" id="passwordField" name="password" required />

    <button type="submit" class="btn-login">Login</button>

</form>

<div class="register-text">
    Don’t have an account?
    <a href="${pageContext.request.contextPath}/register">Register</a>
</div>

</div>

</body>
</html>