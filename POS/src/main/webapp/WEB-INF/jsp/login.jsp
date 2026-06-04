<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        body {
            margin: 0;
            height: 100vh;
            font-family: 'Segoe UI', sans-serif;

            background: linear-gradient(135deg, #232526, #414345);

            display: flex;
            justify-content: center;
            align-items: center;
        }

        form {
            background: #ffffff;
            padding: 35px;
            width: 340px;

            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);

            animation: fadeIn 0.6s ease-in-out;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #333;
        }

        form div {
            margin-bottom: 18px;
        }

        label {
            font-size: 14px;
            color: #444;
            display: block;
            margin-bottom: 5px;
        }

        input {
            width: 100%;
            padding: 10px;

            border: 1px solid #ccc;
            border-radius: 8px;

            transition: 0.3s;
        }

        input:focus {
            outline: none;
            border-color: #4facfe;
            box-shadow: 0 0 5px #4facfe;
        }

        button {
            width: 100%;
            padding: 12px;

            background: linear-gradient(to right, #4facfe, #00f2fe);
            border: none;

            border-radius: 8px;
            font-size: 15px;
            color: white;
            cursor: pointer;

            transition: 0.3s;
        }

        button:hover {
            transform: scale(1.03);
            opacity: 0.9;
        }

        .register-link {
            text-align: center;
            margin-top: 15px;
        }

        .register-link p {
            margin-bottom: 5px;
            color: #aaa;
            font-size: 13px;
        }

        .register-link a {
            text-decoration: none;
            color: #4facfe;
            font-size: 14px;
            font-weight: 500;
        }

        .register-link a:hover {
            text-decoration: underline;
            color: #007bff;
        }
    </style>
</head>
<body>

<form action="${pageContext.request.contextPath}/login" method="post">
    <h2>Sign in</h2>
        <c:if test="${param.error != null}">
            <div style="
                margin-top:15px;
                padding:10px;
                background:#ffe5e0;
                color:#b91c1c;
                border-radius:8px;
                text-align:center;
                font-size:14px;">
                Invalid username or password
            </div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div style="
                margin-top:15px;
                padding:10px;
                background:#e6fffa;
                color:#065f46;
                border-radius:8px;
                text-align:center;
                font-size:14px;">
                Logged out successfully
            </div>
        </c:if>
    <div>
        <label>Username</label>
        <input type="text" name="username" required />
    </div>

    <div>
        <label>Password</label>
        <input type="password" name="password" required />
    </div>

    <button type="submit">Sign in</button>

    <div class="register-link">
        <p>New to the site?</p>
        <a href="${pageContext.request.contextPath}/register">
            Create your account
        </a>
    </div>

</form>
</body>
</html>