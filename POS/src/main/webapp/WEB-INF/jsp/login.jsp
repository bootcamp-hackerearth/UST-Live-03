<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>POS System</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            color: #1e293b;
        }

        .main {
            display: flex;
            height: 100vh;
        }

        .left {
            flex: 1;
            padding: 80px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            background: linear-gradient(135deg, #e2e8f0, #f8fafc);
        }

        .left h1 {
            font-size: 56px;
            font-weight: 800;
            line-height: 1.1;
            margin-bottom: 20px;
        }

        .left p {
            font-size: 18px;
            color: #475569;
            max-width: 520px;
        }

        .right {
            width: 460px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .form-box {
            width: 100%;
            background: #ffffff;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        .form-box h2 {
            text-align: center;
            margin-bottom: 26px;
            font-size: 22px;
            font-weight: 700;
        }

        input {
            width: 100%;
            padding: 12px;
            margin-bottom: 16px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .error-msg {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #fee2e2;
            color: #b91c1c;
            font-size: 14px;
            text-align: center;
            border: 1px solid #fca5a5;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            background-color: #1e293b;
            color: #ffffff;
            cursor: pointer;
        }

        .btn-login:hover {
            background-color: #0f172a;
        }

        .extra-text {
            text-align: center;
            margin-top: 18px;
            font-size: 14px;
            color: #64748b;
        }

        .extra-text a {
            color: #1e293b;
            font-weight: 600;
            text-decoration: none;
        }

        .extra-text a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="main">

    <div class="left">
        <h1>POS MADE SIMPLE</h1>
        <p>Modern retail application</p>
    </div>

    <div class="right">
        <div class="form-box">

            <h2>Login to your account</h2>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="error-msg">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <c:if test="${not empty param.logout}">
                <div class="error-msg" style="background:#dcfce7;color:#166534;border-color:#86efac;">
                    Logged out successfully
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">

                <input type="text" name="username" placeholder="Enter username" required>

                <input type="password" name="password" placeholder="Enter password" required>

                <button type="submit" class="btn-login">
                    Sign In
                </button>

            </form>

            <div class="extra-text">
                Do not have an account?
                <a href="${pageContext.request.contextPath}/register">
                    Register here
                </a>
            </div>

        </div>
    </div>

</div>

</body>
</html>