<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg: #f4fdf6;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #1f8a38;

            --border: #e5e7eb;
            --radius: 14px;

            --shadow: 0 10px 25px rgba(0,0,0,0.06);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: var(--bg);
        }

        .login-card {
            width: 380px;
            background: var(--card);
            padding: 32px;
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            border-top: 4px solid var(--primary);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: var(--text);
            font-weight: 600;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-bottom: 6px;
            display: block;
        }

        .form-control {
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(40,167,69,0.15);
        }

        .btn-login {
            width: 100%;
            padding: 10px;
            background: var(--primary);
            border: none;
            color: #fff;
            font-weight: 600;
            border-radius: 10px;
            margin-top: 10px;
            transition: 0.2s ease;
        }

        .btn-login:hover {
            background: var(--primary-hover);
        }

        .btn-register {
            display: block;
            text-align: center;
            width: 100%;
            padding: 10px;
            margin-top: 12px;
            border-radius: 10px;
            border: 1px solid var(--primary);
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .btn-register:hover {
            background: var(--primary);
            color: white;
        }

        .divider {
            text-align: center;
            margin: 14px 0;
            font-size: 12px;
            color: var(--muted);
        }

        .alert {
            font-size: 13px;
            border-radius: 10px;
        }
    </style>

    <script>
        function validateLoginForm() {
            let username = document.getElementsByName("username")[0].value.trim();
            let password = document.getElementsByName("password")[0].value.trim();

            if (username === "" || password === "") {
                alert("Please fill in all fields.");
                return false;
            }

            let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;

            if (!username.match(emailPattern)) {
                alert("Enter a valid email address.");
                return false;
            }

            return true;
        }
    </script>
</head>

<body>

<div class="login-card">

    <h2>Login</h2>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger text-center py-2">
            ${errorMsg}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login"
          method="post"
          onsubmit="return validateLoginForm()">

        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="username" class="form-control" required />
        </div>

        <div class="mb-3">
            <label>Password</label>
            <input type="password" name="password" class="form-control" required />
        </div>

        <button type="submit" class="btn-login">Login</button>
    </form>

    <div class="divider">OR</div>

    <a href="${pageContext.request.contextPath}/register"
       class="btn-register">
        Create New Account
    </a>

</div>

</body>
</html>
