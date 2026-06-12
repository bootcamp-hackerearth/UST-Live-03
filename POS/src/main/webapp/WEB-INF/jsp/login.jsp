<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<head>
<meta charset="UTF-8">
<title>Login</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    color: #1e293b;
}

/* Card */
.container {
    width: 100%;
    max-width: 520px;
    background: #ffffff;
    padding: 45px 40px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 20px 40px rgba(0,0,0,0.08);
    animation: fadeIn 0.6s ease-in-out;
}

/* Headings */
.main-title {
    text-align: center;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #4f46e5;
    margin-bottom: 5px;
}

.sub-title {
    text-align: center;
    font-size: 18px;
    font-weight: 500;
    color: #64748b;
    margin-bottom: 25px;
}

/* Input group */
.input-group {
    margin-bottom: 18px;
}

/* Label */
label {
    font-size: 13px;
    color: #475569;
    display: block;
    margin-bottom: 6px;
}

/* Input */
input {
    width: 100%;
    padding: 13px;
    border-radius: 10px;
    border: 1px solid #cbd5f5;
    background: #f8fafc;
    color: #1e293b;
    transition: all 0.2s ease;
}

input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 8px rgba(99,102,241,0.25);
    background: #ffffff;
}

/* Button */
button {
    width: 100%;
    padding: 15px;
    margin-top: 10px;
    border-radius: 10px;
    border: none;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    font-weight: 600;
    font-size: 15px;
    cursor: pointer;
    transition: all 0.25s ease;
}

button:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 20px rgba(99,102,241,0.3);
}

/* Footer */
.footer {
    margin-top: 18px;
    text-align: center;
    font-size: 13px;
    color: #64748b;
}

.footer a {
    text-decoration: none;
    color: #4f46e5;
    font-weight: 500;
}

.footer a:hover {
    text-decoration: underline;
}

/* Error */
.error-msg {
    margin-bottom: 15px;
    padding: 12px;
    text-align: center;
    border-radius: 8px;
    background: #fee2e2;
    color: #b91c1c;
    font-size: 13px;
}

/* Animation */
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(15px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

</style>
</head>

<body>

<div class="container">

    <!-- Heading -->
    <div class="main-title">POS SYSTEM</div>
    <div class="sub-title">LOGIN</div>

    <form th:action="@{/login}" method="post">

        <!-- Error -->
        <c:if test="${param.error != null}">
            <div class="error-msg">
                Invalid username or password
            </div>
        </c:if>

        <div class="input-group">
            <label>Username</label>
            <input type="text" name="username" required />
        </div>

        <div class="input-group">
            <label>Password</label>
            <input type="password" name="password" required />
        </div>

        <button type="submit">Login</button>

    </form>

    <div class="footer">
        Are you a new user?
        <a href="${pageContext.request.contextPath}/register">
            Click here to register
        </a>
    </div>

</div>

</body>
</html>