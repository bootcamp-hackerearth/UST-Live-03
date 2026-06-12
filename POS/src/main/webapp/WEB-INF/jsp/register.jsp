<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register</title>

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
        }

        .left small {
            opacity: 0.7;
            margin-bottom: 20px;
        }

        .left h1 {
            font-size: 52px;
            line-height: 1.1;
            font-weight: 600;
        }

        .right {
            width: 50%;
            background: #f7f7f7;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px;
        }

        .form-container {
            width: 100%;
            max-width: 420px;
        }

        .form-container h2 {
            font-size: 34px;
            margin-bottom: 25px;
            color: #222;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            font-size: 13px;
            color: #555;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 14px;
            border-radius: 30px;
            border: 1px solid #ddd;
            font-size: 14px;
            background: #fff;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #ff4800;
        }

        select[multiple] {
            height: 120px;
            border-radius: 12px;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            border-radius: 30px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 15px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            border-radius: 30px;
            border: 1px solid #ccc;
            background: transparent;
            color: #333;
            font-size: 14px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-login:hover {
            background: #eee;
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
    <small>Smart POS system – create your account</small>
    <h1>Join<br>POS System</h1>
</div>

<div class="right">
    <div class="form-container">
        <h2>Register</h2>

        <form:form action="register" method="post" modelAttribute="userDto">

            <div class="form-group">
                <label>Name</label>
                <form:input path="name" required="true"/>
            </div>

            <div class="form-group">
                <label>Email</label>
                <form:input path="username" type="email" required="true"/>
            </div>

            <div class="form-group">
                <label>Roles</label>
                <form:select path="roles" multiple="true" required="true">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <form:input path="phoneNo" required="true" type="tel" pattern="[0-9]{10}"/>
            </div>

            <div class="form-group">
                <label>Password</label>
                <form:password path="password" required="true" pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}"/>
            </div>

            <input type="submit" value="Register" class="btn-submit"/>

            <button type="button" class="btn-login" onclick="window.location.href='login'">
                Login
            </button>

            <c:if test="${not empty message}">
                <div class="message-box">${message}</div>
            </c:if>

        </form:form>
    </div>
</div>

</body>
</html>