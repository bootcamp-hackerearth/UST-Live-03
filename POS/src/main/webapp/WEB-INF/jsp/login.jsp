<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            background: #ffffff;
            padding: 30px 35px;
            width: 350px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
        }

        label {
            display: block;
            text-align: left;
            margin-bottom: 6px;
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
            border-radius: 6px;
            border: none;
            font-size: 15px;
            cursor: pointer;
        }

        .login-btn {
            background: #6f82e9;
            color: white;
        }

        .register-btn {
            margin-top: 10px;
            background: #6f82e9;
            color: white;
        }

        .divider {
            margin: 15px 0;
            color: #777;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="card">
       <c:if test="${param.error != null}">
           <div class="error-message">
               Invalid username or password
           </div>
       </c:if>

    <!-- LOGIN FORM -->
    <form action="/login" method="post">
        <h2>Login</h2>

        <label>Username</label>
        <input type="text" name="username" required>

        <label>Password</label>
        <input type="password" name="password" required>

        <button type="submit" class="login-btn">Login</button>
    </form>

    <div class="divider">or</div>

    <!-- REGISTER FORM -->
    <form action="/register" method="get">
        <button type="submit" class="register-btn">Sign-Up</button>
    </form>

</div>

</body>
</html>