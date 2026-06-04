<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, sans-serif;
            background-color: #f6f7f9;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .page-wrapper {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .login-card {
            width: 350px;
            background-color: #fff;
            box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;
            border-radius: 10px;
            box-sizing: border-box;
            padding: 20px 30px;
        }

        h2 {
            text-align: center;
            margin: 10px 0 30px 0;
            font-size: 25px;
            font-weight: 500;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
            margin-bottom: 15px;
        }

        label {
            font-size: 11px;
            font-weight: 700;
            color: #747474;
        }

    .app-title {
        text-align: center;
        font-size: 14px;
        color: #6b7280; /* soft gray */
        margin-bottom: -5px;
        letter-spacing: 0.5px;
    }

        input {
            border-radius: 25px;
            border: 1px solid #c0c0c0;
            padding: 12px 15px;
            outline: none;
        }

        .login-btn {
            padding: 12px;
            border-radius: 25px;
            border: none;
            background: teal;
            color: white;
            cursor: pointer;
            width: 100%;
            font-weight: 600;
            box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
        }

        .error-message {
            margin-top: 14px;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            background-color: #fee2e2;
            color: #991b1b;
            font-size: 13px;
        }

        .logout-message {
            margin-top: 14px;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            background-color: #e6fffa;
            color: #065f46;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="page-wrapper">

    <div class="login-card">
    <div class="app-title">POS Application</div>

        <h2>Login</h2>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label>Email</label>
                <input type="email" name="username" required />
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required />
            </div>

            <button type="submit" class="login-btn">Login</button>
        </form>

        <div style="text-align:center; margin-top: 14px; font-size: 13px; color: #555;">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/register"
               style="color: teal; font-weight: 600; text-decoration: none;">
                Register
            </a>
        </div>

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

</div>

</body>
</html>