<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>POS Login</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Segoe UI", sans-serif;
        }

        body {
            height: 100vh;
            display: flex;
        }

        .left {
            width: 50%;
            background: linear-gradient(135deg, #1f1f1f, #3a2e2a);
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 80px;
            position: relative;
        }

        .left small {
            opacity: 0.7;
            margin-bottom: 20px;
        }

        .left h1 {
            font-size: 56px;
            line-height: 1.1;
            font-weight: 600;
        }

        .circle {
            position: absolute;
            width: 400px;
            height: 400px;
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 50%;
            top: 50%;
            left: 40%;
            transform: translate(-50%, -50%);
        }

        .right {
            width: 50%;
            background: #f7f7f7;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 60px;
        }

        .form-container {
            max-width: 380px;
            width: 100%;
        }

        .form-container h2 {
            font-size: 36px;
            margin-bottom: 30px;
            color: #222;
        }

        .input-box {
            width: 100%;
            padding: 14px;
            border-radius: 30px;
            border: 1px solid #ddd;
            margin-bottom: 15px;
            font-size: 14px;
            background: #fff;
        }

        .input-box:focus {
            outline: none;
            border-color: #ff4800;
        }

        .login-btn {
            width: 100%;
            padding: 14px;
            border-radius: 30px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 15px;
            cursor: pointer;
        }

        .register-btn {
            margin-top: 15px;
            width: 100%;
            padding: 12px;
            border-radius: 30px;
            border: 1px solid #ccc;
            background: transparent;
            cursor: pointer;
        }

        .message-box {
            margin-top: 10px;
            font-size: 13px;
            color: red;
            text-align: center;
        }
    </style>

</head>

<body>

<div class="left">
    <small>Smart POS system – manage billing & sales easily</small>
    <h1>Manage<br>your POS</h1>
    <div class="circle"></div>
</div>

<div class="right">
    <div class="form-container">
        <h2>Sign In</h2>

        <form action="login" method="post">

            <input class="input-box" type="email" name="username" placeholder="Email" required>

            <input class="input-box" type="password" name="password" placeholder="Password" required="ture">

            <button type="submit" class="login-btn">Sign In</button>

            <button type="button" class="register-btn" onclick="window.location.href='register'">
                Register
            </button>

            <c:if test="${not empty message}">
                <div class="message-box">${message}</div>
            </c:if>
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