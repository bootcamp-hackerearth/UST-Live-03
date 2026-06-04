<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login - POS System</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-wrapper {
            display: flex;
            width: 780px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 3px 15px rgba(0,0,0,0.1);
            border: 1px solid #ddd;
            overflow: hidden;
        }

        .left-panel {
            width: 320px;
            background-color: #007bff;
            color: white;
            padding: 50px 35px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .left-panel h1 {
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .left-panel p {
            font-size: 13px;
            opacity: 0.85;
            line-height: 1.7;
        }

        .left-panel .divider {
            width: 40px;
            height: 3px;
            background: white;
            opacity: 0.5;
            margin: 16px 0;
            border-radius: 2px;
        }

        .left-panel .feature {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 14px;
            font-size: 13px;
            opacity: 0.9;
        }

        .left-panel .feature span {
            font-size: 16px;
        }

        .right-panel {
            flex: 1;
            padding: 50px 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .right-panel h2 {
            font-size: 20px;
            margin-bottom: 6px;
            color: #333;
        }

        .right-panel .sub {
            font-size: 13px;
            color: #888;
            margin-bottom: 28px;
        }

        .error-msg {
            margin-bottom: 16px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
            font-size: 13px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: bold;
            margin-bottom: 6px;
            color: #444;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 13px;
            margin-bottom: 18px;
        }

        input:focus {
            outline: none;
            border-color: #007bff;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
        }

        .btn-login:hover {
            background-color: #0056b3;
        }

        .register-link {
            margin-top: 16px;
            text-align: center;
            font-size: 13px;
            color: #888;
        }

        .register-link a {
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }

        .register-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="login-wrapper">

    <div class="left-panel">
        <h1>POS System</h1>
        <div class="divider"></div>
        <p>Point of Sale Management for your business operations.</p>

        <div class="feature">
            <span>📦</span> Product Management
        </div>
        <div class="feature">
            <span>🏷️</span> Category & Brand Control
        </div>
        <div class="feature">
            <span>👥</span> User & Role Management
        </div>
        <div class="feature">
            <span>🏪</span> Shelf & Rack Tracking
        </div>
    </div>

    <div class="right-panel">

        <h2>Welcome Back</h2>
        <div class="sub">Sign in to your account to continue</div>

        <c:if test="${param.error != null}">
            <div class="error-msg">Invalid username or password</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <label>Username</label>
            <input type="text" name="username" required/>

            <label>Password</label>
            <input type="password" name="password" required/>

            <button type="submit" class="btn-login">Login</button>

        </form>

        <div class="register-link">
            New user?
            <a href="${pageContext.request.contextPath}/register">Register here</a>
        </div>

    </div>

</div>

</body>
</html>