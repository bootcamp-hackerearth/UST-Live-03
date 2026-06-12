<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: Arial, sans-serif;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            background: #ffffff;
            padding: 30px 35px;
            width: 320px;
            border-radius: 10px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            color: #555;
        }

        input {
            width: 100%;
            padding: 10px;
            font-size: 14px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 15px;
            cursor: pointer;
        }

        button:hover {
            background-color: #5a67d8;
        }

        .error-message {
            background-color: #f8d7da;
            color: #842029;
            padding: 8px;
            border-radius: 6px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 15px;
        }
    </style>
</head>

<body>

<div class="card">

    <!-- ✅ LOGIN FORM -->
    <form action="${pageContext.request.contextPath}/login" method="post">

        <h2>Login</h2>

        <c:if test="${param.error != null}">
            <div class="error-message">
                Invalid username or password
            </div>
        </c:if>

        <div class="form-group">
            <label>Username:</label>
            <input type="text" name="username" required />
        </div>

        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password" required />
        </div>

        <button type="submit">Login</button>
    </form>

    <!-- ✅ SIGNUP FORM (BELOW LOGIN BUTTON) -->
    <form action="${pageContext.request.contextPath}/register" method="get">
        <button type="submit" style="margin-top:10px;">
            Sign Up
        </button>
    </form>

</div>

</body>
</html>
