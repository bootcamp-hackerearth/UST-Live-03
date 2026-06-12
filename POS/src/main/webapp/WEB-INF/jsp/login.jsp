<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.login-card {
    width: 360px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.login-header {
    text-align: center;
    margin-bottom: 20px;
    color: #fff;
    font-weight: 600;
}

.form-label {
    font-size: 13px;
    color: #ccc;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;

    border-radius: 8px;
    border: none;
    outline: none;

    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control::placeholder {
    color: #aaa;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn-primary {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;

    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 12px;
    border-radius: 6px;
    font-size: 13px;
}

.alert-danger {
    color: #ff8080;
}

.alert-info {
    color: #00ffcc;
}

.card-footer {
    text-align: center;
    margin-top: 15px;
    font-size: 12px;
    color: #aaa;
}

.card-footer a {
    color: #00ffff;
    text-decoration: none;
}

.card-footer a:hover {
    text-decoration: underline;
}
</style>
</head>

<body>

<div class="login-card">

    <div class="login-header">
        POS System Login
    </div>

    <c:if test="${param.error == 'true'}">
        <div class="alert alert-danger">
            Invalid username or password
        </div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="alert alert-info">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">

        <label class="form-label">Email</label>
        <input type="text"
               name="username"
               class="form-control"
               placeholder="Enter your email"
               required>

        <label class="form-label">Password</label>
        <input type="password"
               name="password"
               class="form-control"
               placeholder="Enter your password"
               required>

        <button type="submit" class="btn-primary">
            Sign In
        </button>

    </form>

    <div class="card-footer">
        Don’t have an account?
        <a href="${pageContext.request.contextPath}/register">
            Register here
        </a>
    </div>

</div>

</body>
</html>