<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Login</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #111;
            padding: 20px;
        }

        .login-card {
            width: 400px;
            background: #fff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 15px 40px rgba(255, 255, 255, 0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #111;
            font-size: 28px;
            font-weight: 600;
        }

        .error {
            background: #ffe5e5;
            color: #d8000c;
            padding: 10px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 18px;
            text-align: center;
        }

        .success {
            background: #e7ffe7;
            color: #1a7f37;
            padding: 10px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 18px;
            text-align: center;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-size: 14px;
            font-weight: 500;
            color: #111;
        }

        input {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #ccc;
            border-radius: 10px;
            font-size: 14px;
            outline: none;
            transition: 0.3s ease;
            background: #f8f8f8;
        }

        input:focus {
            border-color: #000;
            background: #fff;
        }

        button {
            width: 100%;
            padding: 13px;
            margin-top: 10px;
            border: none;
            border-radius: 10px;
            background: #111;
            color: #fff;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: 0.3s ease;
        }

        button:hover {
            background: #333;
        }

        .register-link {
            margin-top: 20px;
            text-align: center;
            font-size: 14px;
            color: #555;
        }

        .register-link a {
            color: #111;
            text-decoration: none;
            font-weight: 600;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        @media (max-width: 500px) {
            .login-card {
                width: 100%;
                padding: 28px 22px;
            }
        }

    </style>

</head>

<body>

    <div class="login-card">

        <form action="${pageContext.request.contextPath}/login"
              method="post">

            <h2>Login</h2>

            <c:if test="${param.error != null}">
                <div class="error">
                    Invalid username or password
                </div>
            </c:if>

            <c:if test="${param.logout != null}">
                <div class="success">
                    You have been logged out successfully
                </div>
            </c:if>

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}" />

            <div class="form-group">
                <label>Email</label>
                <input type="text"
                       name="username"
                       placeholder="Enter email"
                       required />
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password"
                       name="password"
                       placeholder="Enter password"
                       required />
            </div>

            <button type="submit">
                Login
            </button>

            <div class="register-link">
                New user?
                <a href="${pageContext.request.contextPath}/register">
                    Register here
                </a>
            </div>

        </form>

    </div>

</body>

</html>
