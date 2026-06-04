<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: 'Poppins', Arial, sans-serif;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .login-card {
            width: 360px;
            background: #ffffff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .error-message {
            margin-bottom: 15px;
            padding: 10px;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 500;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        button {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            transform: scale(1.03);
        }

        .register-link {
            display: block;
            margin-top: 15px;
            text-align: center;
            font-size: 14px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 500;
        }

        .register-link:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="login-card">

    <h2>Login</h2>


    <c:if test="${param.error != null}">
        <div class="error-message">
            Invalid username or password
        </div>
    </c:if>


    <c:if test="${param.logout != null}">
        <div class="error-message" style="color:#155724; border-color:#28a745;">
            Logged out successfully
        </div>
    </c:if>

    <form action="/login" method="post">

        <div class="form-group">
            <label>Username</label>
            <input type="text" name="username" required />
        </div>

        <div class="form-group">
            <label>Password</label>
            <input type="password" name="password" required />
        </div>

        <button type="submit">Login</button>

    </form>

    <a href="/register" class="register-link">
        Don't have an account? Register
    </a>

</div>

</body>
</html>