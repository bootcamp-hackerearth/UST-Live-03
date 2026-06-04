<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;
            --accent: #22c55e;

            --bg-dark: #0f172a;
            --bg-light: #f8fafc;
            --card: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;

            --border: rgba(255,255,255,0.2);
            --radius: 18px;

            --shadow: 0 30px 60px rgba(2,6,23,0.25);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            background: linear-gradient(135deg, #0f172a, #1e293b);
        }

        /* LEFT PANEL (Branding) */
        .left-panel {
            flex: 1;
            color: white;
            padding: 60px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .brand-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 16px;
        }

        .brand-sub {
            color: #cbd5f5;
            font-size: 15px;
            max-width: 420px;
            line-height: 1.6;
        }

        /* RIGHT PANEL */
        .right-panel {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #f8fafc, #eef2f7);
        }

        /* LOGIN CARD */
        .login-card {
            width: 420px;
            padding: 40px;
            border-radius: var(--radius);

            background: var(--card);
            backdrop-filter: blur(18px);

            border: 1px solid var(--border);
            box-shadow: var(--shadow);

            animation: fadeIn 0.6s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h2 {
            text-align: center;
            font-weight: 700;
            margin-bottom: 28px;
            color: var(--text);
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: var(--muted);
            margin-bottom: 6px;
        }

        .form-control {
            border-radius: 12px;
            padding: 12px 14px;
            font-size: 14px;

            border: 1px solid #e2e8f0;
            background: rgba(255,255,255,0.85);

            transition: all 0.2s ease;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
            background: #fff;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;

            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            font-weight: 600;

            transition: all 0.2s ease;
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.35);
        }

        .btn-register {
            display: block;
            text-align: center;
            width: 100%;
            padding: 12px;
            margin-top: 16px;

            border-radius: 12px;
            border: 1px solid #e2e8f0;

            font-weight: 600;
            text-decoration: none;
            color: var(--text);

            background: #f1f5f9;
            transition: all 0.2s ease;
        }

        .btn-register:hover {
            background: #e2e8f0;
            transform: translateY(-1px);
        }

        .divider {
            text-align: center;
            margin: 18px 0;
            font-size: 12px;
            color: var(--muted);
            position: relative;
        }

        .divider::before,
        .divider::after {
            content: "";
            position: absolute;
            top: 50%;
            width: 40%;
            height: 1px;
            background: #e2e8f0;
        }

        .divider::before { left: 0; }
        .divider::after { right: 0; }

        .alert {
            font-size: 13px;
            border-radius: 10px;
            padding: 10px;
        }

        /* Responsive */
        @media (max-width: 900px) {
            .left-panel {
                display: none;
            }
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

<!-- LEFT SIDE -->
<div class="left-panel">
    <div class="brand-title">POS System</div>
    <div class="brand-sub">
        Secure, scalable, and fast point-of-sale platform designed for modern retail operations.
        Manage transactions, inventory, and analytics seamlessly.
    </div>
</div>

<!-- RIGHT SIDE -->
<div class="right-panel">

    <div class="login-card">

        <h2>Welcome Back</h2>

        <c:if test="${not empty errorMsg}">
            <div class="alert alert-danger text-center mb-3">
                ${errorMsg}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login"
              method="post"
              onsubmit="return validateLoginForm()">

            <div class="mb-3">
                <label>Email</label>
                <input type="email" name="username" class="form-control" required />
            </div>

            <div class="mb-3">
                <label>Password</label>
                <input type="password" name="password" class="form-control" required />
            </div>

            <button type="submit" class="btn-login">
                Sign In
            </button>
        </form>

        <div class="divider">OR</div>

        <a href="${pageContext.request.contextPath}/register"
           class="btn-register">
            Create New Account
        </a>

    </div>

</div>

</body>
</html>