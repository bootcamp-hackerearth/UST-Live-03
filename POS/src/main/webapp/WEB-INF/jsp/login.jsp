<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>POS | Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {
            height: 100vh;
        }

        .container {
            display: flex;
            height: 100vh;
        }

        /* LEFT PANEL */
        .left-panel {
            flex: 60%;
            background: linear-gradient(135deg, #0f766e, #022c43);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px;
        }

        .left-content {
            max-width: 400px;
        }

        .left-content h1 {
            font-size: 42px;
            margin-bottom: 15px;
        }

        .left-content p {
            margin-bottom: 20px;
            line-height: 1.5;
        }

        .features {
            list-style: none;
        }

        .features li {
            margin-bottom: 10px;
        }

        /* RIGHT PANEL */
        .right-panel {
            flex: 40%;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f1f5f9;
        }

        .login-card {
            width: 320px;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        .login-card h2 {
            text-align: center;
            margin-bottom: 15px;
        }

        .alert {
            background: #d1fae5;
            color: #065f46;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            text-align: center;
            font-size: 14px;
        }

        .login-card input {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border-radius: 6px;
            border: 1px solid #ccc;
        }

        .login-card button {
            width: 100%;
            padding: 12px;
            background: #0f766e;
            color: white;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            margin-top: 10px;
        }

        .login-card button:hover {
            background: #115e59;
        }

        .login-card p {
            text-align: center;
            margin-top: 15px;
            font-size: 14px;
        }

        .login-card a {
            color: #0f766e;
            text-decoration: none;
            font-weight: bold;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }

            .left-panel {
                display: none;
            }

            .right-panel {
                flex: 100%;
            }
        }
    </style>
</head>
<body>

<div class="container">

    <div class="left-panel">
        <div class="left-content">
            <h1>POS System</h1>
            <p>
                Manage your business efficiently with our powerful Point of Sale system.
                Track sales, monitor inventory, and gain insights in real time.
            </p>

            <ul class="features">
                <li>✔ Fast and secure billing</li>
                <li>✔ Real-time inventory tracking</li>
                <li>✔ Sales reports & analytics</li>
                <li>✔ User-friendly interface</li>
            </ul>
        </div>
    </div>

    <div class="right-panel">
        <div class="login-card">

            <h2>Login</h2>

            <%String logout = request.getParameter("logout");
                if ("true".equals(logout)) {%>
                <div class="alert">You have logged out successfully</div>
            <%}%>

            <form action="login" method="post">
                <input type="email" name="username" placeholder="Enter your email" required />
                <input type="password" name="password" placeholder="Enter your password" required />
                <button type="submit">Login</button>
            </form>

            <div>
            New User?
            <a href="${pageContext.request.contextPath}/register">Register here</a>
            </div>

        </div>
    </div>
</div>
</body>
</html>