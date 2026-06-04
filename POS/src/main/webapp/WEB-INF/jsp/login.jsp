<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS System - Login</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            height: 100vh;
            display: flex;
        }

        .left {
            flex: 1;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .left h1 {
            font-size: 36px;
            margin-bottom: 10px;
        }

        .left p {
            font-size: 16px;
            opacity: 0.8;
        }

        .right {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #f5f6fa;
        }

        .form-container {
            width: 320px;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        button {
            width: 100%;
            padding: 10px;
            background: #667eea;
            border: none;
            color: white;
            border-radius: 6px;
            cursor: pointer;
        }

        .toggle {
            text-align: center;
            margin-top: 15px;
            font-size: 14px;
        }

        .toggle a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="left">
    <h1>POS System</h1>
    <p>Manage users, roles & access securely</p>
</div>

<div class="right">
    <div class="form-container">

        <form action="${pageContext.request.contextPath}/login" method="post">
            <h2>Login</h2>

            <input type="email" name="username" placeholder="Username" required />
            <input type="password" name="password" placeholder="Password" required />

            <button type="submit">Login</button>

            <div class="toggle">
                New user?
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </div>
            <c:if test="${param.error == 'true'}">
                <div style="color:red; margin-bottom:15px; text-align:center; font-size:13px;">
                    Invalid username or password
                </div>
            </c:if>
        </form>

    </div>
</div>

</body>
</html>
