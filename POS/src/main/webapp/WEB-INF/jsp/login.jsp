<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Login</title>

 <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        html,body{
            height:100%;
        }
        body {
            margin: 0;
            height: 100vh;
            font-family: Arial, sans-serif;
            background-color:#edebeb;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        #container{
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height:100%;
        }
        #leftDiv{
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            text-wrap: auto;
            background-color: #5b68a0;
            color:white;
            border-right: solid 10px white;
        }
        #rightDiv{
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #d5dbf3;
        }

        form {
            background: #ffffffc2;
            padding: 30px 35px;
            width: 320px;
            border-radius: 10px;
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #667eea;
        }

        form div {
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

        input:focus {
            outline: none;
            border-color: #667eea;
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
            margin-bottom: 10px;

        }

        button:hover {
            background-color: #5a67d8;
        }

        form p {
            padding:5px;
            color:red;
        }

        form a {
            text-decoration: none;
            color: #667eea;
            padding: 5px;
            border-radius: 6px;
            margin-top: 15px;
            font-size: 13px;
        }
        form p{
        color:green;
        font-size:12px;
        }
    </style>
</head>

<body>
<div id="container">
    <div id="leftDiv">
        <h1>Point of Sales Application</h1>
    </div>
    <div id="rightDiv">
        <form th:action="@{/login}" method="post">
            <h2>Login</h2>
            <div>
                <label>Username:</label>
                <input type="email" name="username" required />
            </div>

            <div>
                <label>Password:</label>
                <input type="password" name="password" required />
            </div>

            <button type="submit">Login</button>
            <a href="/register" center>Register</a>
            <p id="successMsg">${message}</p>

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