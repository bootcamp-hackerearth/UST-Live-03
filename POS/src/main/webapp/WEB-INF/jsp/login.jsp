<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #eef2ff, #e0e7ff);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        form {
            background: #ffffff;
            padding: 28px 30px;
            width: 320px;
            border-radius: 12px;
            box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #374151;
        }

        .error {
            color: red;
            text-align: center;
            margin-bottom: 12px;
            font-size: 13px;
        }

        .success {
            color: green;
            text-align: center;
            margin-bottom: 12px;
            font-size: 13px;
        }

        form div {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-size: 13px;
            color: #475569;
        }

        input {
            width: 100%;
            padding: 10px;
            font-size: 14px;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            transition: 0.2s;
        }

        input:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
        }

        button {
            display: block;
            width: 100%;
            margin-top: 15px;
            padding: 11px;
            background: linear-gradient(135deg, #6366f1, #4338ca);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 15px rgba(99,102,241,0.25);
        }

        .register-link {
            margin-top: 16px;
            text-align: center;
            font-size: 13px;
        }

        .register-link a {
            color: #6366f1;
            text-decoration: none;
            font-weight: 600;
        }

        .register-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<form action="${pageContext.request.contextPath}/login" method="post">
    <h2>Login</h2>

    <c:if test="${param.error != null}">
        <div class="error">Invalid username or password</div>
    </c:if>

    <c:if test="${param.logout != null}">
        <div class="success">You have been logged out successfully</div>
    </c:if>

    <div>
        <label>Email</label>
        <input type="text" name="username" required />
    </div>

    <div>
        <label>Password</label>
        <input type="password" name="password" required />
    </div>

    <button type="submit">Login</button>

    <div class="register-link">
        New user?
        <a href="${pageContext.request.contextPath}/register">Register here</a>
    </div>
</form>

</body>
</html>