<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>
        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-card {
            background: #f3efe9;
            width: 360px;
            padding: 35px 30px;
            border-radius: 4px;
            text-align: left;
        }

        .app-title {
            font-size: 12px;
            font-weight: 700;
            color: #2f2f2f;
            letter-spacing: 1px;
            margin-bottom: 6px;
        }

        h2 {
            margin: 0 0 25px;
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            font-size: 10px;
            letter-spacing: 1px;
            color: #7a7a7a;
            display: block;
            margin-bottom: 6px;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 0;
            border: none;
            border-bottom: 2px solid #cfcfcf;
            background: #f3efe9; /* SAME CREAM COLOR */
            font-size: 14px;
            outline: none;
            color: #2f2f2f;
        }

        input:focus {
            border-bottom: 2px solid #3f3f3f;
            background: #f3efe9; /* KEEP CREAM ON FOCUS */
        }

        /* REMOVE WHITE AUTOFILL BG */
        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus,
        input:-webkit-autofill:active {
            -webkit-box-shadow: 0 0 0 30px #f3efe9 inset !important;
            -webkit-text-fill-color: #2f2f2f !important;
        }

        .login-btn {
            width: 100%;
            box-sizing: border-box;
            padding: 12px;
            margin-top: 15px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-weight: 700;
            letter-spacing: 1px;
            cursor: pointer;
            transition: 0.3s;
        }

        .login-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        /* DIVIDER */
        .divider {
            margin: 18px 0;
            text-align: center;
            color: #8a8a8a;
            font-size: 11px;
        }

        .register-btn {
            width: 100%;
            box-sizing: border-box;
            padding: 12px;
            background: transparent;
            border: 2px solid #3f3f3f;
            color: #3f3f3f;
            font-weight: 700;
            cursor: pointer;
            transition: 0.3s;
        }

        .register-btn:hover {
            background: #3f3f3f;
            color: #ffffff;
        }

        .error-message {
            margin-top: 12px;
            padding: 8px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 12px;
        }

        .logout-message {
            margin-top: 12px;
            padding: 8px;
            background: #e6fffa;
            color: #065f46;
            font-size: 12px;
        }
    </style>
</head>

<body>

<div class="login-card">

    <div class="app-title">POS APPLICATION</div>
    <h2>Sign in</h2>

    <form action="${pageContext.request.contextPath}/login" method="post">

        <div class="form-group">
            <label>EMAIL</label>
            <input type="email" name="username" required />
        </div>

        <div class="form-group">
            <label>PASSWORD</label>
            <input type="password" name="password" required />
        </div>

        <button type="submit" class="login-btn">LOGIN</button>
    </form>

    <div class="divider">or</div>

    <form action="${pageContext.request.contextPath}/register" method="get">
        <button type="submit" class="register-btn">REGISTER</button>
    </form>

    <c:if test="${param.error != null}">
        <div class="error-message">
            Invalid username or password
        </div>
    </c:if>

    <c:if test="${param.logout != null}">
        <div class="logout-message">
            Logged out successfully
        </div>
    </c:if>

</div>

</body>
</html>