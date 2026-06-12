<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS Management | Enterprise Login</title>

    <style>
        :root {
            --navy: #0B3C5D;
            --accent: #3282B8;
            --text-main: #1F2937;
            /* Added a subtle background color for the panel to create the box illusion */
            --panel-bg: #F8FAFC;
        }

        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: #F3F4F6;
        }

        .wrapper {
            display: flex;
            height: 100vh;
            width: 100%;
        }

        /* ===== Left Side: Brand Panel ===== */
        .brand-panel {
            flex: 1;
            background: var(--navy);
            background: linear-gradient(135deg, #0B3C5D 0%, #164E75 100%);
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 80px;
            color: white;
        }

        .brand-panel h1 {
            font-size: 42px;
            font-weight: 800;
            margin: 0 0 20px 0;
            line-height: 1.2;
        }

        .brand-panel p {
            font-size: 18px;
            line-height: 1.6;
            opacity: 0.9;
            max-width: 400px;
        }

        .feature-list {
            margin-top: 40px;
            list-style: none;
            padding: 0;
        }

        .feature-list li {
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 15px;
            font-weight: 500;
        }

        /* ===== Right Side: Form Panel (The Box Illusion) ===== */
        .login-panel {
            flex: 0.8;
            background: var(--panel-bg); /* Changed from white to subtle gray */
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .login-card {
            width: 100%;
            max-width: 400px; /* Adjusted slightly for the box feel */
            background: white;
            padding: 50px;
            border-radius: 20px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05); /* The "Illusion" Shadow */
            border: 1px solid rgba(0,0,0,0.04);
        }

        .login-card h2 {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 10px;
        }

        .subtitle {
            color: #6B7280;
            font-size: 15px;
            margin-bottom: 35px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: #374151;
            margin-bottom: 8px;
        }

        .form-group input {
            width: 100%;
            padding: 14px;
            border: 1.5px solid #E5E7EB;
            border-radius: 10px;
            font-size: 15px;
            box-sizing: border-box;
            transition: all 0.2s;
        }

        .form-group input:focus {
            outline: none;
            border-color: var(--navy);
            box-shadow: 0 0 0 4px rgba(11, 60, 93, 0.08);
        }

        .btn-login {
            width: 100%;
            padding: 14px;
            background: var(--navy);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s, transform 0.1s;
            margin-top: 10px;
        }

        .btn-login:hover {
            background: #082d47;
        }

        .btn-login:active {
            transform: scale(0.98);
        }

        /* Alerts & Toasts */
        .error-box {
            background: #FEF2F2;
            color: #991B1B;
            padding: 12px;
            border-radius: 8px;
            font-size: 14px;
            margin-bottom: 25px;
            border: 1px solid #FEE2E2;
            font-weight: 500;
        }

        .register-link {
            margin-top: 25px;
            text-align: center;
            font-size: 14px;
            color: #6B7280;
        }

        .register-link a {
            color: var(--navy);
            text-decoration: none;
            font-weight: 700;
        }

        /* Mobile Viewport Fix */
        @media (max-width: 850px) {
            .brand-panel { display: none; }
            .login-panel { flex: 1; background: white; }
            .login-card { box-shadow: none; border: none; padding: 20px; }
        }
    </style>
</head>
<body>

    <div class="wrapper">
        <!-- Brand Visual Section -->
        <section class="brand-panel">
            <h1>POS <br>Management.</h1>
            <p>The centralized hub for your business operations. Smart, reliable, and secure.</p>

            <ul class="feature-list">
                <li>✅ Real-time Analytics</li>
                <li>✅ Secure Cloud Storage</li>
                <li>✅ Inventory Management</li>
            </ul>

            <div style="margin-top: auto; font-size: 12px; opacity: 0.6;">
                Developed by UST Global Java Pod-1 &copy; 2026
            </div>
        </section>

        <!-- Login Interaction Section -->
        <main class="login-panel">
            <!-- Content wrapped in the "Illusive Box" card -->
            <div class="login-card">
                <h2>Account Sign-in</h2>
                <p class="subtitle">Access your management console</p>

                <c:if test="${param.error != null}">
                    <div class="error-box">
                        Email or password is incorrect.
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="form-group">
                        <label>Email Address</label>
                        <input type="text" name="username" placeholder="eg. admin@ust.com" required />
                    </div>

                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" name="password" placeholder="••••••••" required />
                    </div>

                    <button type="submit" class="btn-login">Sign In</button>
                </form>

                <p class="register-link">
                    Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one</a>
                </p>
            </div>
        </main>
    </div>

</body>
</html>