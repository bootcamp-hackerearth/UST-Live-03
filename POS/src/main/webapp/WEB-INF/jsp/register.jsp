<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS Retail Management | Register Account</title>

    <style>
        :root {
            --navy: #0B3C5D;
            --accent: #3282B8;
            --text-main: #1F2937;
            --panel-bg: #F8FAFC; /* Matches the login page panel contrast */
        }

        body, html {
            margin: 0;
            padding: 0;
            height: 100vh;
            width: 100vw;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: #F3F4F6;
            overflow: hidden; /* Prevents page-level scrolling */
        }

        .wrapper {
            display: flex;
            height: 100vh;
            width: 100%;
        }

        /* ===== Left Side: Brand Panel (35% Width) ===== */
        .brand-panel {
            flex: 0.35;
            background: var(--navy);
            background: linear-gradient(135deg, #0B3C5D 0%, #164E75 100%);
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 60px;
            color: white;
        }

        .brand-panel h1 {
            font-size: 38px;
            font-weight: 800;
            margin: 0 0 20px 0;
            line-height: 1.2;
        }

        .brand-panel p {
            font-size: 16px;
            line-height: 1.6;
            opacity: 0.9;
            max-width: 320px;
        }

        .feature-tag {
            margin-top: 30px;
            display: inline-block;
            padding: 6px 12px;
            background: rgba(255,255,255,0.1);
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
        }

        .register-panel {
            flex: 0.65;
            background: var(--panel-bg);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .login-card {
            width: 100%;
            max-width: 600px; /* Wider for the 2-column illusion */
            background: white;
            padding: 45px;
            border-radius: 20px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05);
            border: 1px solid rgba(0,0,0,0.04);
        }

        .login-card h2 {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-main);
            margin: 0 0 8px 0;
        }

        .subtitle {
            color: #6B7280;
            font-size: 15px;
            margin-bottom: 30px;
        }

        /* Form Grid to prevent scrolling */
        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px 25px;
        }

        .form-group {
            margin-bottom: 5px;
        }

        .full-row {
            grid-column: span 2;
        }

        .form-group label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: #374151;
            margin-bottom: 8px;
        }

        .form-group input, .form-group select {
            width: 100%;
            padding: 12px;
            border: 1.5px solid #E5E7EB;
            border-radius: 10px;
            font-size: 14px;
            box-sizing: border-box;
            transition: all 0.2s;
        }

        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: var(--navy);
            box-shadow: 0 0 0 4px rgba(11, 60, 93, 0.08);
        }

        .form-group select[multiple] {
            height: 75px;
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
            margin-top: 15px;
        }

        .btn-login:hover {
            background: #082d47;
        }

        .btn-login:active {
            transform: scale(0.98);
        }

        .error-box {
            background: #FEF2F2;
            color: #991B1B;
            padding: 10px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 20px;
            border: 1px solid #FEE2E2;
            text-align: center;
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

        @media (max-width: 900px) {
            .brand-panel { display: none; }
            .form-grid { grid-template-columns: 1fr; }
            .register-panel { flex: 1; background: white; }
            .login-card { box-shadow: none; border: none; padding: 20px; }
            body { overflow: auto; }
        }
    </style>
</head>
<body>

    <div class="wrapper">
        <section class="brand-panel">
            <h1>Join the <br>Network.</h1>
            <p>Ready to scale? Set up your workspace account in just a few minutes.</p>

            <div class="feature-tag">UST JAVA POD-1 &bull; 2026</div>

            <div style="margin-top: auto; font-size: 12px; opacity: 0.6;">
                Security verified by UST Security &copy;
            </div>
        </section>

        <main class="register-panel">
            <div class="login-card">
                <h2>Registration</h2>
                <p class="subtitle">Complete the fields below to create your profile.</p>

                <c:if test="${not empty message}">
                    <div class="error-box">
                        ${message}
                    </div>
                </c:if>

                <form:form action="register" method="post" modelAttribute="userDto">
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Full Name</label>
                            <form:input path="name" required="required" placeholder="Kushal" />
                        </div>

                        <div class="form-group">
                            <label>Corporate Email</label>
                            <form:input path="username" type="email" required="required" placeholder="user@ust.com" />
                        </div>

                        <div class="form-group">
                            <label>Phone Number</label>
                            <form:input path="phoneNo" required="required" pattern="[0-9]{10}" placeholder="10-digit mobile" />
                        </div>

                        <div class="form-group">
                            <label>Password</label>
                            <form:password path="password" required="required" placeholder="••••••••" />
                        </div>

                        <div class="form-group full-row">
                            <label>Assigned Roles (Multi-select)</label>
                            <form:select path="roles" multiple="true" required="required">
                                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                            </form:select>
                        </div>
                    </div>

                    <button type="submit" class="btn-login">Create Account</button>
                </form:form>

                <p class="register-link">
                    Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a>
                </p>
            </div>
        </main>
    </div>

</body>
</html>