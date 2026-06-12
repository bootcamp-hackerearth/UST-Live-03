<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            height: 100vh;
            font-family: 'Segoe UI', Tahoma, sans-serif;

            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);

            display: flex;
            justify-content: center;
            align-items: center;
        }

        form {
            width: 370px;
            background: #ffffff;
            padding: 45px 40px;
            border-radius: 16px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 20px 40px rgba(0,0,0,0.10);
            position: relative;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            font-size: 26px;
            color: #111827;
            font-weight: 700;
            letter-spacing: 0.5px;
        }

        form div {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #4b5563;
        }

        input {
            width: 100%;
            padding: 13px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 14px;
            background: #f9fafb;
            transition: all 0.2s ease;
        }

        input:focus {
            outline: none;
            border-color: #6366f1;
            background: #ffffff;
            box-shadow: 0 0 0 4px rgba(99,102,241,0.15);
        }

        button {
            width: 100%;
            padding: 13px;
            margin-top: 5px;
            background: linear-gradient(135deg, #4f46e5, #2563eb);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.25s ease;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(79,70,229,0.25);
        }

        button:active {
            transform: scale(0.98);
        }

        .register-link {
            margin-top: 20px;
            text-align: center;
            font-size: 13px;
            color: #6b7280;
        }

        .register-link a {
            color: #4f46e5;
            font-weight: 600;
            text-decoration: none;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        .error-box {
            background: #fff1f2;
            color: #be123c;
            padding: 10px 12px;
            border-radius: 10px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 15px;
            border: 1px solid #fecdd3;
        }

        form::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            height: 6px;
            width: 100%;
            background: linear-gradient(90deg, #4f46e5, #2563eb);
            border-radius: 16px 16px 0 0;
        }
    </style>
</head>

<body>

<form action="/login" method="post">

    <h2>Login</h2>

    <c:if test="${param.error != null}">
        <div class="error-box">
            Invalid username or password
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

    <button type="submit">Login</button>

    <div class="register-link">
        New user?
        <a href="/register">Create account</a>
    </div>

</form>
</body>
</html>