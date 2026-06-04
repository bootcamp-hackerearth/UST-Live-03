<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Retail POS | Secure Login</title>

<style>
    :root {
        --primary: #111827;
        --primary-dark: #000000;
        --bg-canvas: #f9fafb;
        --card-bg: #ffffff;
        --text-main: #111827;
        --text-soft: #6b7280;
        --border: #e5e7eb;
        --error: #dc2626;
    }

    body {
        font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
        margin: 0;
        height: 100vh;

        display: flex;
        justify-content: center;
        align-items: center;

        /* ✅ Matched background */
        background: linear-gradient(
            135deg,
            #e5e7eb 0%,
            #f9fafb 50%,
            #d1d5db 100%
        );
    }

    /* Error Toast */
    .error-toast {
        position: fixed;
        top: 20px;
        background-color: #ffffff;
        color: var(--error);
        padding: 12px 24px;
        border-radius: 50px;
        box-shadow: 0 10px 15px -3px rgba(0,0,0,0.15);
        font-size: 14px;
        font-weight: 600;
        display: flex;
        align-items: center;
        border: 1px solid #fee2e2;
        animation: fadeInDown 0.5s ease;
        z-index: 1000;
    }

    @keyframes fadeInDown {
        from { transform: translateY(-20px); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
    }

    /* Card */
    .container {
        width: 100%;
        max-width: 440px;
        background: var(--card-bg);
        padding: 48px;
        border-radius: 16px;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.15);
        position: relative;
        overflow: hidden;
    }

    /* Side accent bar */
    .container::before {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 6px;
        background: var(--primary);
    }

    .header {
        margin-bottom: 32px;
    }

    .header h2 {
        margin: 0;
        color: var(--text-main);
        font-size: 28px;
        font-weight: 800;
        letter-spacing: -0.025em;
    }

    .header p {
        color: var(--text-soft);
        font-size: 15px;
        margin-top: 8px;
    }

    /* Form */
    .form-group {
        margin-bottom: 20px;
    }

    label {
        display: block;
        font-weight: 500;
        margin-bottom: 8px;
        font-size: 14px;
        color: var(--text-main);
    }

    .input-field {
        width: 100%;
        padding: 12px 16px;
        border: 1px solid var(--border);
        border-radius: 8px;
        font-size: 15px;
        color: var(--text-main);
    }

    .input-field:focus {
        outline: none;
        border-color: var(--primary);
        box-shadow: 0 0 0 3px rgba(0,0,0,0.1);
    }

    .btn-login {
        width: 100%;
        padding: 14px;
        background-color: var(--primary);
        color: #ffffff;
        border: none;
        border-radius: 8px;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: background-color 0.2s ease, transform 0.1s;
        margin-top: 10px;
    }

    .btn-login:hover {
        background-color: var(--primary-dark);
    }

    .btn-login:active {
        transform: scale(0.98);
    }

    .footer-action {
        margin-top: 32px;
        text-align: center;
        font-size: 14px;
        color: var(--text-soft);
    }

    .btn-register {
        color: var(--text-main);
        text-decoration: none;
        font-weight: 600;
        margin-left: 4px;
    }

    .btn-register:hover {
        text-decoration: underline;
    }

    .system-tag {
        position: absolute;
        bottom: 24px;
        font-size: 12px;
        color: #9ca3af;
        font-weight: 500;
        letter-spacing: 0.05em;
    }
</style>
</head>

<body>

<c:if test="${param.error != null}">
    <div class="error-toast">
        <span style="margin-right:12px; font-size: 18px;">✕</span>
        Invalid credentials. Please try again.
    </div>
</c:if>

<div class="container">
    <div class="header">
        <h2>Welcome</h2>
        <p>Enter your details to access the POS dashboard.</p>
    </div>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label>E-mail Address</label>
            <input type="email" name="username" class="input-field"
                   placeholder="name@company.com" required autofocus>
        </div>

        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" class="input-field"
                   placeholder="••••••••" required>
        </div>

        <button type="submit" class="btn-login">Sign In</button>
    </form>

    <div class="footer-action">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register" class="btn-register">
            Register
        </a>
    </div>
</div>

<div class="system-tag">RETAIL ENGINE v2.0</div>

</body>
</html>