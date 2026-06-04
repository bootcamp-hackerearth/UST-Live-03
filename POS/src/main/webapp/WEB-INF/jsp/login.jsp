<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Login</title>

    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-card {
            background: #FFFFFF;
            width: 100%;
            max-width: 400px;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            text-align: center;
            position: relative;
        }

        .brand-header {
            background: #0B3C5D;
            margin: -40px -40px 30px -40px;
            padding: 25px;
            border-radius: 16px 16px 0 0;
            color: #FFFFFF;
        }

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        h2 {
            color: #1F2937;
            font-size: 18px;
            margin-bottom: 8px;
        }

        .subtitle {
            color: #6B7280;
            font-size: 14px;
            margin-bottom: 25px;
        }

        form {
            text-align: left;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: #4B5563;
            margin-bottom: 6px;
        }

        input {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border: 1px solid #E5E7EB;
            border-radius: 8px;
            box-sizing: border-box;
            font-size: 14px;
            transition: border-color 0.2s;
        }

        input:focus {
            outline: none;
            border-color: #0B3C5D;
            background: #F9FAFB;
        }

        button {
            width: 100%;
            padding: 12px;
            background: #0B3C5D;
            color: #FFFFFF;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: opacity 0.2s;
            margin-top: 5px;
        }

        button:hover {
            opacity: 0.9;
        }

        .register-link {
            margin-top: 20px;
            font-size: 14px;
            color: #6B7280;
        }

        .register-link a {
            color: #0B3C5D;
            text-decoration: none;
            font-weight: 600;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        .error-message {
            background: #FEE2E2;
            color: #B91C1C;
            padding: 10px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 20px;
            border: 1px solid #FECACA;
            font-weight: 500;
        }

        .toast {
            position: fixed;
            top: 24px;
            right: 24px;
            min-width: 280px;
            padding: 16px 20px;
            background: #16A34A; /* Success Green */
            color: #FFFFFF;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            z-index: 9999;
            animation: slideIn 0.3s ease-out;
        }

        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        .footer {
            margin-top: 30px;
            font-size: 12px;
            color: #9CA3AF;
        }
    </style>
</head>

<body>
    <c:if test="${not empty successMessage}">
        <div class="toast">
            ${successMessage}
        </div>
    </c:if>

    <div class="login-card">
        <div class="brand-header">
            <h1>POS Retail Management</h1>
        </div>

        <h2>User Login</h2>
        <p class="subtitle">Please enter your credentials.</p>

        <c:if test="${param.error != null}">
            <div class="error-message">
                Invalid email or password.
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <label>Email Address</label>
            <input type="text" name="username" placeholder="eg. user@ust.com" required />

            <label>Password</label>
            <input type="password" name="password" placeholder="eg. #$@!*&" required />

            <button type="submit">Login</button>
        </form>

        <div class="register-link">
            New user? <a href="${pageContext.request.contextPath}/register">Create an account</a>
        </div>

        <div class="footer">
            Developed by <strong>UST Global</strong> Java Pod-1
        </div>
    </div>
</body>
</html>